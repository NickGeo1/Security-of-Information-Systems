package com.classes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;


/**
 * This is the model of a patient.A patient is able to register to the website,search for an available appointment and
 * see his scheduled or past appointments. Except of User's attributes, each patient has also his unique AMKA.
 */
public class Patient extends Users
{
    private final String AMKA; // This is the unique AMKA of each patient

    //variables for database management
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet rs;

    //Patient constructor
    public Patient(String username, String password, String firstname, String lastname, int age, String AMKA)
    {
        super(username, password, firstname, lastname, age);
        this.AMKA = AMKA;
    }

    /**
     *
     * Returns a ResultSet object that contains the results of a patient's desired appointments.
     * Using the class's global Connection object, makes a connection with database, then, initializes
     * the query depending on the parameters, creates the statement and then the result set.
     *
     * @param showby     The doctor attribute to search appointments by
     * @param value      The actual value of the above attribute
     * @param category   The category of the appointment(history or scheduled)
     * @param session    Session object required to get patient's session attributes
     * @return The ResultSet object that contains the results of a patient's desired appointments
     * @throws SQLException
     * @throws ParseException
     */
    private static ResultSet createResultSet(String showby, String value, String category, HttpSession session) throws SQLException, ParseException
    {
        //Query that selects all the required information of the appointments(using < operator for history appointments and > for scheduled).
        String query = "SELECT date,startSlotTime,endSlotTime,DOCTOR_doctorAMKA,specialty,name,surname " +
                "FROM appointment JOIN doctor ON DOCTOR_doctorAMKA = doctorAMKA " +
                "WHERE PATIENT_patientAMKA = ? AND (date "+(category.equals("history") ? "<":">") +" cast(now() as date) " +
                "OR date = cast(now() as date) AND "+(category.equals("history") ? "endSlotTime <":"startSlotTime >") +" cast(now() as time))";

        //depending on the showby value we add one more constraint on the query.
        //If the given value is in wrong format do not execute query
        switch (showby)
        {
            case "Doctor AMKA":

                if (!value.matches("[0-9]{11}"))
                    throw new ParseException("Invalid AMKA", 0); //in case of invalid AMKA format, we throw a parse exception

                query += " AND DOCTOR_doctorAMKA = ?"; //Add doctor AMKA constraint
                break;

            case "Date (dd-MM-yyyy)":
                //in case we want to search by date, we have to change its format because the format is different in the database

                //checks if user gave date in the form: 1-2 digits - 1-2 digits - 1-4 digits
                if(!value.matches("[0-3]?[0-9]-[0-1]?[0-9]-[1-9][0-9]{0,3}"))
                    throw new ParseException("Invalid date", 0); //in case of invalid date format, we throw a parse exception

                //During date format change, we are doing further checking for the given format
                //because dates like: 39-19-2001 have to be rejected
                value = changeDateFormat("dd-MM-yyyy", "yyyy-MM-dd", value); //in case of invalid date format, we throw a parse exception
                System.out.println(value);

                query += " AND date = ?"; //Add date constraint
                break;

            //fixed values are being taken for specialty from html page, so we dont have to check format here
            case "Specialty":
                query += " AND specialty = ?"; //Add doctor specialty constraint
                break;
        }

        //A prepared statement object, on which we are going to store our sql statement
        PreparedStatement statement = connection.prepareStatement(query);

        //set the first query parameter equal to the patient's AMKA
        statement.setString(1, session.getAttribute("patientAMKA").toString());

        //if we dont want to show all the history appointments, we have to add the doctor attribute value parameter to query
        if (!showby.equals("Show all"))
            statement.setString(2, value);

        //Returning the result set that contains the results.
        return statement.executeQuery();
    }

