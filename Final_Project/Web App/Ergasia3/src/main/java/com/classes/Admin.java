package com.classes;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This is the model of an Admin. Admins are the only ones who can add, modify and delete doctors from the
 * application.
 */
public class Admin extends Users
{
    //variables for database management
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet rs;

    // Constructor method
    public Admin(String username, String password, String firstname, String surname, int age)
    {
        super(username, password, firstname, surname, age);
    }

    /**
     * This function is really abstract, so an admin can use it multiple times to delete various users. It interprets the data given to
     * it and it executes DELETE statements to the database. If the Value of the Element doesn't exist in the database, it redirects to the Fail page.
     * @param request The {@link HttpServletRequest} to redirect correctly the user to success.jsp page.
     * @param response The {@link HttpServletResponse} to reditect the user to the fail.jsp page.
     * @param datasource The Database to delete data from.
     * @param Table Any of the following values: ("admin", "doctor", "patient").
     * @param ValueOfElement The value to search and delete. If the user to be deleted is a {@link Doctor} or a {@link Patient}, then we search to delete an AMKA. If it's an {@link Admin} then it's a username.
     * @param delete_page The delete page the {@link Admin} attempted to delete a user.
     * @throws IOException
     */
    public static void delete_users(HttpServletRequest request, HttpServletResponse response, DataSource datasource, String Table, String ValueOfElement, String delete_page) throws IOException
    {
        String ElementToDelete;

        if ("admin".equals(Table))
        {
            ElementToDelete = "username";

            //check if given username is in correct format before taking any action
            if (!ValueOfElement.matches("[A-Za-z0-9]{1,12}"))
            {
                Users.Fail(response, "There is no such username.", delete_page);
                return;
            }
        }
        else
        {
            ElementToDelete = Table + "AMKA";

            //check if given AMKA is in correct format before taking any action
            if (!ValueOfElement.matches("[0-9]{11}"))
            {
                Users.Fail(response, "There is no such AMKA.", delete_page);
                return;
            }
        }

        try
        {
            //getting the connection and preparing the sql statement.
            connection = datasource.getConnection();

            //the sql statement is intentionally vague, so all users can be deleted.
            //first we use a select statement to see if this AMKA/username exists.
            statement  = connection.prepareStatement("SELECT * FROM " + Table + " WHERE " + ElementToDelete + "=?");
            statement.setString(1, ValueOfElement);
            rs = statement.executeQuery();

            //if it doesn't exist, we show a fail page to the user.
            if (!rs.next())
            {
                String value = ElementToDelete.equals("username") ? "username" : "AMKA";
                Users.Fail(response, "There is no such " + value + ".", delete_page);
                rs.close();
                connection.close();
                return;
            }

            //otherwise we can execute the delete statement. If the delete statement retains a doctor or a patient to be deleted, we also delete him from the appointments table.
            //This is achieved by a "on delete cascade" command, in SQL level. Last but not least, in case when we delete an admin, we set ADMIN_username=null, in all the related doctors(on delete set null).
            rs.close();

            //User can be deleted right away for now.
            statement = connection.prepareStatement("DELETE FROM " + Table + " WHERE " + ElementToDelete + "=?;");
            statement.setString(1, ValueOfElement);
            statement.execute();

            connection.close();

            //We send a successful success.jsp message.
            request.setAttribute("action", "deleted the " + Table + " with " + ElementToDelete + " " + ValueOfElement);
            request.setAttribute("redirect", delete_page);
            RequestDispatcher rd = request.getRequestDispatcher("success.jsp");
            rd.forward(request, response);
        }
        catch (Exception e)
        {
            Users.Fail(response, "An error has occurred. MESSAGE: " + e.getMessage(), delete_page);
        }

    }

    //New patients can use the Users.Register to be added. So their methods can be very simple.

    public static void add_patient(HttpServletRequest request, HttpServletResponse response, DataSource datasource, String username, String password, String firstname, String surname, Integer age, String AMKA) throws IOException
    {
        new Patient(username, password, firstname, surname, age, AMKA).Register(request, response,datasource,"add_new_patient.jsp");
    }

    public static void add_doctor(HttpServletRequest request, HttpServletResponse response, DataSource datasource, String username, String password, String firstname, String surname, Integer age, String speciality, String AMKA) throws IOException
    {
        new Doctor(username,password,firstname,surname,age,speciality,AMKA).Register(request,response,datasource,"add_new_doctor.jsp");
    }

    public static void add_admin(HttpServletRequest request, HttpServletResponse response, DataSource datasource, String username, String password, String firstname, String surname, Integer age) throws IOException
    {
        new Admin(username,password,firstname,surname,age).Register(request,response,datasource,"add_new_admin.jsp");
    }
}
