<!-- This is the main environment of the Doctor. From this page, every doctor who successfully logs in, will be
able to set their availability (in appointments), watch the complete table with all of his appointments and
cancel an appointment -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>  <!-- JSP import packages -->

<!DOCTYPE html>

<html lang="en">

    <head>

      <%//make page invalidate in every access and dont store it to cache in order to prevent access with back button after logout
        response.setHeader("Cache-Control","no-cache, no-store, must-invalidate");

        if(session.getAttribute("doctorusername") == null) //if doctor username attribute is null that means admin is no more logged on
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

            <!-- Form that contains a hidden input html tag. The value of the tag is being passed in the doctor servlet -->
            <form method="post" id="form" action="doctor">

                <div class="imgcontainer">
                    <img src="img/logo1.png" alt="logo_image" class="avatar">
                </div>

                <br>

                <div class="container">
                    <label><pre><b style="color:#012A6C"> Hello doctor! Here are your data:</b></pre></label>
                </div>

                <div class="container">

                    <!--Showing doctor's attributes-->
                    <table>
                        <tr><th>Username</th><th>Name</th><th>Surname</th><th>Age</th><th>Speciality</th><th>AMKA</th></tr>
                        <tr><td><%= session.getAttribute("doctorusername") %></td><td><%= session.getAttribute("name") %></td><td><%= session.getAttribute("surname") %></td><td><%= session.getAttribute("age") %></td><td><%= session.getAttribute("specialty")%></td><td><%= session.getAttribute("doctorAMKA") %></td></tr>
                    </table>

                </div>

                <br>

                <div class="container">
                    <label><pre><b style="color:#012A6C"> Please select what do you want to do!</b></pre></label>
                </div>

                <center>

                    <div class="container">

                        <!--In every button click the form is submitted and the value of the pressed button is being passed in DoctorServlet-->

                        <button id="buttons" name="doctor_action" value="set availability"  type="submit">Set availability</button>
                        <button id="buttons" name="doctor_action" value="view appointments" type="submit">View scheduled appointments</button>
                        <button id="buttons" name="doctor_action" value="logout"            type="submit">Logout</button>

                        <br>
                        <br>

                    </div>

                </center>

            </form>

        </article>

    </body>

</html>
