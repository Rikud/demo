CREATE TABLE IF NOT EXISTS USERS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  nickname VARCHAR(128) UNIQUE,
  fullname VARCHAR(128),
  about TEXT,
  email VARCHAR(128) UNIQUE
);

CREATE TABLE IF NOT EXISTS FORUMS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  tittle VARCHAR(128),
  slug VARCHAR(128) UNIQUE,
  user_id BIGINT REFERENCES USERS(id),
  posts BIGINT DEFAULT 0,
  threads BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS THREADS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  tittle VARCHAR(128),
  author BIGINT  REFERENCES USERS(id),
  forum BIGINT REFERENCES FORUMS(id),
  message TEXT,
  slug VARCHAR(128) UNIQUE,
  created TIMESTAMP WITH TIME ZONE,
  votes BIGINT
);

CREATE TABLE IF NOT EXISTS POSTS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  parent BIGINT REFERENCES POSTS(id),
  author BIGINT REFERENCES USERS(id),
  thread BIGINT REFERENCES THREADS(id),
  forum BIGINT REFERENCES FORUMS(id),
  message TEXT,
  isedited BOOLEAN,
  created TIMESTAMP  WITH TIME ZONE,
  path TEXT,
  branch BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS VOTES (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  thread BIGINT REFERENCES THREADS(id),
  vote_maker BIGINT REFERENCES USERS(id),
  voice SMALLINT
);

CREATE INDEX posts_tree_index on posts (path);

ALTER SEQUENCE posts_id_seq RESTART WITH 100000000000;