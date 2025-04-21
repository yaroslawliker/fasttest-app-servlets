<%--
  Created by IntelliJ IDEA.
  User: yarek
  Date: 21.04.25
  Time: 20:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            flex-direction: column;
            align-items: center;
            justify-content: center;
            margin: 0;
            overflow: auto;
        }
        a {
            text-decoration: none;
        }
        .container {
            margin-top: 20px;
            padding: 30px 20px 10px;
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

        .questions {
            font-size: x-small;
        }

        .start-button {
            padding: 50px;
            width: fit-content;
            background-color: #8eb6e7;
            border-radius: 30px;
        }

    </style>
</head>
<body>
<%@include file="elements/navbar.jsp"%>

<div class="container content">
    <span class="quizzes-name">Test ${requestScope.quiz.id} </span>

        <div class="name">${requestScope.quiz.name}</div>

        <div class="description">${requestScope.quiz.description}</div>

        <div class="line"></div>

        <div class="questions">Questions: ${requestScope.questionAmount}</div>

        <div class="line"></div>

        <div class="author-date">
            <div class="author">${requestScope.username}</div>
            <div class="author-date-gap"></div>
            <div class="date">${requestScope.quiz.creationDate}</div>
        </div>

</div>

<form action="/tests/${requestScope.quiz.id}/preview" method="post">
    <input type="text" name="quizId" hidden="hidden" value="${requestScope.quiz.id}">
    <button type="submit" class="container start-button">
        Start test!
    </button>
</form>

</body>
</html>
