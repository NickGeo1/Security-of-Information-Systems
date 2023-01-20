<!-- In this page, a doctor is able to view all his appointments -->
<%@ page import="java.util.Calendar" %>
<%@ page import="com.classes.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<html>

    <head>

        <%//make page invalidate in every access and dont store it to cache in order to prevent access with back button after logout
        response.setHeader("Cache-Control","no-cache, no-store, must-invalidate");

        if(session.getAttribute("doctorusername") == null) //if doctor username attribute is null that means admin is no more logged on
        {
            request.setAttribute("message",1);
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.forward(request, response); //forward the user back to login page and show "Session time out message"
        }%>

        <title>Doctor appointments: scheduled appointments</title>
        <link rel="stylesheet" href="CSS/styles.css">
        <meta charset="utf-8">
    </head>

    <body>

    <form action="doctor" method="post" id="form">

        <div class="imgcontainer">
            <img src="img/logo1.png" alt="logo_image" class="avatar">
        </div>

        <br>

        <%= Users.getHTML() %> <!-- print the html results that viewAppointments method stored on HTML variable -->
        <% Users.clearHTML(); //clear HTML variable%>

        <br>
        <br>

        <div class="container">
            <label><b style="color:#012A6C">Choose an interval to show appointments by:  </b></label>

            <select name="showby" id="showby" onclick="checkoption();">
                <option selected value="Week">Week</option>
                <option value="Month">Month</option>
            </select>
        </div>


        <%
            //Get current week,year and month in order to set the appropriate minimum values on
            //week and month html input tags

            Calendar cal = Calendar.getInstance();
            int curr_week = cal.get(Calendar.WEEK_OF_YEAR);
            int curr_year = cal.get(Calendar.YEAR);
            Integer month = cal.get(Calendar.MONTH) + 1;

            String curr_month =  month.toString();

            if(curr_month.length() == 1) //month has to be 2 digits so we add a zero if its one digit
                curr_month =  "0"+curr_month;
        %>



        <div class="container">
            <label for="week"><b style="color:#012A6C" id="txt">Choose a week: </b></label>
            <input type="week" id="week" min="<%=curr_year%>-W<%=curr_week%>" name="week" required> <!-- Minimum week has been set to current week of year  -->
            <input type="month" id="month" min="<%=curr_year%>-<%=curr_month%>" name="month" hidden = "true"> <!-- Minimum month has been set to current month of year  -->
            <button type="submit">Search</button>

            <!--Hidden html tag that stores the DoctorServlet action for this page(value can be changed to "Cancel" if a cancel button is pressed)-->
            <input type="hidden" name="doctor_action" id="doctor_action" value="view appointments">

            <!--Hidden html tags that stores the details of the desired to cancel appointment -->
            <input type="hidden" name="datevalue" id="datevalue" value="">
            <input type="hidden" name="start" id="start" value="">
            <input type="hidden" name="patientAMKA" id="patientAMKA" value="">


        </div>

    </form>

    <br>

    <div class="navbar">
        <p>Do you want to go back? Click <a href="doctor_main_environment.jsp">here</a></p>
    </div>

    <script>
        function checkoption() //manage input html tags depending on the select box value
        {
            var s = document.getElementById("showby");
            var o = s.options[s.selectedIndex].value;

            if(o == "Week")
            {
                document.getElementById("txt").innerHTML = "Choose a week:";
                document.getElementById("month").hidden = true;
                document.getElementById("month").required = false;
                document.getElementById("month").value = "";
                document.getElementById("week").hidden = false;
                document.getElementById("week").required = true;
            }
            else
            {
                document.getElementById("txt").innerHTML = "Choose a month:";
                document.getElementById("week").hidden = true;
                document.getElementById("week").required = false;
                document.getElementById("week").value = "";
                document.getElementById("month").required = true;
                document.getElementById("month").hidden = false;
            }

        }

                                                        //This function is running after we press a "Cancel" button on the table that HTML variable returns
                                                        //(see above). It takes the desirable to cancel appointment details as parameter, it passes them to
             function cancelappointment(d,start,pAMKA)  //hidden html input tags and then submits the form
             {
                 var choice = confirm("Are you sure that you want to cancel this appointment?");

                 if(choice)
                 {
                     document.getElementById("datevalue").value = d;
                     document.getElementById("start").value = start;
                     document.getElementById("patientAMKA").value = pAMKA;

                     document.forms[0].submit();
                 }
            }

    </script>

    </body>

</html>
