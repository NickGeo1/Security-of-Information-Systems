<!-- Τhis is the page in which a patient will be able to book an appointment with a doctor -->
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.classes.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<html>

    <head>

        <%//make page invalidate in every access and dont store it to cache in order to prevent access with back button after logout
        response.setHeader("Cache-Control","no-cache, no-store, must-invalidate");

        if(session.getAttribute("patientusername") == null)  //if patient username attribute is null that means admin is no more logged on
        {
            request.setAttribute("message",1);
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.forward(request, response); //forward the user back to login page and show "Session time out message"
        }%>

        <title>Doctor appointments: book appointment</title>
        <meta charset="utf-8">

        <style>

            /* set border and background colour to the form */
            form
            {
                border: 3px solid whitesmoke;
                background-color: white ;
            }

            /* the whole page has the same font */
            *
            {
                font-family:candara;
            }

            /* style rules for the body of the page */
            body
            {
                background-color: seagreen;
                margin: 0;
                position: absolute;
                top: 50%;
                left: 50%;
                -ms-transform: translate(-50%, -50%);
                transform: translate(-50%, -50%);
            }

            /* set styles for the inputs of the login form */
            input[type=text],
            input[type=password]
            {
                width: 30%;
                padding: 12px 20px;
                margin: 8px 0;
                box-sizing: border-box;
                border:1px solid mediumseagreen;
            }

            /* set a style for the buttons */
            button
            {
                background-color: mediumseagreen;
                color: white;
                padding: 10px 18px;
                margin: 8px 0;
                border: none;
                cursor: pointer;
                width: auto;
            }

            /* set a hover effect for the button */
            button:hover
            {
                opacity: 0.8;
            }

            /* centre the display image inside the container */
            .imgcontainer
            {
                text-align: center;
                margin: 24px 0 12px 0;
            }

            /* set image properties */
            img.avatar
            {
                width: 16%;
                border-radius: 70%;
            }

            /* set padding to the container */
            .container
            {
                padding: 16px;
            }

            /* style rules for the select section */
            select
            {
                width: 25%;
                height:30px;
                margin: 8px 0;
                display: inline-block;
                box-sizing: border-box;
                border:1px solid #012A6C;
            }

            /* style rules when hyperlinks are pressed */
            a:visited
            {
                color: #012A6C;
            }

            /* style style rules for the hyperlinks */
            a
            {
                font-size:16px;
                color: #012A6C;
            }

            /* style rules for the menu at the bottom of the page */
            .navbar
            {
                font-size: 16px;
                bottom: 0;
                text-align: center;
                background-color: #f1f1f1;
                width: 100%;
                height:20px;
            }

        </style>

    </head>

    <body>

        <form action="patient" method="post" id="form">

            <div class="imgcontainer">
                <img src="img/logo1.png" alt="logo_image" class="avatar">
            </div>

            <div class="container">
                <label><b style="color:#012A6C">Hello, let's book your appointment!</b></label>
            </div>

            <%  Date now = new Date(); //get the current date
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                String date_now = df.format(now); //format the current date to the required format for date html input tag
            %>

            <div class="container">
                <label><b>1) Please select the time interval you want to search appointments in:</b></label>
            </div>

            <br>

            <center>
                                                                                                                <!-- Set the current date as minimum starting date -->
                <b style="color:#012A6C;">Starting date:  </b><input type="date" name="start" id="start" onchange="set_min_end_date();" required min=<%= date_now %>>

                <br>
                <br>

                <b style="color:#012A6C;">Ending date:  </b><input type="date" name="end" id="end" min="" required>

            </center>

            <br>

            <div class="container">
                <label><b>2) Please select the category you want to search appointments by:</b></label>
            </div>

            <center>
                <select name="searchby" id="searchby" onchange="checkoption();">
                    <option selected value="Show all">Show all</option>
                    <option value="Doctor AMKA">Doctor AMKA</option>
                    <option value="Specialty">Specialty</option>
                    <option value="Full name">Full name</option>
                </select>
            </center>

            <br>

            <div class="container">
                <label id="b3"><b></b></label>
            </div>

            <br>

            <center>

                <b style="color:#012A6C;"  id="firstname"></b>
                <input type="text" id="value" name="value" style="display: none">
                <select name="value3" id="value3" style = "display: none">
                    <option selected value="Ophthalmologist">Ophthalmologist</option>
                    <option value="Pathologist">Pathologist</option>
                    <option value="Orthopedist">Orthopedist</option>
                </select>

                <br>

                <b style="color:#012A6C;"  id="lastname" ></b>
                <input type="text" id="value2" name="value2" hidden="true">

                <br>
                <br>

                <button type="submit" onclick="setvalue(2)">Search</button>
            </center>

            <br>

            <!--Hidden html tag that stores the patient servlet action as it's value -->
            <input type="hidden" name="patient_action" id="patient_action" value="2">

            <!--Hidden html tags that stores the details of the desired to book appointment -->
            <input type="hidden" name="datevalue" id="datevalue" value="">
            <input type="hidden" name="startvalue" id="startvalue" value="">
            <input type="hidden" name="endvalue" id="endvalue" value="">
            <input type="hidden" name="dAMKA" id="dAMKA" value="">

            <br>

            <center style="color:#012A6C">
                <%= Users.getHTML() %> <!-- //print the html results that searchAvailableAppointments method stored on HTML variable -->
                <% Users.clearHTML(); //clear HTML variable%>
            </center>

            <br>

        </form>

        <br>

        <div class="navbar">
            <p>Do you want to go back? Click <a href="patient_main_environment.jsp">here</a></p>
        </div>

        <script>

            function setvalue(v) //this function is being used to set value on a hidden html tag after the submit button click.
            {                    //patient servlet takes the value from the hidden html tag and performs the corresponding action
                document.getElementById("patient_action").value = v;
            }

            function set_min_end_date() //set min date for ending date tag equal to start_date + 1
            {
                var start_date = new Date(document.getElementById("start").value);
                start_date.setDate(start_date.getDate() + 1)

                document.getElementById("end").setAttribute("min", convert_date(start_date));
                document.getElementById("end").value = convert_date(start_date);
            }

            function convert_date(start_date) //format date to yyyy-MM-dd
            {
                var year = start_date.getFullYear();

                var month = start_date.getMonth() + 1;

                if(month.toString().length === 1)
                    month = "0" + month;

                var day = start_date.getDate();

                if(day.toString().length === 1)
                    day = "0" + day;

                start_date = year + "-" + month + "-" + day

                return start_date;
            }

            function checkoption() //this function manages the input tags depending on the select box value you choose
            {
                var s = document.getElementById("searchby");
                var o = s.options[s.selectedIndex].value;

                if(o == "Show all")
                {
                    document.getElementById("lastname").innerHTML = document.getElementById("firstname").innerHTML = "";

                    document.getElementById("value").style.display = "none";
                    document.getElementById("value").required = false;

                    document.getElementById("value2").hidden = true;
                    document.getElementById("value2").required = false;

                    document.getElementById("value3").style.display = "none";
                    document.getElementById("value3").required = false;

                    document.getElementById("b3").innerHTML = "";
                }
                else if(o == "Full name")
                {
                    document.getElementById("firstname").innerHTML = "Firstname: ";
                    document.getElementById("lastname").innerHTML = "Lastname: ";

                    document.getElementById("value").style.display = "inline";
                    document.getElementById("value").required = true;
                    document.getElementById("value").value = ""

                    document.getElementById("value2").hidden = false;
                    document.getElementById("value2").required = true;
                    document.getElementById("value2").value = ""

                    document.getElementById("value3").style.display = "none";
                    document.getElementById("value3").required = false;

                    document.getElementById("b3").innerHTML = "<b>3) Please insert the full name of the doctor you want to search appointments by:</b>";
                }
                else if(o == "Specialty")
                {
                    document.getElementById("lastname").innerHTML = document.getElementById("firstname").innerHTML = "";

                    document.getElementById("value").style.display = "none";
                    document.getElementById("value").required = false;

                    document.getElementById("value2").hidden = true;
                    document.getElementById("value2").required = false;

                    document.getElementById("value3").style.display = "inline";
                    document.getElementById("value3").required = true;
                    document.getElementById("value3").value = "Ophthalmologist"

                    document.getElementById("b3").innerHTML = "<b>3) Please choose the specialty you want to search appointments by:</b>";
                }

                else
                {
                    document.getElementById("value").value = document.getElementById("lastname").innerHTML = document.getElementById("firstname").innerHTML = "";

                    document.getElementById("value").style.display = "inline";
                    document.getElementById("value").required = true;

                    document.getElementById("value2").hidden = true;
                    document.getElementById("value2").required = false;

                    document.getElementById("value3").style.display = "none";
                    document.getElementById("value3").required = false;

                    document.getElementById("b3").innerHTML = "<b>3) Please insert the doctor's AMKA you want to search appointments by:</b>";
                }

            }

            function bookappointment(date,start,end,dAMKA) //this function is running after we press a "Book" button on the table that HTML variable returns
            {                                              //(see above). It takes the desirable to book appointment details as parameter, it passes them to
                                                          //hidden html input tags and then submits the form

                var choice = confirm("Are you sure that you want to book this appointmnt?");

                if(choice)
                {
                    document.getElementById("datevalue").value = date;
                    document.getElementById("startvalue").value = start;
                    document.getElementById("endvalue").value = end;
                    document.getElementById("dAMKA").value = dAMKA;

                    document.forms[0].submit();
                }
            }

        </script>

    </body>

</html>