    /**
     * Returns an html table in StringBuilder format that contains the patient's desired appointment results.
     * We inject each row result from rs, into each table row
     *
     * @param table_case The specified case for each table row(see create createTableRow method)
     * @param rs The result set object that contains the sql statement results
     * @return The html table of the results in StringBuilder format
     * @throws SQLException
     * @throws ParseException
     */
    private static StringBuilder createHtmlTable(int table_case, ResultSet rs) throws SQLException, ParseException
    {
        //In this html variable, we append each row of the html table we want to return

        StringBuilder html = new StringBuilder(  //append the table headers first
                "<table>"
                        + "<tr>"
                        + "<th>Date</th>"
                        + "<th>Start time</th>"
                        + "<th>End time</th>"
                        + "<th>Doctor AMKA</th>"
                        + "<th>Doctor name</th>"
                        + "<th>Doctor surname</th>"
                        + "<th>Doctor specialty</th>"
                        + "</tr>");

        String date;
        String startSlotTime;
        String endSlotTime;
        String DOCTOR_doctorAMKA;
        String Doctor_specialty;
        String Doctor_name;
        String Doctor_surname;
        String htmlRow;

        //For each result set row, store the column values into variables and append on html variable the html row
        //created from these variables
        do
        {
            date = rs.getString("date");

            //change the date to the correct format before storing it into the variable(database holds date in yyyy-MM-dd format)
            date = changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", date);
            startSlotTime = rs.getString("startSlotTime");
            endSlotTime = rs.getString("endSlotTime");
            DOCTOR_doctorAMKA = rs.getString("DOCTOR_doctorAMKA");
            Doctor_specialty = rs.getString("specialty");
            Doctor_name = rs.getString("name");
            Doctor_surname = rs.getString("surname");

            //Get table row for appointment
            htmlRow = createTableRow(table_case, date, startSlotTime, endSlotTime, DOCTOR_doctorAMKA, Doctor_specialty, Doctor_name, Doctor_surname);
            //append on html variable the above row
            html.append(htmlRow);

        } while (rs.next());

        html.append("</table>");

        return html; //return the created html table
    }

    /**
     *
     * Searching for available appointments between a given time interval, specified(or not) by a doctor attribute and the attribute value
     *
     * Makes a html table which contains, on each row, the details about the doctor who is available on a specific date and time,
     * followed by an interactive "Book" button.
     * Each row contains the following information: the date, the start time and the ending time of the appointment,
     * the doctor's AMKA and the doctor's firstname, lastname and specialty.In case a booking is successful, a success
     * message appears. In any other case(ex. no results found) a corresponding message appears, with the help of Fail function.
     *
     * @param start_date The start date of the desired search interval
     * @param end_date The end date of the desired search interval
     * @param searchby   The doctor attribute to search available appointments by
     * @param value      The actual value of the above attribute
     * @param response   A Servlet response object required to redirect user to another page.
     * @param datasource A Datasource to inject SQL statements into.
     * @throws IOException
     */
    public static void searchAvailableAppointments(String start_date, String end_date, String searchby, String value, HttpServletResponse response, DataSource datasource) throws IOException
    {

        StringBuilder html = new StringBuilder(""); //a string builder object to store the html content we want to show on "AvailableDoctorAppointments.jsp"

        try
        {
            connection = datasource.getConnection(); //connection object for database connection

            //This is our default query, in case we want to search all the available appointments
            //Query selects all the required data from appointment and doctor tables, from records that
            //have patientAMKA = 0(available) and date between the date interval the patient gave.
            //Note that dates are being passed in yyyy-MM-dd format so we dont have to change their format
            //in order to search them in database
            String query = "SELECT date,startSlotTime,endSlotTime,DOCTOR_doctorAMKA,specialty,name,surname " +
                    "FROM appointment JOIN doctor ON DOCTOR_doctorAMKA = doctorAMKA " +
                    "WHERE PATIENT_patientAMKA = 0 AND date BETWEEN ? AND ?";

            switch (searchby)  //depending on the searchby value we add the corresponding constraint to our query
            {
                case "Doctor AMKA": //in case we want to search by Doctor AMKA

                    //if AMKA has invalid format, set the corresponding message on HTML object, close connection and return
                    if (!value.matches("[0-9]{11}"))
                    {
                        html.append("An AMKA must be 11 non negative integers");
                        setHTML(html);
                        connection.close();
                        return;
                    }

                    //add to query the corresponding doctorAMKA constraint
                    query += " AND DOCTOR_doctorAMKA = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, start_date);
                    statement.setString(2, end_date);
                    statement.setString(3, value);
                    break;

                case "Full name": //in case we want to search by Full name

                    //in this case, value contains the first name and the last name splitted by space
                    //we store the firstname and the last name seperately on names array
                    String[] names = value.split(" ");

                    //Check given username format. If its invalid, do not proceed
                    if(!names[0].matches("[A-Z][a-z]+") || !names[1].matches("[A-Z][a-z]+"))
                    {
                        html.append("Invalid Full name! All first/last names must start with one capital letter with succeeding lowercase letters. No other characters, other than letters, are allowed.");
                        setHTML(html);
                        connection.close();
                        return;
                    }

                    //add to query the corresponding name and surname constraint
                    query += " AND (name = ? OR surname=?)";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, start_date);
                    statement.setString(2, end_date);
                    statement.setString(3, names[0]);
                    statement.setString(4, names[1]);
                    break;

                case "Specialty": //in case we want to search by Specialty

                    //add to query the corresponding specialty constraint
                    query += " AND specialty = ?";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, start_date);
                    statement.setString(2, end_date);
                    statement.setString(3, value);
                    break;

                default: //default case is when we want to search all the available appointments, so we dont change the query
                    statement = connection.prepareStatement(query);
                    statement.setString(1, start_date);
                    statement.setString(2, end_date);
            }

