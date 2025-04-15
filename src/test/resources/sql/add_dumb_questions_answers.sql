INSERT INTO questions (content, score, quiz)
VALUES
    ('Is this good question?1', 1, 1),
    ('Is this good question?2', 2, 1),
    ('Is this good question?3', 0.5, 1);


INSERT INTO answers (content, is_correct, question)
VALUES
    ('Is this answer right?1', true, 1),
    ('Is this answer right?2', false, 1),
    ('Is this answer right?3', false, 1),

    ('Is this answer right?1', true, 2),
    ('Is this answer right?2', false, 2),
    ('Is this answer right?3', false, 2),

    ('Is this answer right?1', true, 3),
    ('Is this answer right?2', false, 3),
    ('Is this answer right?3', true, 3);