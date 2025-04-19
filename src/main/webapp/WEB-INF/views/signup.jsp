<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Sign Up</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f0f2f5;
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
        }

        .signup-container {
            background-color: #fff;
            padding: 40px 30px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            width: 300px;
        }

        .signup-container h2 {
            margin-bottom: 20px;
            color: #333;
            text-align: center;
        }

        label {
            display: block;
            margin-bottom: 6px;
            color: #555;
        }
        label.radiolabel {
            display: inline;
            color: #555;
        }

        input[type="text"],
        input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 6px;
            box-sizing: border-box;
            font-size: 14px;
        }

        input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #4a90e2;
            border: none;
            border-radius: 6px;
            color: white;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        input[type="submit"]:hover {
            background-color: #357abd;
        }

        .error {
            color: red;
            font-size: 13px;
            margin-bottom: 10px;
            display: none;
        }
    </style>
</head>
<body>
<div class="signup-container">
    <h2>Create Account</h2>
    <form method="post" action="/signup" onsubmit="return validateForm()">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" required />

        <label for="password">Password</label>
        <input type="password" id="password" name="password" required />

        <label for="confirmPassword">Confirm Password</label>
        <input type="password" id="confirmPassword" name="confirmPassword" required />

        <label>Choose your role:</label>
        <input type="radio" id="user" name="role" value="user" checked>
        <label for="user" class="radiolabel">Student</label>
        <br>
        <input type="radio" id="teacher" name="role" value="teacher">
        <label for="teacher" class="radiolabel">Teacher</label>

        <ul class="error" id="error-message">
            <c:if test="${userExistsError}">
                <li>This username is already taken.</li>
            </c:if>
        </ul>

        <input type="submit" value="Sign Up" />
    </form>
</div>

<script>

    const error = document.getElementById("error-message")
    if (error.children.length > 0) {
        error.style.display = "block";
    }

    function addErrorMsg(error, text) {
        const msg = document.createElement("li");
        msg.textContent = text;
        error.append(msg);
    }

    function validateForm() {
        const username = document.getElementById("username").value;
        const pass = document.getElementById("password").value;
        const confirm = document.getElementById("confirmPassword").value;

        while (error.hasChildNodes()) {
            error.removeChild(error.firstChild);
        }

        if (username.length < 5) {
            addErrorMsg(error, "User name must be at least 5 symbols!")
        }

        if (pass.length < 6 || confirm.length < 6) {
            addErrorMsg(error, "Password must be at least 6 symbols.")
        }

        if (pass !== confirm) {
            addErrorMsg(error, "Passwords do not match.")
        }

        if (error.children.length !== 0) {

            error.style.display = "block";
            return false;
        }

        error.style.display = "none";
        return true;
    }
</script>
</body>
</html>
