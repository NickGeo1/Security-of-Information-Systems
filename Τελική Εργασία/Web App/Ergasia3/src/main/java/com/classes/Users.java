package com.classes;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Every class is extending Users class.This class has the attributes and methods that every(or most) kind of user must have
 * Any user has a username, password, firstname, surname, age and is able to Login and Logout from the website
 */
public class Users
{
    // Basic characteristics of each user
    private String username, password, firstname, surname;
    private int age;

    //A common used string builder instance, in order to print the database results on many jsp pages
    private static StringBuilder HTML = new StringBuilder("");

    //variables for database management
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet rs;

    static Enumeration<String> attributes; //Enumeration list that contains user's session attribute names

    //main constructor
    public Users(String username, String password, String firstname, String surname, int age)
    {
        this.username  = username;
        this.password  = password;
        this.firstname = firstname;
        this.surname   = surname;
        this.age       = age;
    }

    /**
     * Provides an error HTML page with a title "Something went wrong!". A user can provide an error message to  specify
     * what went wrong during any process of registration or login.
     * @param response A response object to provide an HTML page.
     * @param reason An error message specifying what went wrong.
     * @param redirect_to The page we want to redirect the user after the error
     * @throws IOException
     */
    public static void Fail(HttpServletResponse response, String reason, String redirect_to) throws IOException
    {
        if (reason.isBlank())
            reason = "An unknown error occurred. Please try again.";

        PrintWriter writer = response.getWriter();

        // show an html web page back to the client
        writer.println("<!DOCTYPE html>");

        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Error during register</title>");
        writer.println("<meta http-equiv = \"refresh\" content = \"5; url = "+ redirect_to +" \" />"); // redirect page after 7 seconds
        writer.println("<link rel=\"stylesheet\" href=\"CSS/styles.css\">"); // use styles from the styles.css file
        writer.print("</head>");
        writer.println("<body>");
        writer.println("<form>");
        writer.println("<div class=\"imgcontainer\">");
        writer.println("<img src=\"img/logo1.png\" alt=\"logo_image\" class=\"avatar\">");
        writer.println("</div>");
        writer.println("<article>");
        writer.println("<h1> Something went wrong! </h1>");
        writer.println("<h3> " + reason + "</h3>");
        writer.println("</article>");
        writer.println("</form>");
        writer.println("</body>");

        writer.println("</html>");
    }

