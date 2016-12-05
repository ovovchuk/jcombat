DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS session_stats;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts
(
  id            CHAR(36) PRIMARY KEY                   NOT NULL,
  username      VARCHAR(255)                           NOT NULL,
  first_name    VARCHAR(255)                           NOT NULL,
  last_name     VARCHAR(255)                           NOT NULL,
  password      VARCHAR(255)                           NOT NULL,
  date_created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP    NOT NULL,
  date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP    NOT NULL,
  enabled       BIT(1) DEFAULT 0                       NOT NULL,
  UNIQUE INDEX accounts_username_uindex (username)
)
  DEFAULT CHARSET = utf8;

CREATE TABLE authorities
(
  username  VARCHAR(255) NOT NULL,
  authority VARCHAR(255) NOT NULL,
  UNIQUE INDEX authorities_username_authority_uindex (username, authority),
  CONSTRAINT authorities_users_username_fk FOREIGN KEY (username) REFERENCES accounts (username)
)
  DEFAULT CHARSET = utf8;

CREATE TABLE answers
(
  id            CHAR(36) PRIMARY KEY                NOT NULL,
  answer_pos    CHAR(1)                             NOT NULL,
  answer        VARCHAR(1000)                       NOT NULL,
  is_correct    TINYINT(1)                          NOT NULL,
  date_created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  created_by    CHAR(36)                            NOT NULL,
  modified_by   CHAR(36)                            NOT NULL,
  INDEX answers_created_by_index (created_by),
  INDEX answers_modified_by_index (modified_by),
  CONSTRAINT answers_accounts_id_fc1 FOREIGN KEY (created_by) REFERENCES accounts (id),
  CONSTRAINT answers_accounts_id_fc2 FOREIGN KEY (modified_by) REFERENCES accounts (id)
)
  DEFAULT CHARSET = utf8;

CREATE TABLE questions
(
  id            CHAR(36) PRIMARY KEY                              NOT NULL,
  question      VARCHAR(2000)                                     NOT NULL,
  answer_id     CHAR(36)                                          NOT NULL,
  date_created  TIMESTAMP DEFAULT CURRENT_TIMESTAMP               NOT NULL,
  date_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP               NOT NULL,
  created_by    CHAR(36)                                          NOT NULL,
  modified_by   CHAR(36)                                          NOT NULL,
  is_active     TINYINT(1)                                        NOT NULL,
  INDEX questions_answer_id_index (answer_id),
  INDEX questions_created_by_index (created_by),
  INDEX questions_modified_by_index (modified_by),
  CONSTRAINT questions_answers_id_fc FOREIGN KEY (answer_id) REFERENCES answers (id),
  CONSTRAINT questions_accounts_id_fc1 FOREIGN KEY (created_by) REFERENCES accounts (id),
  CONSTRAINT questions_accounts_id_fc2 FOREIGN KEY (modified_by) REFERENCES accounts (id)
)
  DEFAULT CHARSET = utf8;

CREATE TABLE session_stats
(
  id           CHAR(36) PRIMARY KEY                NOT NULL,
  user1_health TINYINT UNSIGNED                    NOT NULL,
  user2_health TINYINT UNSIGNED                    NOT NULL,
  question_id  CHAR(36)                            NOT NULL,
  answer_id    CHAR(36)                            NOT NULL,
  answered_by  CHAR(36)                            NOT NULL,
  date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  INDEX session_stats_question_id_index (question_id),
  INDEX session_stats_answer_id_index (answer_id),
  INDEX session_stats_answered_by_index (answered_by),
  CONSTRAINT session_stats_questions_id_fc FOREIGN KEY (question_id) REFERENCES questions (id),
  CONSTRAINT session_stats_answers_id_fc FOREIGN KEY (answer_id) REFERENCES answers (id),
  CONSTRAINT session_stats_accounts_id_fc FOREIGN KEY (answered_by) REFERENCES accounts (id)
)
  DEFAULT CHARSET = utf8;

CREATE TABLE sessions
(
  id           CHAR(36) PRIMARY KEY                NOT NULL,
  name         VARCHAR(255)                        NOT NULL,
  user_id1     CHAR(36)                            NOT NULL,
  user_id2     CHAR(36)                            NOT NULL,
  date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  date_end     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  status       VARCHAR(255)                        NOT NULL,
  INDEX sessions_user_id1_index (user_id1),
  INDEX sessions_user_id2_index (user_id2),
  CONSTRAINT sessions_accounts_id_fc1 FOREIGN KEY (user_id1) REFERENCES accounts (id),
  CONSTRAINT sessions_accounts_id_fc2 FOREIGN KEY (user_id2) REFERENCES accounts (id)
)
  DEFAULT CHARSET = utf8;
