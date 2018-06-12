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

CREATE INDEX IF NOT EXISTS forums_lower_slug_index on FORUMS (lower(slug));

CREATE INDEX IF NOT EXISTS posts_path_index on posts (path);

CREATE INDEX IF NOT EXISTS posts_branch_index on posts (branch);

CREATE INDEX IF NOT EXISTS posts_branch_is_null on posts (branch) WHERE parent ISNULL AND branch > 0;

CREATE INDEX IF NOT EXISTS posts_parent_index on posts (parent);

CREATE INDEX IF NOT EXISTS users_lower_nickname_index on USERS(lower(nickname));

CREATE INDEX IF NOT EXISTS threads_lower_slug_index on THREADS(lower(slug));

CREATE INDEX IF NOT EXISTS votes_thread_index on VOTES(thread);

CREATE INDEX IF NOT EXISTS votes_vote_maker_index on VOTES(thread, vote_maker);

ALTER SEQUENCE posts_id_seq RESTART WITH 100000000000;

CREATE OR REPLACE FUNCTION deleteall()
  RETURNS void
LANGUAGE plpgsql
AS $$
begin
  delete from votes; delete from posts; delete from threads; delete from forums; delete from users;
end;
$$;

CREATE OR REPLACE FUNCTION create_or_update_vote(u_id INTEGER, thread INTEGER, v INTEGER)
  RETURNS VOID AS '
BEGIN
  INSERT INTO votes (vote_maker, thread, voice) VALUES (u_id, t_id, v)
  ON CONFLICT (vote_maker, thread)
    DO UPDATE SET voice = v;
  UPDATE threads
  SET votes = (SELECT SUM(voice)
               FROM votes
               WHERE thread = t_id)
  WHERE id = t_id;
END;'
LANGUAGE plpgsql;