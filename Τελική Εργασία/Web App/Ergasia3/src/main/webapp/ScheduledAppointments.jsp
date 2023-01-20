<!-- In this page, patient will be able to watch all of his scheduled appointments -->
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

        <title>Patient scheduled appointments</title>
        <link rel="stylesheet" href="CSS/styles.css">
        <meta charset="utf-8">

    </head>

    <body>

        <form action="patient" method="post" id="form">

            <div class="imgcontainer">
                <img src="img/logo1.png" alt="logo_image" class="avatar">
            </div>

            <br>

            <%= Users.getHTML() %> <!-- print the html results that showScheduledAppointments method stored on HTML variable -->
            <% Users.clearHTML(); //clear HTML variable%>

            </table>

            <br>
            <br>

            <div class="container">
                <label><b style="color:#012A6C">Choose a category to search appointments by:  </b></label>

                <select name="showby" id="showby" onclick="checkoption();">
                    <option selected value="Show all">Show all</option>
                    <option value="Doctor AMKA">Doctor AMKA</option>
                    <option value="Date (dd-MM-yyyy)">Date (dd-MM-yyyy)</option>
                    <option value="Specialty">Specialty</option>
                </select>
            </div>

            <div class="container">
                <label hidden id="lbl" for="value1"><b style="color:#012A6C">Insert the doctor's AMKA/appointment date/speciality:  </b></label>
                <input type="text" id="value1" name="value1" style = "display: none">
                <select name="value2" id="value2" style = "display: none">
                    <option selected value="Ophthalmologist">Ophthalmologist</option>
                    <option value="Pathologist">Pathologist</option>
                    <option value="Orthopedist">Orthopedist</option>
                </select>
                <button type="submit" onclick="setvalue(3);">Search</button>

                <!--Hidden html tag that stores the patient servlet action as it's value -->
                <input type="hidden" name="patient_action" id="patient_action" value="3">

                <!--Hidden html tags that stores the details of the desired to cancel appointment -->
                <input type="hidden" name="datevalue" id="datevalue" value="">
                <input type="hidden" name="start" id="start" value="">
                <input type="hidden" name="doctorAMKA" id="doctorAMKA" value="">

            </div>

        </form>

        <br>

        <div class=navbar>
            <p>Do you want to go back? Click <a href="patient_main_environment.jsp">here</a></p>
        </div>

        <script>
            function checkoption() //this function disables the input html tag for doctor attribute if we choose to search all the appointments from select box
            {
                var s = document.getElementById("showby");
                var o = s.options[s.selectedIndex].value;

                if(o == "Show all")
                {
                    document.getElementById("value1").style.display = "none"; //both controls hidden
                    document.getElementById("value1").required = false;
                    document.getElementById("value1").value = "";

                    document.getElementById("value2").style.display = "none";
                    document.getElementById("value2").required = false;
                    document.getElementById("value2").value = "Ophthalmologist";

                    document.getElementById("lbl").hidden = true; //hide label
                }
                else if(o == "Specialty")
                {
                    document.getElementById("value1").style.display = "none"; //hide input text
                    document.getElementById("value1").required = false;
                    document.getElementById("value1").value = "";

                    document.getElementById("value2").style.display = "inline"; //show menu
                    document.getElementById("value2").required = true;

                    document.getElementById("lbl").hidden = false; //show label
                }
                else
                {
                    document.getElementById("value1").style.display = "inline"; //show input text
                    document.getElementById("value1").required = true;
                    document.getElementById("value1").value = "";

                    document.getElementById("value2").style.display = "none"; //hide menu
                    document.getElementById("value2").required = false;
                    document.getElementById("value2").value = "Ophthalmologist";

                    document.getElementById("lbl").hidden = false; //show label
                }

            }

            function setvalue(v) //this function is being used to set value on a hidden html tag after the submit button click.
            {                    //patient servlet takes the value from the hidden html tag and performs the corresponding action
                document.getElementById("patient_action").value = v;
            }

                                                       //this function is running after we press a "Cancel" button on the table that HTML variable returns
                                                       //(see above). It takes the desirable to cancel appointment details as parameter, it passes them to
            function cancelappointment(d,start,dAMKA) //hidden html input tags and then submits the form
            {
                var choice = confirm("Are you sure that you want to cancel this appointment?");

                if(choice)
                {
                    document.getElementById("datevalue").value = d;
                    document.getElementById("start").value = start;
                    document.getElementById("doctorAMKA").value = dAMKA;

                    document.forms[0].submit();
                }
            }

        </script>

    </body>

</html>
