-- Version: 1
--
-- This is a db visualization file in terms of dbdiagram.io
-- Probably newest version: https://dbdiagram.io/d/670e7cc797a66db9a30f928a

Table users {
  id serial [primary key]
  username varchar(30)
  role varchar(10)
  password varchar(30)
}


Table tests {
  id serial [primary key]
  owner integer [ref: > users.id]
}

Table questions {
  id serial [primary key]
  content text
  score integer
  test integer [ref: > tests.id]
}

Table answers {
  id serial [primary key]
  content text
  isCorrect bool
  question integer [ref: > questions.id]
}

Table results {
  id serial [primary key]
  user integer [ref: > users.id]
  test integer [ref: > tests.id]
  score integer
}
