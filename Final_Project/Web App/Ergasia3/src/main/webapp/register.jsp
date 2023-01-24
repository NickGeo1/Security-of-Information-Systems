<!-- This is the page where a patient can register to the application -->
<!-- source for the html and css code: https://www.geeksforgeeks.org/html-responsive-modal-login-form/ -->
<%@ page import="com.classes.Users" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>

<html lang="en">

    <head>

        <meta charset="utf-8">
        <title>Doctor appointments: register</title>

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

            /* style rules for the paragraph in this page */
            p
            {
                color:white;
                font-size:18px;
                margin: 4px 0;
                width: auto;
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

            /* style rules for the inputs boxes in the form */
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

            /* set padding to the container class */
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

            /* style rules for the error message box */
            #error_message
            {
                background: #fe8b8e;
                text-align: center;
                font-size: 16px;
                transition: all 0.5s ease;
                width: 97%;
                margin: 8px 0;
                display: inline-block;
                box-sizing: border-box;
                color: white;
                font-weight: bold;
                padding: 0px;
            }

        </style>

    </head>

    <body>

        <form action="patient" method="post" name="signup" onsubmit="return validation()">

            <input type="hidden", value="5", name="patient_action">

            <div class="imgcontainer">
                <img src="img/logo1.png" alt="logo_image" class="avatar">
            </div>

            <center><div id="error_message"></div></center>

            <div class="container">
                <label><b style="color:#012A6C">Hello patient, please create your account!</b></label>
            </div>

            <div class="container">

                <label><b style="color:#012A6C">First name: *</b></label>
                <input type="text" id="fn" placeholder="Enter your first name" name="fn" required>

                <label><b style="color:#012A6C">Last name: *</b></label>
                <input type="text" id="ln" placeholder="Enter your last name" name="ln" required>

                <label><b style="color:#012A6C">Username: *</b></label>
                <input type="text" id="username" placeholder="Enter username" name="username" required>

                <label><b style="color:#012A6C">Password: *</b></label>
                <input type="password" id="password" placeholder="Enter password" name="password" required>

                <label><b style="color:#012A6C">Age: *</b></label>
                <input type="text" id="age" placeholder="Enter your age" name="age" required>

                <label><b style="color:#012A6C">AMKA: *</b></label>
                <input type="text" id="AMKA" placeholder="Enter your AMKA number" name="AMKA" required>

            </div>

            <div class="container" style="background-color:#f1f1f1">
                <button type="submit" onclick="remove_err()">Register</button>
                <button type="reset" class="cancelbtn">Reset</button>
                <span class="psw">Already have an account? <a href="login.jsp">login</a></span>
            </div>

        </form>

        <br>

        <p>Total Users registered: <%= Users.getUsersCount()%></p> <!-- Get the users count from database -->

        <script>

            //in case form still has empty input values, hide previous error (if any)
            function remove_err()
            {
                document.getElementById("error_message").style.display = "none";
            }

            // this function is used for form's validation on client's side
            function validation()
            {
                // get the values of inputs that user gave in the form
                var first_name = document.getElementById("fn").value;
                var last_name = document.getElementById("ln").value;
                var username = document.getElementById("username").value;
                var password = document.getElementById("password").value;
                var age = document.getElementById("age").value;
                var AMKA = document.getElementById("AMKA").value;
                var error_message = document.getElementById("error_message");

                var text; // set a new variable

                // use a new variable for storing the errors during validation
                error_message.style.padding = "10px";
                error_message.style.display = "block"

                // validation check begins

                // check first name
                if (!/^[A-Z][a-z]+$/.test(first_name))
                {
                    text = "First name must contain only letters(at least two) and should begin with a capital letter. " +
                            "No more capital letters are allowed.";
                    error_message.innerHTML = text;
                    return false;
                }

                // check last name
                if (!/^[A-Z][a-z]+$/.test(last_name))
                {
                    text = "Last name must contain only letters(at least two) and should begin with a capital letter. " +
                            "No more capital letters are allowed.";
                    error_message.innerHTML = text;
                    return false;
                }

                // check username
                if (!/^[A-Za-z0-9]{1,12}$/.test(username))
                {
                    text = "Username length must be between 1 and 12 characters. Only alphabetic and numeric characters are allowed";
                    error_message.innerHTML = text;
                    return false;
                }

                // check password
                if (password.length < 4)
                {
                    text = "Password consists of at least 4 characters!";
                    error_message.innerHTML = text;
                    return false;
                }

                // check age
                if (!/^[0-9]+$/.test(age) || age > 119 || age <= 0)
                {
                    text = "Age consist of positive integer numbers and is lower than or equal to 119!";
                    error_message.innerHTML = text;
                    return false;
                }

                // check AMKA
                if (!/^\d{11}$/.test(AMKA))
                {
                    text = "AMKA should be a 11-digit number!";
                    error_message.innerHTML = text;
                    return false;
                }

                error_message.style.display = "none"
                return true; // everything is fine, no errors occurred
            }

        </script>

    </body>

</html>