            rs = statement.executeQuery(); //execute query

            if (rs.next()) //in case there is at least one record, store on html variable the table result for available appointments
                html = createHtmlTable(2, rs);
            else if (!rs.next() && searchby.equals("Show all")) //if there is not any results on "Show all category"
            {
                //change from database date format to dd-MM-yyyy format in order to show the fail message to user
                start_date = changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", start_date);
                end_date = changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", end_date);

                //append the message on html variable
                html.append("There is not any appointment available on interval " + start_date + " through " + end_date);
            }
            else  //In this case, there is not any record on the results but the option wasn't 'Show all'.
            {     //That means there is not any results JUST for the restrictions we had set

                //change from database date format to dd-MM-yyyy format in order to show the fail message to user
                start_date = changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", start_date);
                end_date = changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", end_date);

                //append the message on html variable
                html.append("No results found for interval " + start_date + " through " + end_date + " and " + searchby + " " + value);
            }

            rs.close();
            connection.close(); //close ResultSet and Connection

        }
        catch (ArrayIndexOutOfBoundsException e) //In this Exception case, user didnt enter the doctor's first name and last name correct
        {
            html.append("Invalid firstname/lastname format");
        }
        catch (Exception e) //in other Exception cases, print message on console
        {
            System.out.println(e.toString());
        }
        finally //in any case, set the HTML value equal to html value. HTML is being shown in "AvailableDoctorAppointments.jsp"
        {
            setHTML(html); //Set HTML equal to html. HTML content is being shown at "AvailableDoctorAppointments.jsp"
            response.sendRedirect("AvailableDoctorAppointments.jsp"); //redirect the user back
        }
    }

    /**
     *
     * Books the selected appointment after "Book" button is being pressed.
     * In database level, this method just set's the desired appointment's
     * Patient AMKA from value 0 to value equal to the patient's AMKA
     * Appointment is specified by the parameters
     *
     * @param date Date of the appointment patient wants to book
     * @param start Starting hour of the appointment patient wants to book
     * @param end Ending hour of the appointment patient wants to book
     * @param dAMKA DoctorAMKA that specifies the doctor of the appointment patient wants to book
     * @param response Servlet response object to redirect the user back in "AvailableDoctorAppointments.jsp" or to print a message on page
     * @param request Servlet request object to get the patient's session attributes
     * @param datasource Datasource object required to access database
     * @throws IOException
     */
    public static void bookAppointment(String date, String start, String end, String dAMKA, HttpServletResponse response, HttpServletRequest request, DataSource datasource) throws IOException
    {

        HttpSession session = request.getSession(); //get session object

        try
        {
            connection = datasource.getConnection();

            //Update the corresponding appointment AMKA and set it equal to patient's AMKA(appointment booked).
            //We can specify the appointment, by the given parameters
            statement = connection.prepareStatement("UPDATE appointment SET PATIENT_patientAMKA = ? WHERE date = ? AND startSlotTime=? AND endSlotTime=? AND DOCTOR_doctorAMKA=?");
            statement.setString(1, session.getAttribute("patientAMKA").toString());
            statement.setString(2, changeDateFormat("dd-MM-yyyy", "yyyy-MM-dd", date));
            statement.setString(3, start);
            statement.setString(4, end);
            statement.setString(5, dAMKA);
            statement.execute();
            connection.close();

            StringBuilder html = new StringBuilder("Thank you for using our web application to book your appointment " + session.getAttribute("name") +
                    "!\nYour appointment has been booked on " + date + " at " + start + " until " + end + "(Doctor's AMKA: " + dAMKA + ")");

            setHTML(html); //Set HTML value equal to the message above(HTML content is being shown in "AvailableDoctorAppointments.jsp")

            response.sendRedirect("AvailableDoctorAppointments.jsp"); //redirect patient back
        }
        catch (Exception e) //in case something goes wrong print message to page
        {
            PrintWriter showhtml = response.getWriter();
            showhtml.println(e.toString());
        }
    }

    /**
     * Show past doctor and patient appointments by doctor attribute
     * <p>
     * Makes a html table which contains, on each row, the details about the patient's history appointments.
     * Each row contains the following information: the date, the start time and the ending time of the appointment,
     * the doctor's AMKA and the doctor's firstname, lastname and specialty.
     * If the patient has not any history appointments a 'Appointment history is empty' message appears(Fail function).
     * In any other case a corresponding message appears, with the help of Fail function.
     *
     * @param showby     The doctor attribute to search history appointments by
     * @param value      The actual value of the above attribute
     * @param response   A Servlet response object required to redirect user to another page.
     * @param request    A Servlet request object required to get the session attributes
     * @param datasource A Datasource to inject SQL statements into.
     * @throws IOException if anything goes wrong with the HttpServletResponse.
     *
     */
    public static void showAppointmentHistory(String showby, String value, HttpServletResponse response, HttpServletRequest request, DataSource datasource) throws IOException
    {
        HttpSession session = request.getSession();

        PrintWriter showhtml = response.getWriter();

        //The user has the option(from an option box) to choose by which doctor attribute(doctor AMKA, appointment date or doctor specialty)
        //his appointments are going to be shown(there is also a 'Show All' option to show appointments without a restriction).
        //The desirable value of an attribute is being submitted in a textbox before the results are shown.

        try
        {
            connection = datasource.getConnection();    //connection object for database connection

            rs = createResultSet(showby,value,"history", session); //return on rs the patient's search results(check input formats first)

            if (rs.next()) //in case there is at least one record, store on html variable the table result for history appointments
            {
                StringBuilder html = createHtmlTable(0, rs); //a string builder object to store the html content we want to show on "appointmenthistory.jsp"
                setHTML(html); //Set HTML content equal to html content. HTML content appears on "appointmenthistory.jsp"
                response.sendRedirect("appointmenthistory.jsp"); //redirect patient back
            }
            else if (!rs.next() && showby.equals("Show all")) //if there is not any record on the results and the option
            {                                                // is 'Show all', that means history is empty
                Fail(response, "Appointment history is empty", "appointmenthistory.jsp");
            }
            else  //In this case, there is not any record on the results but the option wasn't 'Show all'.
            {     //That means there is not any results JUST for the restrictions we had set

                Fail(response, "No results found for " + showby + " " + value, "appointmenthistory.jsp");
            }

            rs.close();
            connection.close(); //close ResultSet and Connection

        }
        catch (ParseException e) //parse exception occurs if we try to search a date or an AMKA with invalid format typed
        {
            Fail(response, "Invalid " + showby + " format", "appointmenthistory.jsp");
        }
        catch (Exception e) //In any other exception case, print message to page
        {
            showhtml.println(e.toString());
        }

    }

    /**
     *
     * Show scheduled doctor appointments by doctor attribute.
     * Makes a html table which contains, on each row, the details about the appointment that has been scheduled
     * with a specified doctor.Each row is being followed by a "Cancel" button, to give patient the opportunity to
     * cancel the appointment.
     *
     * Each row contains the following information: the date, the start time and the ending time of the appointment,
     * the doctor's AMKA and the doctor's firstname, lastname and specialty.
     * In any other case(ex. no results found) a corresponding message appears, with the help of Fail function.
     *
     * @param showby The attribute of a doctor we want to show scheduled appointments for.
     * @param value  The actual value of 'showby' attribute we are looking for
     * @param response Servlet response object required to redirect to other pages and show messages on page
     * @param request Servlet request object required to get session attributes
     * @param datasource Datasource object required to access database
     *
     */
    public static void showScheduledAppointments(String showby, String value, HttpServletResponse response, HttpServletRequest request, DataSource datasource) throws IOException
    {

        HttpSession session = request.getSession(); //get session

        try
        {
            connection = datasource.getConnection();    //connection object for database connection

            rs = createResultSet(showby,value,"scheduled",session); //return on rs the patient's search results

            if (rs.next()) //in case there is at least one record, store on html variable the table result for scheduled appointments
            {
                StringBuilder html = createHtmlTable(1, rs); //a string builder object to store the html content we want to show on "ScheduledAppointments.jsp"
                setHTML(html); //Set HTML equal to html. HTML content is being shown at "ScheduledAppointments.jsp"
                response.sendRedirect("ScheduledAppointments.jsp"); //redirect back
            }
            else if (!rs.next() && showby.equals("Show all")) //if there is not any record on the results and the option
            {                                                // is 'Show all', that means there is not any scheduled appointment
                Fail(response, "You have not any scheduled appointments yet", "patient_main_environment.jsp");
            }
            else  //In this case, there is not any record on the results but the option wasn't 'Show all'.
            {     //That means there is not any results JUST for the restrictions we had set

                Fail(response, "No results found for " + showby + " " + value, "ScheduledAppointments.jsp");
            }

            rs.close();
            connection.close(); //close ResultSet and Connection

        }
        catch (ParseException e) //parse exception occurs if we try to search a date or an AMKA with invalid format typed
        {
            Fail(response, "Invalid " + showby + " format", "ScheduledAppointments.jsp");
        }
        catch (Exception e) //Show message on page in any other case
        {
            PrintWriter showhtml = response.getWriter();
            showhtml.println(e.toString());
        }
    }

    // Getter for the attribute AMKA
    public String getAMKA() {
        return this.AMKA;
    }

}
