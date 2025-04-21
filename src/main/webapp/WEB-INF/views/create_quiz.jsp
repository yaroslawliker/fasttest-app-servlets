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
    .create-form {
      min-width: 650px;
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
      color: #313131;
    }
    button {
      border-radius: 20px;
      background-color: #8eb6e7;
      border-width: 0px;
      box-shadow: rgba(0, 0, 0, .2) 0 4px 4px 0;
      font-weight: lighter;
      padding: 5px 15px;
    }
    .submit-btn {
      width: fit-content;
      padding: 20px;
      background-color: #73e763;
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
    .question-label {
      color: #6e6e6e;
      font-size: 16px;
    }
    .question-text-score {
      display: flex;
      gap: 10px;
    }
    .question-text {
      flex-shrink: 1;
      flex-grow: 1;
      border-width: 0;
      border-bottom-width: 1px;
      background-color: rgba(0, 0, 0, 0);
    }
    .question-score {
      flex-grow: 0;
      flex-shrink: 0;
      text-align: center;
      border-width: 0;
      border-bottom-width: 1px;
      background-color: transparent;
      width: 100px;
    }
    .question-label-delete {
      display: inline-block;
    }
    .add-question-btn {
      width: 50%;
      align-self: center;
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
      display: flex;
      gap: 20px;
      justify-content: space-between;
    }
    .answer-text {
      border-width: 0;
      background-color: rgba(100%, 100%,100%,75%);
      border-bottom-width: 1px;
      flex-shrink: 1;
      flex-grow: 1;
    }
    .is-correct-checkbox {
      flex-shrink: 0;
      flex-grow: 0;
    }
    .delete-answer-btn, .delete-question {
      background-color: #ff9d9d;
      flex-shrink: 0;
      flex-grow: 0;
      font-size: 16px;
      border-radius: 5px;
      padding: 1px 10px;
    }
    .delete-question {
      padding-top: 7px;
      padding-bottom: 7px;
      float: right;
    }



  </style>
</head>
<body>

  <form class="create-form" method="post" action="/create-quiz" onsubmit="return processForm()">

    <div>
      <label for="name">Enter test name:</label>
      <input type="text" id="name" name="name" required>
    </div>

    <div>
      <label for="description">Enter description</label>
      <textarea id="description" name="description"></textarea>
    </div>

    <%--Technical field, not for user--%>
    <input id="number-of-questions" name="number-of-questions" hidden="hidden">

    <div class="questions-container" id="questions"></div>
    <button type="button" class="add-question-btn" onclick="addQuestion()">
      Add question
    </button>

    <button type="submit" class="submit-btn">
      Create test
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
    <div class="question-label-delete">
      <label class="question-label">Question:</label>
      <button type="button" class="delete-question" onclick="deleteQuestion(\${newIndex}, this)">X</button>
    </div>

    <div class="question-text-score">
      <input type="text" class="question-text" name="questions[\${newIndex}].text" placeholder="Type your question" required>
      <input type="number" step="0.01" class="question-score" name="questions[\${newIndex}].score" placeholder="score">
    </div>

    <div class="answers-container" id="answers-\${newIndex}"></div>

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
    newAnswer.name = `questions[\${questionIndex}].answers[\${answerIndex}]`


    newAnswer.innerHTML = `
    <input type="text" class="answer-text" name="not calculated" placeholder="Type answer" required>
    <input type="checkbox" class="is-correct-checkbox" name="not calculated">
    <button type="button" class="delete-answer-btn" onclick="deleteAnswer(\${questionIndex}, this)">X</button>
    `
    answers.appendChild(newAnswer);
    enumerateAnswersName(questionIndex);
  }

  function enumerateAnswersName(questionIndex) {
    const answers = document.getElementById(`answers-\${questionIndex}`);
    const answersAmount = answers.childElementCount;
    console.log("Enumeration")
    for (let i = 1; i < answersAmount+1; i++) {
      console.log(`i = \${i}`)
      const answerText = answers.getElementsByClassName("answer-text").item(i-1);
      answerText.name = `questions[\${questionIndex}].answers[\${i}].text`;

      const answerCheckbox = answers.getElementsByClassName("is-correct-checkbox").item(i-1);
      answerCheckbox.name = `questions[\${questionIndex}].answers[\${i}].isCorrect`
    }
  }

  function deleteAnswer(questionIndex, answerBtn) {
    const answers = document.getElementById(`answers-\${questionIndex}`);
    const answer = answerBtn.parentNode;
    answers.removeChild(answer);
    enumerateAnswersName(questionIndex);
  }

  function deleteQuestion(questionIndex, questionBtn) {
    const questions = document.getElementById("questions");
    questions.removeChild(questionBtn.parentNode.parentNode);
    questionsIndexes.splice(questionsIndexes.indexOf(questionIndex), 1);
  }

  function getLastQuestionIndex() {
    const questions = document.getElementById("questions");
    if (questions.children.length === 0) {
      return -1;
    }
    const lastQuestion = questions.children.item(questions.children.length-1);
    const text = lastQuestion.getElementsByClassName("question-text")[0];
    const name = text.name;
    // questions[..].answers[..]
    const idStart = 10;
    const idEnd = name.indexOf("].text");
    const id = name.substring(idStart, idEnd);
    return Number(id);
  }

  function processForm() {
    let id = getLastQuestionIndex();

    if (id === -1) {
      alert("Form must have at least one question");
      return false;
    }

    if (!isEveryQuestionHasAtLeastOneAnswer()) {
      alert("Every question must have at least one answer")
      return false;
    }

    const numberOfQuestions = document.getElementById("number-of-questions");
    numberOfQuestions.value = id;

    return true;
  }

  function isEveryQuestionHasAtLeastOneAnswer() {
    const questions = document.getElementById("questions");
    for (let i = 0; i < questions.children.length; i++) {
      const question = questions.children.item(i);
      const answers = question.getElementsByClassName("answers-container")[0];
      if (answers.children.length < 1) {
        return false;
      }
    }
    return true;
  }

</script>

</body>
</html>