    /**
     * Registers a user. Preceding the injection, all fields are carefully processed and tested for duplicates in the database.
     * If patient's register is successful, he is being redirected to 'success.jsp' page and his data are being
     * stored into the database.If an admin register's a user successfully, he is being redirected to 'success.jsp' too.
     * For each register, the corresponding success message is being shown. For example, after a successful admin register
     * from another admin, 'success.jsp' shows : "Successfully added an admin".
     * Register's behavior depends on the object type that calls it.
     *
     * @param request A Servlet request required to get session attributes and redirect the user with Request Dispatcher on the corresponding page with the corresponding message
     * @param response A Servlet response required to provide error information.
     * @param dataSource A Datasource to inject SQL statements into.
     * @param register_page The page to redirect the user after the register
     * @throws IOException if anything goes wrong with the HttpServletResponse.
     */
    public void Register(HttpServletRequest request, HttpServletResponse response, DataSource dataSource, String register_page) throws IOException
    {
        //Checks for all the fields. We use Users.Fail() to provide a plain-text HTML page to print any errors.

        if (!this.getUsername().matches("[A-Za-z0-9]{1,12}"))
        {
            Fail(response, "Invalid Username! The username length must be between 1 and 12 characters. Only alphabetic and numeric characters are allowed",register_page);
            return;
        }

        else if (this.getPassword().length() < 4)
        {
            Fail(response, "Provide a password with at least 4 characters.",register_page);
            return;
        }

        else if (!this.getFirstname().matches("[A-Z][a-z]+"))
        {
            Fail(response, "Invalid Firstname! All first/last names must start with one capital letter with succeeding lowercase letters. No other characters, other than letters, are allowed.",register_page);
            return;
        }

        else if (!this.getSurname().matches("[A-Z][a-z]+"))
        {
            Fail(response, "Invalid Lastname! All first/last names must start with one capital letter with succeeding lowercase letters. No other characters, other than letters, are allowed.",register_page);
            return;
        }

        else if (this.getAge() > 119 || this.getAge() <= 0)
        {
            Fail(response, "Invalid Age! A registered age cannot be greater than 119 years or a non-positive number.",register_page);
            return;
        }

        else if (this instanceof Patient && !((Patient)this).getAMKA().matches("[0-9]{11}") ||
                this instanceof Doctor  && !((Doctor)this).getAMKA().matches("[0-9]{11}"))
        {

            Fail(response, "Invalid AMKA! A social security number must have exactly 11 digits.",register_page);
            return;
        }

        else if (this instanceof Doctor && !((Doctor)this).getSpeciality().equals("Pathologist") &&
                !((Doctor)this).getSpeciality().equals("Ophthalmologist") && !((Doctor)this).getSpeciality().equals("Orthopedist"))
        {
            Fail(response, "Doctors can have only one of the following specialities: Pathologist, Ophthalmologist and Orthopedist",register_page);
            return;
        }

        //if we get to this point it means none of the fields are incorrect. we can execute sql statements safely.
        //checking for duplicates in the database

        try
        {
            //Check if there are any duplicates in the database what comes to AMKA and username.
            //preparing an sql statement
            connection = dataSource.getConnection();
            String selectquery, insertquery;
            Integer age = this.getAge();
            String username = this.getUsername(), password = this.getPassword(), name = this.getFirstname(), surname = this.getSurname();
            String user; //this variable specifies the type of user who just registered
            String salt;

            if(this instanceof Patient) //If a Patient object calls the register method
            {
                //check if there is any other patient who has the same given AMKA or username
                selectquery = "SELECT * FROM patient WHERE patientAMKA=? OR username=?";
                statement = connection.prepareStatement(selectquery);
                statement.setString(1, ((Patient)this).getAMKA());
                statement.setString(2, username);

                rs = statement.executeQuery();

                if (rs.next()) //if there is any, we call the Fail function with the corresponding reason
                {
                    Fail(response, "This username/AMKA is already taken!", register_page);
                    rs.close();
                    connection.close();
                    return;
                }

                //check if there is any doctor who has the same given AMKA or username
                statement = connection.prepareStatement("SELECT * FROM doctor WHERE doctorAMKA=? OR username=?");
                statement.setString(1, ((Patient)this).getAMKA());
                statement.setString(2, username);

                rs = statement.executeQuery();

                if (rs.next()) //if there is any, we call the Fail function with the corresponding reason
                {
                    Fail(response, "This username/AMKA is already taken!", register_page);
                    rs.close();
                    connection.close();
                    return;
                }

                //check if there is any admin who has the same given username
                statement = connection.prepareStatement("SELECT * FROM admin WHERE username=?");
                statement.setString(1, username);

                rs = statement.executeQuery();

                if (rs.next()) //if there is any, we call the Fail function with the corresponding reason
                {
                    Fail(response, "This username/AMKA is already taken!", register_page);
                    rs.close();
                    connection.close();
                    return;
                }

                //at this point, no user has the given AMKA or username so we proceed with patient's register
                salt = createSalt(response); //create a random salt to be added to the new patient's password

                //Insert the new patient to the database
                insertquery = "INSERT INTO patient (username,hashedpassword,name,surname,age,patientAMKA,salt) VALUES (?,?,?,?,?,?,?)";
                statement = connection.prepareStatement(insertquery);
                statement.setString(1, username);
                statement.setString(2, hashPassword(password, salt)); //we store the hashed salted password into the database
                statement.setString(3, name);
                statement.setString(4, surname);
                statement.setString(5, age.toString()); //age, as a parameter is an Integer (not an int), so we convert it instantly to string.
                statement.setString(6, ((Patient)this).getAMKA());
                statement.setString(7, salt);

                statement.execute(); //execute statement
                user="Patient"; //set user as patient
            }
            else if(this instanceof Doctor) //If a Doctor object(via Admin class) calls the register method
            {
                //check if there is any other doctor who has the same given AMKA or username
                selectquery = "SELECT * FROM doctor WHERE doctorAMKA=? OR username=?";
                statement = connection.prepareStatement(selectquery);
                statement.setString(1, ((Doctor)this).getAMKA());
                statement.setString(2, username);

                rs = statement.executeQuery();

                if (rs.next()) //if there is any, we call the Fail function with the corresponding reason
                {
                    Fail(response, "This username/AMKA is already taken!", register_page);
                    rs.close();
                    connection.close();
                    return;
                }

                //check if there is any patient who has the same given AMKA or username
                statement = connection.prepareStatement("SELECT * FROM patient WHERE patientAMKA=? OR username = ?");
                statement.setString(1, ((Doctor)this).getAMKA());
                statement.setString(2, username);

                rs = statement.executeQuery();

                if (rs.next()) //if there is any, we call the Fail function with the corresponding reason
                {
                    Fail(response, "This username/AMKA is already taken!", register_page);
                    rs.close();
                    connection.close();
                    return;
                }

                //check if there is any admin who has the same given username
                statement = connection.prepareStatement("SELECT * FROM admin WHERE username=?");
                statement.setString(1, username);

                rs = statement.executeQuery();

                if (rs.next()) //if there is any, we call the Fail function with the corresponding reason
                {
                    Fail(response, "This username/AMKA is already taken!", register_page);
                    rs.close();
                    connection.close();
                    return;
                }

                //at this point, no user has the given AMKA or username so we proceed with patient's register
                salt = createSalt(response); //create a random salt to be added to the new doctor's password

                //Insert the new doctor to the database
                insertquery = "INSERT INTO doctor (username,hashedpassword,name,surname,age,doctorAMKA,specialty,ADMIN_username,salt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(insertquery);
                statement.setString(1, username);
                statement.setString(2, hashPassword(password, salt));  //we store the hashed salted password into the database
                statement.setString(3, name);
                statement.setString(4, surname);
                statement.setString(5, age.toString());       //age, as a parameter is an Integer (not an int), so we convert it instantly to string.
                statement.setString(6, ((Doctor)this).getAMKA());
                statement.setString(7, ((Doctor)this).getSpeciality());

                //when an admin tries to add a doctor, we have to store which admin made that doctor in database
                //We can get admin's username from the session attribute "adminusername"
                statement.setString(8, request.getSession().getAttribute("adminusername").toString());
                statement.setString(9, salt);

                statement.execute();  //execute statement
                user="Doctor";  //set user as doctor
            }
            else //Else, an Admin object(via Admin method) calls the register method
            {
                //check if there is any other user who has the given username
                selectquery = "SELECT * FROM admin,doctor,patient WHERE admin.username=? OR doctor.username = ? OR patient.username = ?";
                statement = connection.prepareStatement(selectquery);
                statement.setString(1, this.getUsername());
                statement.setString(2, this.getUsername());
                statement.setString(3, this.getUsername());

                rs = statement.executeQuery();

                //if there is any, we call the Fail function with the corresponding reason
                if (rs.next())
                {
                    Fail(response, "This username is already taken!", register_page);
                    rs.close();
                    connection.close();
                    return;
                }

                //at this point, no admin has the given username so we proceed with admin's register
                salt = createSalt(response);  //create a random salt to be added to the new doctor's password

                //insert the new admin into the database
                insertquery = "INSERT INTO admin (username,hashedpassword,name,surname,age,salt) VALUES (?, ?, ?, ?, ?, ?)";
                statement = connection.prepareStatement(insertquery);

                statement.setString(1, username);
                statement.setString(2, hashPassword(password, salt));  //we store the hashed salted password into the database
                statement.setString(3, name);
                statement.setString(4, surname);
                statement.setString(5, age.toString()); //age, as a parameter is an Integer (not an int), so we convert it instantly to string.
                statement.setString(6, salt);

                statement.execute();  //execute statement
                user="Administrator";  //set user as administrator
            }

            //Specify the parameters to be passed on "success.jsp". Parameters depending on the user that just registered
            request.setAttribute("action", "added a new " + user);
            request.setAttribute("redirect",register_page);

            //Make request dispatcher object and forward the user to "success.jsp"
            RequestDispatcher rd = request.getRequestDispatcher("success.jsp");
            rd.forward(request, response);

            rs.close();
            connection.close();  //close connection and result set
        }
        catch (Exception exception)
        {
            //if anything goes wrong it'll be printed on the user's screen.
            Fail(response, "Cannot insert data. Exception message: \n" + exception.getMessage(), register_page);
            exception.printStackTrace();
        }

    }

