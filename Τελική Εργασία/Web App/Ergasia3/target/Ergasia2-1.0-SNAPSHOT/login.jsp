<!-- This is the sign-up page. Using this page, patients are now able to sign-up to the application. -->
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="en">

    <head>

        <%
            //if any user session has been timed out, we are being redirected to this page with a message attribute
            String msg;
            if(request.getAttribute("message") != null) //if user has been redirected
            {
                msg = "<p>Session time out</p>";
            }
            else //else user has not been redirected
                msg = "";
        %>

        <h1><%= msg %></h1> <!-- Show the above initialized message -->

        <title>Doctor appointments: login</title>
        <meta charset="utf-8">

        <style>

            /* style rules for the paragraph in this page */
            p
            {
                color:white;
                font-size:20px;
                padding: 10px 18px;
                margin: 8px 0;
                width: auto;
            }

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
                width: 100%;
                padding: 12px 20px;
                margin: 8px 0;
                display: inline-block;
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

            /* set extra style for the cancel button */
            .cancelbtn
            {
                width: auto;
                padding: 10px 18px;
                background-color: #f44336;
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

            /* set the forgot password text */
            span.psw
            {
                float: right;
                padding-top: 16px;
            }

            /* style rules for the radio buttons */
            input[type=radio] {
                height: 16px;
                width: 15px;
            }

            /* set styles for span and cancel button on small screens */
            @media screen and (max-width: 300px)
            {
                span.psw
                {
                    display: block;
                    float: none;
                }

                .cancelbtn
                {
                    width: 100%;
                }
            }

            /* style rules when hyperlinks are pressed */
            a:visited
            {
                color: #012A6C;
            }

            /* style the hyperlinks in the nav section */
            a
            {
                font-size:16px;
                color: #012A6C;
            }

        </style>

    </head>



    <body>

        <form action="" method="post" name="login" id="login">

            <input type="hidden" name="admin_action" value="login">
            <input type="hidden" name="patient_action" value="6">
            <input type="hidden" name="doctor_action"  value="login">

            <div class="imgcontainer">
                <img src="img/logo1.png" alt="logo_image" class="avatar">
            </div>

            <div class="container">
                <label><b style="color:#012A6C">Username: *</b></label>
                <input type="text" placeholder="Enter Username" name="username" required>

                <label><b style="color:#012A6C">Password: *</b></label>
                <input type="password" placeholder="Enter Password" name="password" required>

                <label><b style="color:#012A6C">Category: *</b></label>
                <input type="radio" id="Admin"   name="category" value="Admin"   required onchange="AdminAction();" ><label for="Admin">Admin</label>
                <input type="radio" id="Patient" name="category" value="Patient" required onchange="PatientAction();"><label for="Patient">Patient</label>
                <input type="radio" id="Doctor"  name="category" value="Doctor"  required onchange="DoctorAction();"><label for="Doctor">Doctor</label>
            </div>

            <div class="container" style="background-color:#f1f1f1">
                <button type="submit">Login</button>
                <button type="reset" class="cancelbtn">Reset</button>
                <span class="psw">Don't have an account yet? <a href="register.jsp">register</a></span>
            </div>

        </form>

        <br>
    </body>

    <script>
        const form = document.getElementById("login");

        //functions that run after every radio button click.Depending on the radio button selected, the form action changes
        //in order for the user to be redirected to the corresponding servlet
        function AdminAction()
        {
            if (document.getElementById("Admin").checked)
                form.action = "admin";
        }

        function PatientAction()
        {
            if (document.getElementById("Patient").checked)
                form.action = "patient";
        }

        function DoctorAction()
        {
            if (document.getElementById("Doctor").checked)
                form.action = "doctor";
        }
    </script>

</html>