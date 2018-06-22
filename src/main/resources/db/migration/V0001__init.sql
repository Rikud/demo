CREATE TABLE IF NOT EXISTS USERS (
  id SERIAL NOT NULL PRIMARY KEY,
  nickname VARCHAR(128),
  nickname_lower VARCHAR(128) UNIQUE,
  fullname VARCHAR(128),
  about TEXT,
  email VARCHAR(128) UNIQUE
);

CREATE INDEX IF NOT EXISTS users_lower_nickname_index on USERS(nickname_lower);
CREATE INDEX IF NOT EXISTS users_lower_nickname_transfer_index on USERS((nickname_lower::BYTEA));

CREATE TABLE IF NOT EXISTS FORUMS (
  id SERIAL NOT NULL PRIMARY KEY,
  tittle VARCHAR(128),
  slug VARCHAR(128),
  slug_lower VARCHAR(128) UNIQUE,
  user_id INTEGER ,
  posts INTEGER DEFAULT 0,
  threads INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS forums_lower_slug_index on FORUMS (slug_lower);

CREATE TABLE IF NOT EXISTS THREADS (
  id SERIAL NOT NULL PRIMARY KEY,
  tittle VARCHAR(128),
  author INTEGER REFERENCES users(id),
  forum INTEGER REFERENCES forums(id),
  message TEXT,
  slug VARCHAR(128),
  slug_lower VARCHAR(128) UNIQUE,
  created TIMESTAMP WITH TIME ZONE,
  votes INTEGER
);

CREATE INDEX IF NOT EXISTS threads_forum_index on THREADS(forum);
CREATE INDEX IF NOT EXISTS threads_author_index on THREADS(author);
CREATE INDEX IF NOT EXISTS threads_created_index on THREADS(created);
CREATE INDEX IF NOT EXISTS threads_lower_slug_index on THREADS(slug_lower);
CREATE INDEX IF NOT EXISTS threads_forum_created on THREADS(forum, created);

CREATE TABLE IF NOT EXISTS USERS_IN_FORUMS (
  forum_id INTEGER REFERENCES FORUMS(id),
  user_id INTEGER REFERENCES USERS(id),
  about TEXT,
  email VARCHAR(128),
  fullname VARCHAR(128),
  nickname VARCHAR(128),
  nickname_lower_bytea bytea,
  UNIQUE (forum_id, user_id)
);

CREATE INDEX IF NOT EXISTS users_in_forums_forum_id_index on USERS_IN_FORUMS(forum_id);
CREATE INDEX IF NOT EXISTS users_in_forums_user_id_index on USERS_IN_FORUMS(user_id);
CREATE INDEX IF NOT EXISTS users_in_forums_nickname_lower_index on USERS_IN_FORUMS(nickname_lower_bytea);
CREATE INDEX IF NOT EXISTS users_in_forums_forum_id_nickname_lower_bytea_index on USERS_IN_FORUMS(forum_id, nickname_lower_bytea);

CREATE TABLE IF NOT EXISTS POSTS (
  id SERIAL NOT NULL PRIMARY KEY UNIQUE,
  parent INTEGER DEFAULT 0,
  author INTEGER REFERENCES USERS(id),
  thread INTEGER REFERENCES THREADS(id),
  forum INTEGER REFERENCES FORUMS(id),
  message TEXT,
  isedited BOOLEAN,
  created TIMESTAMP  WITH TIME ZONE,
  path_to_root INTEGER[],
  branch_id INTEGER
);

CREATE INDEX IF NOT EXISTS posts_parent_index on posts (parent);
CREATE INDEX IF NOT EXISTS posts_threads_index on POSTS (thread);
CREATE INDEX IF NOT EXISTS posts_author_index on POSTS (author);
CREATE INDEX IF NOT EXISTS posts_forum_index on POSTS (forum);
CREATE INDEX IF NOT EXISTS posts_branch_id_index on POSTS (branch_id);
CREATE INDEX IF NOT EXISTS posts_created_id_index on POSTS (created);
CREATE INDEX IF NOT EXISTS posts_branch_id_parent_id_index on POSTS (path_to_root, parent, id);
CREATE INDEX IF NOT EXISTS posts_id_author_forum on POSTS(forum, author, id);
CREATE INDEX IF NOT EXISTS posts_thread_parent_id_index ON posts(thread,parent, id);
CREATE INDEX IF NOT EXISTS posts_thread_path_to_root_index ON POSTS(thread, path_to_root);
CREATE INDEX IF NOT EXISTS posts_branch_id_thread_parent_index ON POSTS(branch_id, thread, parent);
CREATE INDEX IF NOT EXISTS posts_thread_id_index ON POSTS(thread, id);
CREATE INDEX IF NOT EXISTS posts_path_to_root_thread_id_index ON POSTS(path_to_root, thread, id);



/*CREATE TABLE IF NOT EXISTS BRANCHES (
  branch_id INTEGER DEFAULT 0,
  post_id INTEGER REFERENCES POSTS(id) UNIQUE,
  path_to_root INTEGER[]
);

CREATE INDEX IF NOT EXISTS branches_branch_id on BRANCHES(branch_id);
CREATE INDEX IF NOT EXISTS branches_path on BRANCHES (path_to_root);*/

CREATE TABLE IF NOT EXISTS VOTES (
  id SERIAL NOT NULL PRIMARY KEY,
  thread INTEGER ,
  vote_maker INTEGER ,
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

CREATE OR REPLACE FUNCTION search_forum_id_by_id_or_slug(slugOrId INTEGER)
  RETURNS INTEGER AS $$
  BEGIN
    return slugOrId;
  END;$$
LANGUAGE plpgsql;

CREATE  OR REPLACE FUNCTION search_forum_id_by_id_or_slug(slugOrId VARCHAR)
  RETURNS INTEGER AS $$
BEGIN
  return (SELECT id from FORUMS WHERE slug_lower = slugOrId);
END;$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION search_thread_id_by_id_or_slug(slugOrId INTEGER)
  RETURNS INTEGER AS $$
BEGIN
  return slugOrId;
END;$$
LANGUAGE plpgsql;

CREATE  OR REPLACE FUNCTION search_thread_id_by_id_or_slug(slugOrId VARCHAR)
  RETURNS INTEGER AS $$
BEGIN
  return (SELECT id from THREADS WHERE slug_lower = slugOrId);
END;$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION search_user_id_by_nickname(nickname_for_search VARCHAR)
  RETURNS INTEGER AS $$
BEGIN
  return (SELECT id from USERS WHERE nickname_lower = nickname_for_search);
END;$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION create_post(parent_id numeric, user_nickname varchar, thread_id numeric,
  forum_id numeric, message_text TEXT, created_time TIMESTAMP WITH TIME ZONE)
  RETURNS NUMERIC AS $$
DECLARE author_id INTEGER;
  postId NUMERIC;
BEGIN
  SELECT search_user_id_by_nickname(user_nickname) INTO author_id;
  IF author_id ISNULL THEN
    RETURN -1;
  END IF;
  INSERT INTO POSTS (parent, author, thread, forum, message, created)
  VALUES (parent_id, author_id, thread_id, forum_id, '', created_time) RETURNING id INTO postId;
  RETURN postId;
END;$$
LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION new_post_trigger() RETURNS TRIGGER AS
$new_post_trigger$
DECLARE path_array INTEGER[];
BEGIN
  IF NEW.parent = 0 THEN
    NEW.path_to_root := ARRAY[NEW.id];
  ELSE
    NEW.path_to_root := array_append((SELECT path_to_root FROM POSTS WHERE id = NEW.parent), NEW.id);
--     INSERT INTO BRANCHES(branch_id, post_id,  path_to_root) values(path_array[1], NEW.id, array_append(path_array, NEW.id));
--     UPDATE POSTS SET branch_id = (SELECT branch_id FROM POSTS WHERE id = NEW.parent) WHERE id = NEW.id;
--     NEW.branch_id = (SELECT branch_id FROM POSTS WHERE id = NEW.parent);
  END IF;
  NEW.branch_id := NEW.path_to_root[1];
  Return NEW;
END;
$new_post_trigger$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_users_of_forum(author_id numeric, forum_id numeric) RETURNS VOID AS
$$
DECLARE author_data USERS;
BEGIN
  SELECT * FROM USERS WHERE id = author_id INTO author_data;
  INSERT INTO USERS_IN_FORUMS (forum_id, user_id, about, email, fullname, nickname, nickname_lower_bytea)
  VALUES (forum_id, author_id, author_data.about, author_data.email, author_data.fullname, author_data.nickname,
        (lower(author_data.nickname))::bytea)
  ON CONFLICT (forum_id, user_id) DO NOTHING;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_posts_trigger ON posts;
CREATE TRIGGER insert_posts_trigger BEFORE INSERT ON posts
FOR EACH ROW EXECUTE PROCEDURE new_post_trigger();

/*forum_id INTEGER REFERENCES FORUMS(id),
user_id INTEGER REFERENCES USERS(id),
about TEXT,
email VARCHAR(128) UNIQUE,
fullname VARCHAR(128),
nickname VARCHAR(128),
UNIQUE (forum_id, nickname)*/

CREATE OR REPLACE FUNCTION new_thread_trigger() RETURNS TRIGGER AS
$new_thread_trigger$
DECLARE author_data USERS;
BEGIN
  SELECT * FROM USERS WHERE id = NEW.author INTO author_data;
  INSERT INTO USERS_IN_FORUMS (forum_id, user_id, about, email, fullname, nickname, nickname_lower_bytea)
  VALUES (NEW.forum, NEW.author, author_data.about, author_data.email, author_data.fullname, author_data.nickname, (lower(author_data.nickname))::bytea) ON CONFLICT DO NOTHING;
  Return NEW;
END;
$new_thread_trigger$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS insert_posts_trigger ON threads;
CREATE TRIGGER insert_posts_trigger AFTER INSERT ON threads
  FOR EACH ROW EXECUTE PROCEDURE new_thread_trigger();

/*CREATE OR REPLACE FUNCTION create_post(parent_id numeric, user_nickname varchar, thread_id numeric,
    forum_id numeric, message_text TEXT, created_time TIMESTAMP WITH TIME ZONE, branch_id numeric)
  RETURNS INTEGER AS $$
  DECLARE branchId INTEGER;
  DECLARE path_ VARCHAR;
  DECLARE author_id INTEGER;
  DECLARE postId INTEGER;
BEGIN
  SELECT search_user_id_by_nickname(user_nickname) INTO author_id;
  IF author_id ISNULL THEN
    RETURN -1;
  END IF;

  INSERT INTO POSTS (parent, author, thread, forum, message, created, branch)
  VALUES (parent_id, author_id, thread_id, forum_id, message_text, created_time, branch_id) RETURNING id INTO postId;
  RETURN postId;
END;$$
LANGUAGE plpgsql;*/