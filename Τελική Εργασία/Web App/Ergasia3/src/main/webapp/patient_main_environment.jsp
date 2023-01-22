<!-- This is the main environment of the patient. From this page, every patient who successfully logs in, will be
able to view the history of his appointments, to book a new appointment or watch all the appointments that he has
scheduled in the past-->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>  <!-- JSP import packages -->

<!DOCTYPE html>

<html lang="en">

    <head>

        <%//make page invalidate in every access and dont store it to cache in order to prevent access with back button after logout
        response.setHeader("Cache-Control","no-cache, no-store, must-invalidate");

        if(session.getAttribute("patientusername") == null)  //if patient username attribute is null that means admin is no more logged on
        {
            request.setAttribute("message",1);
            RequestDispatcher rd = request.getRequestDispatcher("login.jsp");
            rd.forward(request, response); //forward the user back to login page and show "Session time out message"
        }%>

        <meta charset="utf-8">
        <title>Doctor appointments: dashboard</title>

        <style>

            /* style rules for the buttons */
            #buttons
            {
                font-size:17px;
                text-align:center;
                color:white;
                background-color: #012A6C;
            }

            /* style rules when hyperlinks are pressed */
            a:visited
            {
                color: #012A6C;
            }

            /* style the hyperlinks in the nav section */
            a
            {
                font-size:14px;
                color: #012A6C;
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
            }

            /* style rules for the logo image that is appeared in the body of the web page */
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

            /* set style rules for the container class */
            .container
            {
                padding: 10px;
            }

            /* style rules about the alert message box */
            .alert
            {
                padding: 20px;
                background-color: #f1f1f1;
                color: #012A6C;
                margin-bottom: 15px;
            }

            /* The close button of the message at the upper of the web page */
            .closebtn
            {
                margin-left: 15px;
                color: #012A6C;
                font-weight: bold;
                float: right;
                font-size: 22px;
                line-height: 20px;
                cursor: pointer;
                transition: 0.3s;
            }

            /* style rules for the article section of the web page */
            article
            {
                margin: 0;
                position: absolute;
                top: 50%;
                left: 50%;
                -ms-transform: translate(-50%, -50%);
                transform: translate(-50%, -50%);
            }

            /* set border and background color of form */
            form
            {
                border: 3px solid whitesmoke;
                background-color: white ;
            }

            /* when moving the mouse over the close button, make it black */
            .closebtn:hover
            {
                color: black;
            }

            /* style rules for the table that is showed to the user */
            table
            {
                align:center;
            }

            /* style rules for every table heading of the table */
            th
            {
                width:120px;
                height:20px;
                text-align:center;
                color:black;
                border:1px solid mediumseagreen;
                padding: 12px 20px;
            }

            /* style rules for every table row of the table */
            tr
            {
                text-align: center;
                padding: 16px;
            }

        </style>

    </head>

    <!-- Show to user a message -->
    <div class="alert">
        <span class="closebtn" onclick="this.parentElement.style.display='none';">&times;</span>
        <strong>Welcome!</strong> Feel free to use the menu, in order to use the application.
    </div>

    <body>

        <article>

            <br>
            <br>

            <!-- Form that contains a hidden input html tag. The value of the tag is being passed in the patient servlet -->
            <form method="post" id="form" action="patient">

                <input type="hidden" id="patient_action" name="patient_action" value="">

                <div class="imgcontainer">
                    <img src="img/logo1.png" alt="logo_image" class="avatar">
                </div>

                <br>

                <div class="container">
                    <label><pre><b style="color:#012A6C"> Hello patient! Here are your data:</b></pre></label>
                </div>

                <div class="container">

                    <!--Showing patient's attributes-->
                    <table>
                        <tr><th>Username</th><th>Name</th><th>Surname</th><th>Age</th><th>AMKA</th></tr>
                        <tr><td><%= session.getAttribute("patientusername") %></td><td><%= session.getAttribute("name") %></td><td><%= session.getAttribute("surname") %></td><td><%= session.getAttribute("age") %></td><td><%= session.getAttribute("patientAMKA") %></td></tr>
                    </table>

                </div>

                <br>

                <div class="container">
                    <label><pre><b style="color:#012A6C"> Please select what do you want to do!</b></pre></label>
                </div>

                <center>

                    <div class="container">

                        <button id="buttons" onclick="setAction(1);">Appointment history</button>

                        <button id="buttons" onclick="setAction(2);">Book an appointment</button>

                        <button id="buttons" onclick="setAction(3);">Scheduled appointments</button>

                        <button id="buttons" onclick="setAction(4);">Logout</button>

                        <br>
                        <br>

                    </div>

                </center>

            </form>

        </article>

        <script>

            //Javascript function that executes for every patient action

            //Depending on the button clicked that describes the action,
            //an action value is being stored in the hidden html input
            //tag (name="patient_action").We submit the
            //form and we are being redirected to the patient servlet.
            //From there, we get the hidden input html tag value, and we
            //act accordingly.

            function setAction(a)
            {
                document.getElementById("patient_action").value = a;

                document.getElementById("form").submit();
            }

        </script>

    </body>

</html>
