<%--
  Created by IntelliJ IDEA.
  User: yarek
  Date: 19.04.25
  Time: 23:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create</title>

  <style>
    body {
      font-family: Arial, sans-serif;
      background: #f0f2f5;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0;
      overflow: auto;
    }
    .create-form {
      background-color: #fff;
      margin-top: 20px;
      padding: 40px 30px;
      border-radius: 10px;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
      display: flex;
      flex-direction: column;
      row-gap: 20px;
      overflow: auto;
    }
    input, textarea {
      border-radius: 10px;
    }
    button {
      border-radius: 20px;
      background-color: #8eb6e7;
    }
    .create-form > div {
      display: flex;
      flex-direction: column;
    }
    .questions-container {
      display: flex;
      flex-direction: column;
      border-radius: 20px;
      background-color: #fff;
      row-gap: 10px;
    }
    .question-block {
      display: flex;
      flex-direction: column;
      border-radius: 20px;
      background-color: #e9f3ff;
      margin: 10px;
      padding: 10px;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
      row-gap: 10px;
    }
    .answers-container {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-content: center;
      border-radius: 20px;
      padding-left: 20px;
      padding-right: 20px;
      row-gap: 5px;
    }
    .add-answer-btn {
      width: fit-content;
    }
    .answer-block {
      display: inline;
    }



  </style>
</head>
<body>

  <form class="create-form" method="post" action="/create_quiz">

    <div>
      <label for="name">Enter test name:</label>
      <input type="text" id="name" name="name">
    </div>

    <div>
      <label for="description">Enter description</label>
      <textarea id="description" name="description"></textarea>
    </div>

    <div class="questions-container" id="questions">
    </div>
    <button type="button" class="add-question-btn" onclick="addQuestion()">
      Add question
    </button>

  </form>

<script>
  const questionsIndexes = [];

  function addQuestion() {
    let newIndex;
    if (questionsIndexes.length === 0) {
      newIndex = 1;
    } else {
      newIndex = Math.max(...questionsIndexes)+1;
    }

    const newQuestion = document.createElement("div");
    newQuestion.className = "question-block";
    console.log(newIndex);
    newQuestion.innerHTML = `
    <label>Question:</label>
    <input type="text" name="questions[\${newIndex}].text">

    <div class="answers-container" id="answers-\${newIndex}"> </div>
    <button type="button" class="add-answer-btn" onclick="addAnswer(\${newIndex})">Add answer</button>
    `;

    const questions = document.getElementById("questions");
    questions.appendChild(newQuestion);
    addAnswer(newIndex);
    questionsIndexes.push(newIndex);

  }

  function addAnswer(questionIndex) {

    const answers = document.getElementById(`answers-\${questionIndex}`);
    const answerIndex = answers.childElementCount+1;

    const newAnswer = document.createElement("div");
    newAnswer.className = "answer-block";

    newAnswer.innerHTML = `
    <input type="text" name="questions[\${questionIndex}].answers[\${answerIndex}].text">
    <input type="checkbox" name="questions[\${questionIndex}].answers[\${answerIndex}].isCorrect">
    `
    answers.appendChild(newAnswer);
  }

</script>

</body>
</html>
