package com.servlets;

import com.classes.Patient;
import com.classes.Users;

import java.io.*;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;

@WebServlet(name = "PatientServlet", value = "/patient") //servlet annotation
public class PatientServlet extends HttpServlet
{
    private static DataSource datasource = null; //datasource object

    private static int PATIENT_SERVLET_ACTION; //Variable that describes what action the servlet should perform for the patient

    //init method runs at the very start of a servlet.
    //Here, init gets the datasource which corresponds
    //to our database

    public void init()
    {
        try
        {
            InitialContext ctx = new InitialContext();
            datasource = (DataSource)ctx.lookup("java:comp/env/jdbc/LiveDataSource");

        } catch(Exception e)
        {
            System.out.println("A Datasource exception has occured: "+e.toString());
        }

    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        //Get the action value from the hidden html input tag(see patient_main_environment.jsp)
        PATIENT_SERVLET_ACTION = Integer.parseInt(request.getParameter("patient_action"));

        String requestedURL = request.getHeader("referer"); //get the URL that sent you to this servlet

        //Depending on the action value, execute the corresponding method

        switch(PATIENT_SERVLET_ACTION)
        {
            //appointment history
            case 1:
                //if the patient is on his main page and clicks the button to view appointment history, we redirect him to the appointmenthistory page.
                //similarly for all other cases, we examine the same factors before taking action.
                if (requestedURL.endsWith("patient_main_environment.jsp"))
                    response.sendRedirect("appointmenthistory.jsp");

                //else, patient is already to appointmenthistory page, so we call the corresponding method with the parameters he entered on form
                else
                {
                    String value;

                    //if patient wants to search history appointments by doctor specialty, get the input value of
                    //HTML tag, with name = "value2"
                    if(request.getParameter("showby").equals("Specialty"))
                        value = request.getParameter("value2");

                    //else patient wants to search history appointments by something else (including show all)
                    //so we retrieve input value of HTML tag, with name = "value1"
                    else
                        value = request.getParameter("value1");

                    Patient.showAppointmentHistory(request.getParameter("showby"), value, response, request, datasource);

                }

                break;

            //Search available appointment
            case 2:
                String value_param = "";

                if (requestedURL.endsWith("patient_main_environment.jsp"))
                {
                    response.sendRedirect("AvailableDoctorAppointments.jsp");
                    return;
                }
                //if patient clicks the search button on AvailableDoctorAppointments page, we have to check if he wants to search appointments by doctor full name
                else if (request.getParameter("searchby").equals("Full name")) //if he wants, we have to join firstname and lastname together
                    value_param = request.getParameter("value") + " " + request.getParameter("value2");
                else if (request.getParameter("searchby").equals("Specialty")) //In case of specialty search, get the specialty value
                    value_param = request.getParameter("value3");
                else //If showby is "Show all", value = "" else showby is DoctorAMKA so value is the AMKA
                    value_param = request.getParameter("value");

                //dates are being passed in yyyy-MM-dd format from the form(refering to start date and end date)
                Patient.searchAvailableAppointments(request.getParameter("start"), request.getParameter("end"), request.getParameter("searchby"), value_param, response, datasource);
                break;

            //Scheduled appointments(similar to case 1)
            case 3:
                if(requestedURL.endsWith("patient_main_environment.jsp"))
                    response.sendRedirect("ScheduledAppointments.jsp");
                else
                {
                    String value;

                    //if patient wants to search history appointments by doctor specialty, get the input value of
                    //HTML tag, with name = "value2"
                    if(request.getParameter("showby").equals("Specialty"))
                        value = request.getParameter("value2");

                    //else patient wants to search history appointments by something else (including show all)
                    //so we retrieve input value of HTML tag, with name = "value1"
                    else
                        value = request.getParameter("value1");

                    Patient.showScheduledAppointments(request.getParameter("showby"), value, response, request, datasource);
                }
                break;

            //logout
            case 4:
                Patient.Logout(response, request);
                break;

            //register
            case 5:
                //getting the age as it is from the form and try to cast it to integer
                int age = 0;
                try
                {
                    age = Integer.parseInt(request.getParameter("age"));

                }
                catch (NumberFormatException e)
                {
                    Users.Fail(response,"Invalid Age! A registered age must be a number.","index.jsp");
                    return;
                }

                //getting the rest parameters as they are from the form.
                Patient patient = new Patient(  request.getParameter("username"),
                                        request.getParameter("password"),
                                        request.getParameter("fn"),
                                        request.getParameter("ln"),
                                        age,
                                        request.getParameter("AMKA")
                );

                //registering the patient as he is.
                patient.Register(request, response, datasource,"register.jsp");
                break;

            //login
            case 6:
                Users.Login("Patient",request,response,datasource);
                break;

            //cancel appointment

            //We get the desirable date value, the start time, the patient and doctor amka of the appointment we want to delete,
            //based to the cancel button clicked and we remove the corresponding record from the database
            case 7:
                String date = request.getParameter("datevalue");
                String pAMKA = (String) request.getSession().getAttribute("patientAMKA");
                String dAMKA = request.getParameter("doctorAMKA");
                String start = request.getParameter("start");
                Patient.cancelScheduledAppointment(date,pAMKA,dAMKA,start,request,response,datasource);
                break;

            //book appointment

            //We get the desirable date value, the start time, the end time and the doctor amka of the appointment we want to book,
            //based to the book button clicked and we update the corresponding appointment record from database.
            case 8:
                String date2 = request.getParameter("datevalue");
                String start2 = request.getParameter("startvalue");
                String end = request.getParameter("endvalue");
                String dAMKA2 = request.getParameter("dAMKA");
                Patient.bookAppointment(date2,start2,end,dAMKA2,response,request,datasource);
        }
    }
}