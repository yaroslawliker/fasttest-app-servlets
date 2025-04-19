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
</head>
<body>

  <form method="post" action="/create_quiz">

    <label for="name">Enter test name:</label>
    <input type="text" id="name" name="name">

    <label for="description">Enter description</label>
    <input type="text" id="description" name="description">


    <div class="questions-container" id="questions">
      <button type="button" class="add-question-btn" onclick="addQuestion()">
        Add question
      </button>

    </div>

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
    newAnswer.className = "answers-container";

    newAnswer.innerHTML = `
    <span class="answer-number">Answer #\${answerIndex}</span>
    <input type="text" name="questions[\${questionIndex}].answers[\${answerIndex}].text">
    <input type="checkbox" name="questions[\${questionIndex}].answers[\${answerIndex}].isCorrect">
    `
    answers.appendChild(newAnswer);
  }

</script>

</body>
</html>
