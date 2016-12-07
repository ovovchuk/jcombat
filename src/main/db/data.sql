INSERT INTO accounts (id, username, first_name, last_name, password)
VALUES (UUID(), 'ovovchuk', 'Oleksandr', 'Vovchuk', '123456');

INSERT INTO accounts (id, username, first_name, last_name, password)
VALUES (UUID(), 'lmessi', 'Lionel', 'Messi', '123456');

INSERT INTO authorities (username, authority) VALUES ('ovovchuk', 'ROLE_ADMIN');
INSERT INTO authorities (username, authority) VALUES ('ovovchuk', 'ROLE_USER');
INSERT INTO authorities (username, authority) VALUES ('lmessi', 'ROLE_USER');

SELECT id INTO @ovId FROM accounts WHERE username = 'ovovchuk';
SELECT id INTO @lmId FROM accounts WHERE username = 'lmessi';

INSERT INTO questions (id, question, created_by, modified_by, is_active)
VALUES (UUID(), 'What is the size of boolean variable?', @ovId, @ovId, 1);

SELECT id INTO @qId FROM questions WHERE question = 'What is the size of boolean variable?';

INSERT INTO answers (id, answer_pos, answer, is_correct, question_id, created_by, modified_by)
VALUES (UUID(), 'A', '8 bit', 0, @qId, @ovId, @ovId);

INSERT INTO answers (id, answer_pos, answer, is_correct, question_id, created_by, modified_by)
VALUES (UUID(), 'B', '16 bit', 1, @qId, @ovId, @ovId);

INSERT INTO answers (id, answer_pos, answer, is_correct, question_id, created_by, modified_by)
VALUES (UUID(), 'C', '32 bit', 0, @qId, @ovId, @ovId);

INSERT INTO answers (id, answer_pos, answer, is_correct, question_id, created_by, modified_by)
VALUES (UUID(), 'D', 'not precisely defined', 0, @qId, @ovId, @ovId);

INSERT INTO sessions (id, name, user_id1, user_id2, date_created, date_end, status)
VALUES (UUID(), 'ov vs lm', @ovId, @lmId, CURRENT_TIMESTAMP, TIMESTAMPADD(MINUTE, 5, CURRENT_TIMESTAMP()), 'CREATED');

SELECT id INTO @sesId FROM sessions WHERE name = 'ov vs lm';
SELECT id INTO @aId FROM answers WHERE answer = '16 bit';

INSERT INTO session_items (id, user_health1, user_health2, question_id, answer_id, answered_by, session_id)
VALUES (UUID(), 100, 90, @qId, @aId, @ovId, @sesId);