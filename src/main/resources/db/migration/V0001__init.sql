CREATE TABLE IF NOT EXISTS USERS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  nickname VARCHAR(128) UNIQUE,
  nickname_lower VARCHAR(128) UNIQUE,
  fullname VARCHAR(128),
  about TEXT,
  email VARCHAR(128) UNIQUE
);

CREATE TABLE IF NOT EXISTS FORUMS (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  tittle VARCHAR(128),
  slug VARCHAR(128) UNIQUE,
  slug_lower VARCHAR(128) UNIQUE,
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
  slug_lower VARCHAR(128) UNIQUE,
  created TIMESTAMP WITH TIME ZONE,
  votes BIGINT
);

CREATE TABLE IF NOT EXISTS BRANCHS (
  id BIGSERIAL NOT NULL PRIMARY KEY
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
  branch BIGINT REFERENCES BRANCHS(id)
);

CREATE TABLE IF NOT EXISTS VOTES (
  id BIGSERIAL NOT NULL PRIMARY KEY,
  thread BIGINT REFERENCES THREADS(id),
  vote_maker BIGINT REFERENCES USERS(id),
  voice SMALLINT,
  CONSTRAINT one_owner_thread_pair UNIQUE (vote_maker, thread)
);

/*CREATE INDEX IF NOT EXISTS forums_lower_slug_index on FORUMS (lower(slug));*/

CREATE INDEX IF NOT EXISTS forums_lower_slug_index on FORUMS (slug_lower);

CREATE INDEX IF NOT EXISTS posts_path_index on posts (path);

CREATE INDEX IF NOT EXISTS posts_branch_index on posts (branch);

CREATE INDEX IF NOT EXISTS posts_parent_index on posts (parent);

/*CREATE INDEX IF NOT EXISTS users_lower_nickname_index on USERS(lower(nickname));

CREATE INDEX IF NOT EXISTS threads_lower_slug_index on THREADS(lower(slug));
*/

CREATE INDEX IF NOT EXISTS users_lower_nickname_index on USERS(nickname_lower);

CREATE INDEX IF NOT EXISTS threads_lower_slug_index on THREADS(slug_lower);

CREATE INDEX IF NOT EXISTS votes_thread_index on VOTES(thread);

CREATE INDEX IF NOT EXISTS votes_vote_maker_index on VOTES(vote_maker);

ALTER SEQUENCE posts_id_seq RESTART WITH 100000000000;

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

CREATE OR REPLACE FUNCTION create_post(post_id numeric, parent_id numeric, author_id numeric, thread_id numeric, forum_id numeric, message_text TEXT, created TIMESTAMP  WITH TIME ZONE)
  RETURNS VOID AS $$
DECLARE branch_id BIGINT;
BEGIN
  INSERT INTO POSTS (id, parent, author, thread, forum, message, created, path)
  VALUES (post_id, parent_id, author_id, thread_id, forum_id, message_text, now(), '');
  IF parent_id ISNULL THEN
    INSERT INTO BRANCHS values(default) RETURNING id INTO branch_id;
    UPDATE POSTS SET branch = branch_id, path = post_id
    WHERE id = post_id;
  ELSE
    UPDATE POSTS SET path = (
                        SELECT path FROM posts
                        where id = parent_id
                      ) || post_id,
      branch = (select branch FROM posts WHERE id = parent_id)
    WHERE id = post_id;
  END IF;
END;$$
LANGUAGE plpgsql;