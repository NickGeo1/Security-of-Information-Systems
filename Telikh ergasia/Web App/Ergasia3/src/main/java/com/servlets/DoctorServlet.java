package com.servlets;

import com.classes.Doctor;
import com.classes.Patient;
import com.classes.Users;

import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "DoctorServlet", value = "/doctor")
public class DoctorServlet extends HttpServlet
{
    private static String DOCTOR_SERVLET_ACTION; //variable to specify the doctor's action
    private static DataSource datasource;//Datasource variable to manage database

    public void init() //Initialize datasource variable at servlet start
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

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        DOCTOR_SERVLET_ACTION = request.getParameter("doctor_action"); //Get the action value from the pressed html button(see doctor_main_environment.jsp)
        String requestedURL = request.getHeader("referer"); //get the URL that sent you to this servlet

        switch (DOCTOR_SERVLET_ACTION)
        {
            //login
            case "login":
                Users.Login("Doctor", request, response, datasource);
                break;

            //logout
            case "logout":
                Users.Logout(response, request);
                break;

            //set availability
            case "set availability":

                //if the doctor is on his main page and clicks the button to set an appointment, we redirect him to the set availability page.
                //similarly for all other cases, we examine the same factors before taking action.
                if (requestedURL.endsWith("doctor_main_environment.jsp"))
                    response.sendRedirect("doctor_set_availability.jsp");

                //otherwise, it means he's on the set availability page
                else
                {
                    //at first we get the date the doctor has set.
                    LocalDateTime date = LocalDateTime.parse(request.getParameter("date_of_appointment"));

                    //then we call the set availability function to insert data into the database.
                    if (Doctor.set_availability(datasource, date, request.getSession().getAttribute("doctorAMKA").toString()))
                    {
                        //if it's successful, then we redirect he doctor to the success page.
                        request.setAttribute("action", "set availability for " + date.toString().replace("T"," "));
                        request.setAttribute("redirect", "doctor_set_availability.jsp");
                        RequestDispatcher RD = request.getRequestDispatcher("success.jsp");
                        RD.forward(request, response);
                    }
                    else
                    {
                        //otherwise, we redirect the doctor to the fail page.
                        Users.Fail(response, Doctor.getReason(), "doctor_set_availability.jsp");
                    }
                }

                break;

            //view doctor's scheduled appointments
            case "view appointments":
                //if the doctor is on his main page and clicks the button to view scheduled appointments, we redirect him to the scheduled appointments page.
                if (requestedURL.endsWith("doctor_main_environment.jsp"))
                    response.sendRedirect("doctor_view_appointments.jsp");
                else
                {
                    String date;

                    //if doctor wants to search scheduled appointments by week we get the "week" html input tag value
                    if(request.getParameter("showby").equals("Week"))
                        date = request.getParameter("week");
                    else
                        //else doctor wants to search scheduled appointments by month so we get the "month" html input tag value
                        date = request.getParameter("month");

                    //We call viewAppointments method to return the html results on scheduled appointments page
                    Doctor.viewAppointments(request.getParameter("showby"),date,response,request,datasource);
                }

                break;

            //cancel scheduled appointment
            case "cancel":
                //We get the desirable date value, the start time, the patient and doctor amka of the appointment we want to delete,
                //based from the cancel button clicked and we remove the corresponding record from the database
                String date = request.getParameter("datevalue");
                String pAMKA = request.getParameter("patientAMKA");
                String start = request.getParameter("start");
                String dAMKA = (String) request.getSession().getAttribute("doctorAMKA");
                Patient.cancelScheduledAppointment(date,pAMKA,dAMKA,start,request,response,datasource);
                break;
        }
    }
}