package com.servlets;

import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;

import com.classes.Admin;
import com.classes.Users;

@WebServlet(name = "AdminServlet", value = "/admin")
public class AdminServlet extends HttpServlet
{
    private static String ADMIN_SERVLET_ACTION;
    private static DataSource datasource;

    public void init()
    {
        try
        {
            InitialContext ctx = new InitialContext();
            datasource = (DataSource)ctx.lookup("java:comp/env/jdbc/LiveDataSource");
        }
        catch(Exception e)
        {
            System.out.println("A Datasource exception has occurred: " + e.toString());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        ADMIN_SERVLET_ACTION = request.getParameter("admin_action"); //Get the action value from the pressed html button(see admin_main_environment.jsp)
        String requestedURL = request.getHeader("referer"); //get the URL that sent you to this servlet

        //based on what an admin wants to do, we have a switch-case depending on his actions.
        //Most of these actions are triggered from a button. So depending from where that button was clicked, we trigger different actions.
        switch (ADMIN_SERVLET_ACTION)
        {
                //login
            case "login":
                Users.Login("Admin", request, response, datasource);
                break;

            //add user methods
            case "add_admin":
                //if the admin is on the main environment page and presses the button to add an admin, we redirect them to the page where he can add the admin
                if (requestedURL.endsWith("admin_main_environment.jsp"))
                    response.sendRedirect("add_new_admin.jsp");

                //but if the admin is already in the add admin page, it means that they have data to add. So it's safe to get the data and append them to the databse.
                else if (requestedURL.endsWith("add_new_admin.jsp"))
                {
                    try
                    {
                        String username  = request.getParameter("username");
                        String password  = request.getParameter("password");
                        String firstname = request.getParameter("firstname");
                        String surname   = request.getParameter("surname");
                        Integer age      = Integer.parseInt(request.getParameter("age"));

                        Admin.add_admin(request, response, datasource, username, password, firstname, surname, age);
                    }

                    catch (NumberFormatException e)
                    {
                        Users.Fail(response,"Invalid Age! A registered age must be a number.","add_new_admin.jsp");
                        return;
                    }
                }

                break;

                //similarly for all other cases, we examine the same factors before taking action.
            //if the admin is on its main environment then we don't take any other action other than redirecting them to the page they've clicked on.
            case "add_patient":
                if (requestedURL.endsWith("admin_main_environment.jsp"))
                    response.sendRedirect("add_new_patient.jsp");

                else if (requestedURL.endsWith("add_new_patient.jsp"))
                {
                    try
                    {
                        String username   = request.getParameter("username");
                        String password   = request.getParameter("password");
                        String firstname  = request.getParameter("firstname");
                        String surname    = request.getParameter("surname");
                        Integer age       = Integer.parseInt(request.getParameter("age"));
                        String AMKA       = request.getParameter("AMKA");

                        Admin.add_patient(request, response, datasource, username, password, firstname, surname, age, AMKA);
                    }

                    catch (NumberFormatException e)
                    {
                        Users.Fail(response,"Invalid Age! A registered age must be a number.","add_new_patient.jsp");
                        return;
                    }
                }

                break;

            case "add_doctor":
                if (requestedURL.endsWith("admin_main_environment.jsp"))
                    response.sendRedirect("add_new_doctor.jsp");

                else if (requestedURL.endsWith("add_new_doctor.jsp"))
                {
                    try
                    {
                        String username   = request.getParameter("username");
                        String password   = request.getParameter("password");
                        String firstname  = request.getParameter("firstname");
                        String surname    = request.getParameter("surname");
                        Integer age       = Integer.parseInt(request.getParameter("age"));
                        String speciality = request.getParameter("speciality");
                        String AMKA       = request.getParameter("AMKA");

                        Admin.add_doctor(request, response, datasource, username, password, firstname, surname, age, speciality, AMKA);
                    }
                    catch (NumberFormatException e)
                    {
                        Users.Fail(response,"Invalid Age! A registered age must be a number.","add_new_doctor.jsp");
                        return;
                    }
                }

                break;

                //delete methods
            case "delete_admin":
                if (requestedURL.endsWith("admin_main_environment.jsp"))
                    response.sendRedirect("delete_admin.jsp");

                else if (requestedURL.endsWith("delete_admin.jsp"))
                {
                    //if an admin attempts to delete themselves, could result to disaster. So we make sure this is forbidden.
                    //first we get the username of the admin to be deleted
                    String usernameToBeDeleted = request.getParameter("admin_username");

                    //and if it's the same as the current logged on admin
                    HttpSession session = request.getSession();
                    String CurrentAdminUsername = (String) session.getAttribute("adminusername");

                    //we don't let such thing happen, and we redirect the Admin to the Fail page.
                    if (CurrentAdminUsername.equals(usernameToBeDeleted))
                    {
                        Users.Fail(response, "Admins cannot delete themselves.", "delete_admin.jsp");
                        break;
                    }

                    //otherwise it's safe to delete.
                    Admin.delete_users(request, response, datasource, "admin", usernameToBeDeleted, "delete_admin.jsp");
                }

                break;

                //all other delete methods don't need such constraints as the admin.
            case "delete_patient":
                if (requestedURL.endsWith("admin_main_environment.jsp"))
                    response.sendRedirect("delete_patient.jsp");

                else if (requestedURL.endsWith("delete_patient.jsp"))
                {
                    String AMKAtoBeDeleted = request.getParameter("patientAMKA");
                    Admin.delete_users(request, response, datasource, "patient", AMKAtoBeDeleted, "delete_patient.jsp");
                }

                break;

            case "delete_doctor":
                if (requestedURL.endsWith("admin_main_environment.jsp"))
                    response.sendRedirect("delete_doctor.jsp");

                else if (requestedURL.endsWith("delete_doctor.jsp"))
                {
                    String AMKAtoBeDeleted = request.getParameter("doctorAMKA");
                    Admin.delete_users(request, response, datasource, "doctor", AMKAtoBeDeleted, "delete_doctor.jsp");
                }

                break;

            case "logout":
                Users.Logout(response, request);
                break;
        }
    }
}