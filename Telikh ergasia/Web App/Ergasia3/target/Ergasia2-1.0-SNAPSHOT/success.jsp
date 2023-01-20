<!-- This page is appeared when a user successfully registers in the application -->
<%@ page import="java.io.PrintWriter" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>

<html lang="en">

    <head>

        <meta charset="utf-8">
        <meta http-equiv = "refresh" content = "5; url = <%= request.getAttribute("redirect") %>" />
        <title>Doctor appointments: register successful</title>

        <style>

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

            /* style rules for the image */
            img
            {
                width:400px;
                height:400px;
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

        <center><img src="img/check.png" alt="register was successful"></center>

        <br>
        <br>

        <%
            //Depending on the page that calls success.jsp we get the corresponding message.
            String text;

            if(request.getAttribute("redirect").equals("register.jsp")) //if redirect page is register.jsp, that means a patient has just been self registered
                text="You have successfully registered to the application, patient. Welcome!";
            else //else, we get the action attribute from the sender page in order to show the correct message
                text="Successfully " + request.getAttribute("action") + ".";
        %>

        <div class="navbar">
            <p><pre>  <%= text %></pre></p> <!-- show message -->
        </div>

    </body>

</html>
