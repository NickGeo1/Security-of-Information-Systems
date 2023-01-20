package com.classes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.*;

/**
 * This is the model (class) of a Doctor. Each doctor has his speciality and
 * some other abilities, which are to be able to see his appointments,
 * as well as say if he is available (or not) for a new appointment.
 */
public class Doctor extends Users
{
    private String speciality; // Speciality that a doctor has
    private final String AMKA; // This is the unique AMKA of each doctor

    //variables for database management
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet rs;
    private static String reason;

    // Constructor method
    public Doctor(String username, String password, String firstname, String surname, int age, String speciality, String AMKA)
    {

        super(username, password, firstname, surname, age); // Constructor of class Doctor calls superclasses' constructor
        this.speciality = speciality;
        this.AMKA = AMKA;

    }

    /**
     * Appends data to the database for the specified {@link Doctor}'s available appointments.
     * @param datasource The database to insert data to.
     * @param localDate The date and time the {@link Doctor} has picked.
     * @param AMKA The {@link Doctor}'s {@link Doctor#AMKA}.
     * @return - True, if the data were appended successfully. <br> - False, if anything goes wrong.
     */
    public static boolean set_availability(DataSource datasource, LocalDateTime localDate, String AMKA)
    {
        try
        {
            connection = datasource.getConnection();
            statement  = connection.prepareStatement("SELECT date FROM appointment WHERE ? BETWEEN startSlotTime AND endSlotTime AND date=? OR" +
                                                                                            "? BETWEEN startSlotTime AND endSlotTime AND date=?");
            statement.setString(1, localDate.toLocalTime().toString());
            statement.setString(2, localDate.toLocalDate().toString());
            statement.setString(3, localDate.toLocalTime().plusMinutes(30).toString());
            statement.setString(4, localDate.toLocalDate().toString());
            ResultSet rs = statement.executeQuery();

            if (rs.next())
            {
                reason = "Cannot override appointments. Choose another time or date.";
                return false;
            }

            statement  = connection.prepareStatement("INSERT INTO appointment VALUES(?, ?, ?, 0, ?)");
            statement.setString(1, localDate.toLocalDate().toString());
            statement.setString(2, localDate.toLocalTime().toString());
            statement.setString(3, localDate.plusMinutes(30).toLocalTime().toString());
            statement.setString(4, AMKA);
            statement.execute();

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            reason = e.toString();
            return false;
        }
    }

    /**
     *
     * This method returns the html table on doctor_view_appointments.jsp that contains database results of the corresponding doctor's search
     * of scheduled appointments
     *
     * @param showby The value that specifies by what we are going to search the scheduled appointments (by Month or by Week)
     * @param value The value of the week(ex. 2021-W23 means 23th week of year 2021) or the month(ex 2021-6) we are going to search scheduled appointments by
     * @param response The HttpResponse response object required to redirect the user in a specific page
     * @param request The HttpRequest request object required to get the user's session values
     * @param datasource Object that references to the database datasource
     * @throws IOException
     */
    public static void viewAppointments(String showby, String value, HttpServletResponse response, HttpServletRequest request , DataSource datasource) throws IOException
    {
        try
        {
            connection = datasource.getConnection();    //connection object for database connection

            //This query selects all the scheduled appointments related to the logged on doctor and have a given date format
            String query = "SELECT date,startSlotTime,endSlotTime,PATIENT_patientAMKA,name,surname " +
                    "FROM appointment JOIN patient ON PATIENT_patientAMKA = patientAMKA " +
                    "WHERE DOCTOR_doctorAMKA = ? AND PATIENT_patientAMKA != 0 AND (date > cast(now() as date) OR date = cast(now() as date) AND startSlotTime > cast(now() as time))" +
                    " AND date like ?";

            String[] yearandweek = {}; //array to split the year and the week from "value" because year and week are seperated by "-W"

            if(showby.equals("Week")) //show appointments by week
            {
                //if we want to show appointments by week, we have to add the chosen week of year restriction
                query += " AND WEEKOFYEAR(`date`) = ?";

                yearandweek = value.split("-W"); //get year and week in array "yearandweek"

                //prepare statement and store the corresponding variables
                statement = connection.prepareStatement(query);
                statement.setString(1,(String) request.getSession().getAttribute("doctorAMKA"));
                statement.setString(2,yearandweek[0]+"%");
                statement.setString(3, yearandweek[1]);
            }
            else //show appointments by month
            {
                //prepare statement and store the corresponding variables
                statement = connection.prepareStatement(query);
                statement.setString(1,(String) request.getSession().getAttribute("doctorAMKA"));
                statement.setString(2,value+"%");
            }

            //execute statement and return results in rs
            rs = statement.executeQuery();

            if(rs.next()) //in case there is at least one record, make the table headers
            {
                StringBuilder html = new StringBuilder(
                                "<table>"
                                +"<tr>"
                                +"<th>Date</th>"
                                +"<th>Start time</th>"
                                +"<th>End time</th>"
                                +"<th>Patient AMKA</th>"
                                +"<th>Patient name</th>"
                                +"<th>Patient surname</th>"
                                +"</tr>");
                String date;
                String startSlotTime;
                String endSlotTime;
                String PATIENT_patientAMKA;
                String Patient_name;
                String Patient_surname;
                String htmlRow;

                do  //add the result's rows on the table
                {
                    date = rs.getString("date");

                    //change the date to the correct format before storing it into the variable
                    date = changeDateFormat("yyyy-MM-dd", "dd-MM-yyyy", date);
                    startSlotTime = rs.getString("startSlotTime");
                    endSlotTime = rs.getString("endSlotTime");
                    PATIENT_patientAMKA = rs.getString("PATIENT_patientAMKA");
                    Patient_name = rs.getString("name");
                    Patient_surname = rs.getString("surname");

                    htmlRow = createTableRow(3, date, startSlotTime, endSlotTime, PATIENT_patientAMKA, "", Patient_name, Patient_surname);
                    html.append(htmlRow);

                }while(rs.next());

                html.append("</table>");
                //set the html value and redirect back to doctor_view_appointments.jsp
                setHTML(html);
                response.sendRedirect("doctor_view_appointments.jsp");
            }
            else if(showby.equals("Week")) //In this case, doctor has not any scheduled appointments in this week
            {
                Fail(response,"No results found for week " + yearandweek[1] + " of year " + yearandweek[0],"doctor_view_appointments.jsp");
            }
            else //In this case, doctor has not any scheduled appointments in this month
            {
                String[] yearandmonth = value.split("-"); //seperate year and month from value
                Fail(response,"No results found for month " + yearandmonth[1] + " of year " + yearandmonth[0],"doctor_view_appointments.jsp");
            }

            rs.close();
            connection.close(); //close ResultSet and Connection
        }
        catch (Exception e) //in case something goew wrong print the exception in page
        {
            PrintWriter showhtml = response.getWriter();
            showhtml.println(e.toString());
        }
    }

    // Getter for the attribute speciality that a doctor has
    public String getSpeciality()
    {
        return speciality;
    }
    //returns the reason of a doctor set availability failure
    public static String getReason()
    {
        return reason;
    }

    // Getter for the attribute AMKA
    public String getAMKA() { return this.AMKA; }

}
