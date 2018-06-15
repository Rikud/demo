CREATE TABLE IF NOT EXISTS USERS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  nickname VARCHAR(128),
  nickname_lower VARCHAR(128) UNIQUE,
  fullname VARCHAR(128),
  about TEXT,
  email VARCHAR(128) UNIQUE
);

CREATE INDEX IF NOT EXISTS users_lower_nickname_index on USERS(nickname_lower);

CREATE TABLE IF NOT EXISTS FORUMS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  tittle VARCHAR(128),
  slug VARCHAR(128),
  slug_lower VARCHAR(128) UNIQUE,
  user_id BIGINT ,
  posts BIGINT DEFAULT 0,
  threads BIGINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS forums_lower_slug_index on FORUMS (slug_lower);

CREATE TABLE IF NOT EXISTS THREADS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  tittle VARCHAR(128),
  author BIGINT REFERENCES users(id),
  forum BIGINT REFERENCES forums(id),
  message TEXT,
  slug VARCHAR(128),
  slug_lower VARCHAR(128) UNIQUE,
  created TIMESTAMP WITH TIME ZONE,
  votes BIGINT
);

CREATE INDEX IF NOT EXISTS threads_lower_slug_index on THREADS(slug_lower);

CREATE TABLE IF NOT EXISTS POSTS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  parent BIGINT ,
  author BIGINT ,
  thread BIGINT ,
  forum BIGINT ,
  message TEXT,
  isedited BOOLEAN,
  created TIMESTAMP  WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS posts_parent_index on posts (parent);

CREATE TABLE IF NOT EXISTS BRANCHS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  branch BIGINT,
  post BIGINT UNIQUE
);

CREATE INDEX IF NOT EXISTS branchs_branch_index on BRANCHS(branch);

CREATE TABLE IF NOT EXISTS VOTES (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  thread BIGINT ,
  vote_maker BIGINT ,
  voice SMALLINT,
  CONSTRAINT one_owner_thread_pair UNIQUE (vote_maker, thread)
);

CREATE INDEX IF NOT EXISTS votes_thread_index on VOTES(thread);

CREATE INDEX IF NOT EXISTS votes_vote_maker_index on VOTES(vote_maker);

/*ALTER SEQUENCE posts_id_seq RESTART WITH 100000000000;*/

CREATE OR REPLACE FUNCTION deleteall()
  RETURNS void
LANGUAGE plpgsql
AS $$
begin
  delete from votes; delete from posts; delete from threads; delete from forums; delete from users;
end;
$$;

CREATE OR REPLACE FUNCTION create_or_update_vote(u_id INTEGER, t_id INTEGER, v INTEGER)
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

CREATE OR REPLACE FUNCTION search_forum_id_by_id_or_slug(slugOrId BIGINT)
  RETURNS BIGINT AS $$
  BEGIN
    return slugOrId;
  END;$$
LANGUAGE plpgsql;

CREATE  OR REPLACE FUNCTION search_forum_id_by_id_or_slug(slugOrId VARCHAR)
  RETURNS BIGINT AS $$
BEGIN
  return (SELECT id from FORUMS WHERE slug_lower = slugOrId);
END;$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION search_thread_id_by_id_or_slug(slugOrId BIGINT)
  RETURNS BIGINT AS $$
BEGIN
  return slugOrId;
END;$$
LANGUAGE plpgsql;

CREATE  OR REPLACE FUNCTION search_thread_id_by_id_or_slug(slugOrId VARCHAR)
  RETURNS BIGINT AS $$
BEGIN
  return (SELECT id from THREADS WHERE slug_lower = slugOrId);
END;$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION search_user_id_by_nickname(nickname_for_search VARCHAR)
  RETURNS BIGINT AS $$
BEGIN
  return (SELECT id from USERS WHERE nickname_lower = nickname_for_search);
END;$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_post(parent_id numeric, user_nickname varchar, thread_id numeric, forum_id numeric, message_text TEXT, created TIMESTAMP  WITH TIME ZONE)
  RETURNS BIGINT AS $$
DECLARE branch_id BIGINT;
DECLARE path_ VARCHAR;
DECLARE author_id BIGINT;
DECLARE post_id BIGINT;
BEGIN

  SELECT search_user_id_by_nickname(user_nickname) INTO author_id;

  IF author_id ISNULL THEN
    RETURN -1;
  END IF;

  INSERT INTO POSTS (parent, author, thread, forum, message, created)
  VALUES (parent_id, author_id, thread_id, forum_id, message_text, now()) RETURNING id INTO post_id;
  IF parent_id ISNULL THEN
    SELECT nextval(pg_get_serial_sequence('BRANCHS', 'id')) INTO branch_id;
    INSERT INTO BRANCHS(branch, post) values(branch_id, post_id);
  ELSE
    SELECT branch FROM BRANCHS WHERE post = parent_id INTO branch_id;
    INSERT INTO BRANCHS(branch, post) values(branch_id, post_id);
  END IF;
  RETURN post_id;
END;$$
LANGUAGE plpgsql;