    /**
     * Logs in a user, specified from the login page with all the specified credentials.
     *
     * Makes a connection with the database, it is
     * searching the user's data from it,
     * initializes the user's session attributes,
     * and redirects him to the corresponding page.
     *
     * If the user is not found(wrong credentials), it redirects him to the 'fail.jsp' page.
     *
     * @param type The type of user to be logged in. (Patient/Doctor/Admin)
     * @param request An HTTPServletRequest to acquire the given username and password.
     * @param response An HTTPServletResponse to redirect the user accordingly.
     * @param datasource The datasource required to search the username and password.
     */
    public static void Login(String type, HttpServletRequest request, HttpServletResponse response, DataSource datasource) throws IOException
    {
        HttpSession user_session = request.getSession();  //get session object

        String name = request.getParameter("username");
        String pass = request.getParameter("password");  //get the username and password the user entered in login form
        String table; //variable that specifies the table we are going to search records from, depending on the user who tries to login

        //Check username format server side during login, before taking any action
        if (!name.matches("[A-Za-z0-9]{1,12}"))
        {
            response.sendRedirect("fail.html");
            return;
        }

        try
        {
            //establishing a connection to the database.
            connection = datasource.getConnection();

            //specifying the type of user to log on(doctor,patient or admin).
            table = type.toLowerCase();

            //preparing a general statement.
            statement = connection.prepareStatement("SELECT * FROM `"+ table +"` WHERE username=?");
            statement.setString(1, name);

            rs = statement.executeQuery();

            //if this username exists and the password is correct. To check if password is correct, we take the salt that corresponds
            //to the given username, and we hash the given password with that salt. If the result hash is equal to the database hash,
            //login is successful.
            if(rs.next() && hashPassword(pass, rs.getString("salt")).equals(rs.getString("hashedpassword")))
            {
                //We have to clear session attributes in case a user tries to login while another is already logged on
                String previous_attribute;

                while(attributes != null && attributes.hasMoreElements())
                {
                    previous_attribute = (String) attributes.nextElement();
                    user_session.removeAttribute(previous_attribute);
                }

                //In every user login case, we initialize the corresponding session attributes and we redirect him to the corresponding page
                switch (type)
                {
                    case "Patient":
                        user_session.setAttribute("patientusername" , rs.getString("username"));
                        user_session.setAttribute("name", rs.getString("name"));
                        user_session.setAttribute("surname", rs.getString("surname"));
                        user_session.setAttribute("age", rs.getString("age"));
                        user_session.setAttribute("patientAMKA", rs.getString("patientAMKA"));

                        response.sendRedirect("patient_main_environment.jsp");
                        break;

                    case "Doctor":
                        user_session.setAttribute("doctorusername" , rs.getString("username"));
                        user_session.setAttribute("name", rs.getString("name"));
                        user_session.setAttribute("surname", rs.getString("surname"));
                        user_session.setAttribute("age", rs.getString("age"));
                        user_session.setAttribute("specialty", rs.getString("specialty"));
                        user_session.setAttribute("doctorAMKA", rs.getString("doctorAMKA"));

                        response.sendRedirect("doctor_main_environment.jsp");
                        break;

                    case "Admin":
                        user_session.setAttribute("adminusername" , rs.getString("username"));
                        user_session.setAttribute("name", rs.getString("name"));
                        user_session.setAttribute("surname", rs.getString("surname"));
                        user_session.setAttribute("age", rs.getString("age"));

                        response.sendRedirect("admin_main_environment.jsp");
                        break;
                }

                attributes = user_session.getAttributeNames(); //store attributes in Enumeration type object
            }
            else //wrong credentials
            {
                response.sendRedirect("fail.html");
            }

            rs.close();
            connection.close(); //close connection and result set
        }
        catch(Exception e) //in case of exception, print message to the console
        {
            System.out.println("An exception occured during database connection: "+e.toString());
        }
    }

