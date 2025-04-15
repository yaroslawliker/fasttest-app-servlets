CREATE TABLE quizzes (
    id serial PRIMARY KEY,
    name varchar(100),
    description text,
    creation_date date,
    owner integer REFERENCES users(id)
);

CREATE TABLE questions (
    id serial PRIMARY KEY,
    content text,
    score numeric,
    quiz integer REFERENCES quizzes(id)
);

CREATE TABLE answers (
    id serial PRIMARY KEY,
    content text,
    is_correct bool,
    question integer REFERENCES questions(id)
);