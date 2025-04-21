<%--
  Created by IntelliJ IDEA.
  User: yarek
  Date: 21.04.25
  Time: 16:09
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <title>Tests</title>
    <style>

        * {
            color: #3d3d3d;
        }

        body {
            font-family: Arial, sans-serif;
            background: #f0f2f5;
            display: flex;
            align-items: flex-start;
            justify-content: center;
            margin: 0;
            overflow: auto;
        }
        a {
            text-decoration: none;
        }
        .container {
            margin-top: 20px;
            padding: 40px 30px;
            border-radius: 10px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-direction: column;
            align-items: center;
            row-gap: 10px;
        }
        .content {
            background-color: #fff;
            overflow: auto;
        }

        .quizzes-name {
            font-weight: bold;
        }

        .quizzes-container {
            background-color: transparent;
        }
        .quiz-block {
            background-color: #e9f3ff;
            padding: 5px 10px;
        }
        .line {
            border-bottom: 1px solid gray;
            width: 95%;
        }
        .name {
            font-weight: bold;
            font-size: small;
        }
        .description {
            max-width: 350px;
            font-size: smaller;
            align-self: flex-start;
        }
        .author-date {
            width: 90%;
        }
        .author-date > div {
            display: inline-block;
            font-size: x-small;
            color: #6e6e6e;
        }
        .author-date-gap {
            width: 100px;
        }
        .author {
            float: left;
        }
        .date {
            float: right;
        }

    </style>
</head>
<body>
    <%@include file="elements/navbar.jsp"%>

    <div class="container content">
      <span class="quizzes-name">Public tests</span>

      <div class="quizzes-container">

        <c:forEach var="i" begin="0" end="${requestScope.quizzes.size()-1}">
          <a class="container quiz-block" href="/tests/${requestScope.quizzes[i].id}">

            <div class="name">${requestScope.quizzes[i].name}</div>

            <div class="description">${requestScope.quizzes[i].description}</div>

              <div class="line"></div>

              <div class="author-date">
                  <div class="author">${requestScope.usernames[i]}</div>
                  <div class="author-date-gap"></div>
                  <div class="date">${requestScope.quizzes[i].creationDate}</div>
              </div>

          </a>
        </c:forEach>

      </div>

    </div>

</body>
</html>