    /**
     * Terminates access from the user logged on and ends the session from the User that logged out.
     */
    public static void Logout(HttpServletResponse response, HttpServletRequest request) throws IOException
    {
        HttpSession session = request.getSession();  //get session object

        String attribute;

        while (attributes.hasMoreElements())   //remove all the attributes attached to the session object
        {
            attribute = (String) attributes.nextElement();
            session.removeAttribute(attribute);
        }

        session.invalidate(); //invalidate session
        response.sendRedirect("login.jsp");  //redirect to "login.jsp"
    }

    /**
     * Cancels a scheduled appointment between a patient and a doctor, with the given parameters, after the "Cancel" button is being pressed.
     * Makes a connection with the database and sets patientAMKA=0 to the canceled appointment, in
     * order to make it available for other patients. Note that cancelation fails, if a user tries
     * to cancel an appointment that is scheduled less than 3 days after the day he tries to cancel it.
     *
     * @param date The date of the appointment to be canceled
     * @param pAMKA The patient AMKA that specifies the patient of the appointment to be canceled
     * @param dAMKA The doctor AMKA that specifies the doctor of the appointment to be canceled
     * @param start The starting hour of the appointment
     * @param request The HttpServletRequest object to get session attributes from
     * @param response The HttpServletResponse object to redirect the user back on the cancelation page and print messages to the page
     * @param datasource The Datasource object required to connect to the database
     */
    public static void cancelScheduledAppointment(String date, String pAMKA, String dAMKA, String start, HttpServletRequest request, HttpServletResponse response, DataSource datasource) throws IOException
    {
        Date now = new Date(); //today's date
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date appointment_date = null;

        try
        {
            appointment_date = df.parse(date); //appointment date in dd-MM-yyyy format

            Calendar cal =  Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_MONTH, 3);
            Date nowplus3 = cal.getTime(); //current date + 3 days

            String nowplus3str = df.format(nowplus3);
            nowplus3 = df.parse(nowplus3str); //convert (current date + 3 days) to the correct form(dd-MM-yyyy)

            if(nowplus3.after(appointment_date)) //if (current date + 3 days) is after appointment date, cancelation cannot be done
            {
                String redirect;

                //if patientAMKA session attribute is not null, that means a patient is logged on, so we redirect him to "ScheduledAppointments.jsp"
                if(request.getSession().getAttribute("patientAMKA") != null)
                    redirect = "ScheduledAppointments.jsp";
                //otherwise, a doctor is logged on, so we redirect him to "doctor_view_appointments.jsp"
                else
                    redirect = "doctor_view_appointments.jsp";

                //show fail message and redirect
                Fail(response, "You cannot cancel an appointment that is scheduled in less than 3 days from now", redirect);
                return;
            }

            //If we reach this point, that means that cancelation can be done.
            //Connect to the database and set PATIENT_patientAMKA=0 on the desired appointment
            //at appointment table, in order to make that appointment available for other patients
            connection = datasource.getConnection();
            statement = connection.prepareStatement("UPDATE appointment SET PATIENT_patientAMKA=0 WHERE date = ? AND PATIENT_patientAMKA = ? AND DOCTOR_doctorAMKA = ? AND startSlotTime=?");
            date = changeDateFormat("dd-MM-yyyy","yyyy-MM-dd",date); //In database, dates are being stored in yyyy-MM-dd format, so we have to convert the given date format first
            statement.setString(1, date);
            statement.setString(2, pAMKA);
            statement.setString(3, dAMKA);
            statement.setString(4, start);
            statement.execute();
            connection.close();

            //if patientAMKA session attribute is not null, that means a patient is logged on, so we redirect him to "ScheduledAppointments.jsp"
            if(request.getSession().getAttribute("patientAMKA") != null)
                response.sendRedirect("ScheduledAppointments.jsp");
            //otherwise, a doctor is logged on, so we redirect him to "doctor_view_appointments.jsp"
            else
                response.sendRedirect("doctor_view_appointments.jsp");
        }
        catch(ParseException e) //Show parse exception
        {
            PrintWriter exc = response.getWriter();
            exc.println("Parse exception during date parsing: "+e.toString());
        }
        catch(Exception e)//Show other exception
        {
            PrintWriter showhtml = response.getWriter();
            showhtml.println(e.toString());
        }
    }

    /**
     * Returns an html row in string format with the attributes that passed as parameters
     *
     * @param table_case an integer from 0 to 3, that specifies the output.
     * More specifically:
     * 0 corresponds to a patient's appointment history output
     * 1 corresponds to a patient's scheduled appointments output(A Cancel button appears)
     * 2 corresponds to a patient's search of available appointments output(A Book button appears)
     * 3 corresponds to a doctor's scheduled appointments output(A Cancel button appears)
     * @param date appointment's date
     * @param startSlotTime appointment's startSlotTime
     * @param endSlotTime appointment's endSlotTime
     * @param user_AMKA a user's AMKA (Doctor's or Patient's)
     * @param Doctor_specialty The doctor's specialty
     * @param user_name The user's firstname
     * @param user_surname The user's lastname
     * @return string format of html row
     */
    public static String createTableRow(int table_case, String date, String startSlotTime, String endSlotTime, String user_AMKA, String Doctor_specialty, String user_name, String user_surname)
    {
        StringBuilder tablerow = new StringBuilder(); //string builder object to store the html output

        tablerow.append("<tr>");
        //show the all-case-common column values
        tablerow.append("<td>" + date + "</td>");
        tablerow.append("<td>" + startSlotTime + "</td>");
        tablerow.append("<td>" + endSlotTime + "</td>");
        tablerow.append("<td>" + user_AMKA + "</td>");
        tablerow.append("<td>" + user_name + "</td>");
        tablerow.append("<td>" + user_surname + "</td>");

        //if the logged on user is not a doctor, show the doctor's specialty on patient appointments
        if(table_case != 3)
            tablerow.append("<td>" + Doctor_specialty + "</td>");

        //if we want to show the scheduled appointments, we have to include a cancel button.The button's function depends on the logged on user and on the appointment that it is next to
        if(table_case == 1 || table_case == 3)
            tablerow.append("<td><button style=\"width:60px;\" type=\"button\" onclick=\"" + (table_case == 1 ? "setvalue(7)" : "document.getElementById('doctor_action').value = 'cancel'") +"; cancelappointment('"+date+"','"+startSlotTime+"','"+user_AMKA+"');\">Cancel</button></td>");

        //if a patient tries to book an appointment, we have to include a book button
        else if(table_case == 2)
            tablerow.append("<td><button type=\"button\" onclick=\"setvalue(8); bookappointment('"+date+"','"+startSlotTime+"','"+endSlotTime+"','"+user_AMKA+"');\">Book</button></td>");

        tablerow.append("</tr>");

        return tablerow.toString(); //return the html string
    }

    /**
     * Takes a date in string format as a parameter and converts it from it's old format to the new one
     *
     * @param oldformat the old format of the date
     * @param newformat the new format of the date
     * @param date the date in string format
     * @return the given date at it's new format(as string)
     * @throws ParseException
     */
    public static String changeDateFormat(String oldformat, String newformat, String date) throws ParseException
    {
        SimpleDateFormat df = new SimpleDateFormat(oldformat);
        df.setLenient(false); //Added this to avoid parsing dates like: 32-12-2001 which converts to 01-01-2002
        Date d = df.parse(date);
        df.applyPattern(newformat);
        date = df.format(d);

        return date;
    }

    /**
     * Takes the password and the salt as parameter and returns the hashed salted password
     *
     * @param password The given password to be salted and hashed
     * @param salt The salt to include in password
     * @return The hashed salted password in Uppercase
     */
    private static String hashPassword(String password, String salt)
    {
        // Hash the password.
        final String toHash = salt + password + salt;
        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException ex)
        {
            return "00000000000000000000000000000000";
        }
        messageDigest.update(toHash.getBytes(), 0, toHash.length());
        String hashed = new BigInteger(1, messageDigest.digest()).toString(16);
        if (hashed.length() < 32)
        {
            hashed = "0" + hashed;
        }
        return hashed.toUpperCase();
    }

    /**
     *
     * Returns a random 20byte string called "salt"
     *
     * @param response The response object to use and write the exception in order something goes wrong
     * @return The salt in string format or an empty string if something goes wrong
     */
    private static String createSalt(HttpServletResponse response)
    {
        PrintWriter writer = null;

        try
        {
            writer = response.getWriter();
            SecureRandom random = new SecureRandom();

            byte bytes[]= new byte[20];
            random.nextBytes(bytes); //put 20 random bytes on bytes array

            //Check if any byte is negative and if it is, make it positive
            for(int i = 0; i< bytes.length; i++)
            {
                if(bytes[i] < 0)
                    bytes[i] = (byte) -bytes[i];
            }

            return new String(bytes, "UTF-8"); //Translate the bytes of the array to a UTF-8 string and return it

        }
        catch(UnsupportedEncodingException e) //in UnsupportedEncodingException case, print the exception message on page
        {
            writer.println("UnsupportedEncodingException: "+e.toString());
            return "";
        }
        catch (IOException e) //in IOException case, print the exception message on console
        {
            System.out.println("IOException: "+e.toString());
            return "";
        }
    }

    //getters
    public String getUsername() {
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getSurname() {
        return surname;
    }

    public int getAge() {
        return age;
    }

    /**
     * Makes a connection with the database and with a nested query, it counts all the users stored on it
     *
     * @return The number of users registered or an error message if something goes wrong
     */
    public static String getUsersCount()
    {
        try
        {
            InitialContext ctx = new InitialContext();
            DataSource datasource = (DataSource)ctx.lookup("java:comp/env/jdbc/LiveDataSource");

            connection = datasource.getConnection();
            statement = connection.prepareStatement("SELECT count(*) + (SELECT count(*) + (SELECT count(*) from patient) from admin) AS users from doctor");
            rs = statement.executeQuery();
            rs.next();

            return rs.getString("users");
        }
        catch(Exception e)
        {
            return "An error has occured during users counting";
        }
    }

    public static StringBuilder getHTML()
    {
        return HTML;
    }

    // setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public static void setHTML(StringBuilder html)
    {
        HTML = html;
    }

    public void setAge(int age)
    {
        if (age > 0 && age < 119)
            this.age = age;
    }

    public static void clearHTML()
    {
        HTML.setLength(0);
    }
}
