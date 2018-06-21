package com.example.demo;

import com.dturan.Mapper.ForumMapper;
import com.dturan.Mapper.PostsMapper;
import com.dturan.Mapper.ThreadsMapper;
import com.dturan.Mapper.UsersMapper;
import com.dturan.api.ThreadApi;
import com.dturan.model.Error;
import com.dturan.model.*;
import com.dturan.model.Thread;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.flywaydb.core.internal.util.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/thread/")
@Repository
public class ThreadApiImpl implements ThreadApi {

    @NotNull
    private JdbcTemplate jdbcTemplate;

    private static Logger log = Logger.getLogger(ThreadApiImpl.class.getName());
//    private Connection conn;

//    @NotNull
//    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> branchs;

    @NotNull
    private Integer branch_count;

    @Autowired
    public ThreadApiImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
//        branchs = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>>();
        /*branch_count = 0;*/
    }

    private static final String SEARCH_THREAD_ID_BY_SLUG = "SELECT ID FROM THREADS WHERE slug_lower = ?";

    private static final String SEARCH_TRHEAD_BY_ID =
            "SELECT users.nickname as author,\n" +
                    "  threads.created,\n" +
                    "       forums.slug as forum,\n" +
                    "  threads.id,\n" +
                    "  threads.message,\n" +
                    "  threads.slug,\n" +
                    "  threads.tittle,\n" +
                    "  threads.votes\n" +
                    "FROM threads\n" +
                    "  JOIN users ON users.id = threads.author\n" +
                    "  JOIN forums ON forums.id = threads.forum\n" +
                    "WHERE threads.id = ?";

    private static final String SEARCH_TRHEAD_BY_SLUG =
        "SELECT users.nickname as author,\n" +
        "threads.created, \n" +
        "  forums.slug as forum, \n" +
        "  threads.id, \n" +
        "  threads.message, \n" +
        "  threads.slug, \n" +
        "  threads.tittle, \n" +
        "  threads.votes \n" +
        "FROM forums, threads, users \n" +
        "WHERE threads.slug_lower = ? AND\n" +
        "  forums.id = threads.forum" +
        "  AND users.id = threads.author\n;";

    private static final String SEARCH_USER_ID_BY_NICKNAME = "SELECT ID FROM USERS WHERE nickname_lower = ?;";
    private static final String SEARCH_FORUM_ID_BY_SLUG = "SELECT ID FROM FORUMS WHERE slug_lower = ?;";
    private static final String SEARCH_FORUM_BY_SLUG = "SELECT * FROM FORUMS WHERE slug_lower = ?;";
    private static final String UPDATE_THREAD =
            "UPDATE threads SET tittle = ?, message = ? WHERE id = ?";
    private static final String UPDATE_FORUM_POSTS_COUNTER =
            "UPDATE forums SET posts = posts + ? WHERE id = ?";

    @Override
    @ApiOperation(value = "Создание новых постов", notes = "Добавление новых постов в ветку обсуждения на форум. Все посты, созданные в рамках одного вызова данного метода должны иметь одинаковую дату создания (Post.Created). ", response = Posts.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Посты успешно созданы. Возвращает данные созданных постов в том же порядке, в котором их передали на вход метода. ", response = Posts.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутствует в базе данных. ", response = Posts.class),
        @ApiResponse(code = 409, message = "Хотя бы один родительский пост отсутсвует в текущей ветке обсуждения. ", response = Posts.class) })
    @RequestMapping(value = "/{slugOrId}/create",
        produces = { "application/json" },
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public ResponseEntity<?> postsCreate(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
    @ApiParam(value = "Список создаваемых постов." ,required=true ) @RequestBody Posts posts) {
        DateTime now = DateTime.now().toDateTime(DateTimeZone.UTC);
        Thread thread = null;
        try {
            thread = this.searchThreadByIdOrSlug(slugOrId);
        } catch (Exception e5) {
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутствует в базе данных."), HttpStatus.NOT_FOUND);
        }
        if (posts.isEmpty()) {
            return new ResponseEntity<>(posts, HttpStatus.CREATED);
        }

        //log.info("Создание новых постов");
        Forum forumResult = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG,new Object[]{thread.getForum().toLowerCase()}, new ForumMapper());
        /*Connection conn = null;
        PreparedStatement createPost = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try {
                createPost = conn.prepareStatement("INSERT INTO POSTS (id, parent, author, thread, forum, message, created, branch_id)\n" +
                        "  VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
                for (int i = 0; i < posts.size(); ++i) {
                    Post post = posts.get(i);
                    post.setThread(thread.getId());
                    post.setForum(thread.getForum());
                    if (post.getCreated() == null) {
                        post.setCreated(now);
                    }
                    Integer postid = null;
                    postid = jdbcTemplate.queryForObject("SELECT nextval(pg_get_serial_sequence('posts', 'id'));", Integer.class);
                    post.setId(postid);
                    posts.set(i, post);
                    Integer user_id = jdbcTemplate.queryForObject("SELECT search_user_id_by_nickname(?);", Integer.class, post.getAuthor().toLowerCase());
                    *//*Integer branch = null;
                    if (post.getParent() != null) {
                        branch = jdbcTemplate.queryForObject("SELECT branch_id FROM posts WHERE id = ?", Integer.class, post.getParent());
                    } else {
                        branch = post.getId();
                    }*//*
                    createPost.setInteger(1, postid);
                    createPost.setInteger(2, post.getParent() == null ? new Integer(0) : posts.get(i).getParent());
                    createPost.setInteger(3, user_id);
                    createPost.setInteger(4, thread.getId());
                    createPost.setInteger(5, forumResult );
                    createPost.setString(6, post.getMessage());
                    createPost.setTimestamp(7, new Timestamp(new DateTime(post.getCreated()).getMillis()));
                    createPost.setInteger(8, new Integer(0));
                    createPost.addBatch();
                }
                createPost.executeBatch();
                conn.commit();
            }
            catch (Exception ex) {
//                conn.rollback();
                throw new DataRetrievalFailureException(ex.getLocalizedMessage());
            }
            finally {
                if (createPost != null)
                    createPost.close();
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException ex) {
            throw new DataRetrievalFailureException(ex.getLocalizedMessage());
        }
        finally {
            try {
                if (conn != null)
                    conn.close();
            }
            catch (Exception e)
            {
                throw new DataRetrievalFailureException(e.getLocalizedMessage());
            }
        }*/

        /*for (int i = 0; i < posts.size(); ++i) {
            Post post = posts.get(i);
            post.setThread(thread.getId());
            post.setForum(thread.getForum());
            if (post.getCreated() == null) {
                post.setCreated(now);
            }
            Integer postid = null;
            postid = jdbcTemplate.queryForObject("SELECT nextval(pg_get_serial_sequence('posts', 'id'));", Integer.class);
            post.setId(postid);
            posts.set(i, post);
        }*/

       /* jdbcTemplate.batchUpdate( "SELECT create_post(?, ?, ?, ?, ?, ?)", new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInteger(1, posts.get(i).getParent() == null ? new Integer(0) : posts.get(i).getParent());
                ps.setString(2, posts.get(i).getAuthor().toLowerCase());
                ps.setInteger(3, thread.getId());
            }

            public int getBatchSize() {
                return actors.size();
            }
        });*/

        Connection conn = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
        } catch (Exception e) {

        }
        CallableStatement callableStatement = null;

        try {
            conn.setAutoCommit(false);
            callableStatement = conn.prepareCall("INSERT INTO " +
                    "POSTS(id, parent, author, thread, forum, message, created) "  +
                            "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id;");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < posts.size(); ++i) {
            Post post = posts.get(i);
            Post parent_post = null;
            if (post.getParent() != null) {
                try {
                    parent_post = jdbcTemplate.queryForObject("select * from posts where id = ? and thread = ?", new Object[] {post.getParent(), thread.getId()}, new PostsMapper());
                    if (!parent_post.getThread().equals(thread.getId())) {
                        return new ResponseEntity<>(new Error("Parent post was created in another thread"), HttpStatus.CONFLICT);
                    }
                } catch (Exception p_e) {
                    return new ResponseEntity<>(new Error("Parent post not found"), HttpStatus.CONFLICT);
                }
            }
            post.setThread(thread.getId());
            post.setForum(thread.getForum());
            if (post.getCreated() == null) {
                post.setCreated(now);
            }
            Integer postid = null;
            try {

                //Integer user_id = jdbcTemplate.queryForObject("SELECT search_user_id_by_nickname(?);", Integer.class, post.getAuthor().toLowerCase());
                postid = jdbcTemplate.queryForObject("SELECT nextval(pg_get_serial_sequence('posts', 'id'));", Integer.class);
                User author_id = null;
                try {
                    author_id = jdbcTemplate.queryForObject("SELECT * FROM USERS where nickname_lower = ?", new Object[] {post.getAuthor().toLowerCase()}, new UsersMapper());
                } catch (Exception e) {
                    return new ResponseEntity<>(new Error("Can't find post author by nickname"), HttpStatus.NOT_FOUND);
                }
                callableStatement.setInt(1, postid);
                callableStatement.setInt(2, (post.getParent() == null ? new Integer(0) : post.getParent()));
                callableStatement.setInt(3, author_id.getId());
                callableStatement.setInt(4, thread.getId());
                callableStatement.setInt(5, forumResult.getId());
                callableStatement.setString(6, post.getMessage());
                callableStatement.setTimestamp(7, new Timestamp(new DateTime(post.getCreated()).getMillis()));
//                callableStatement.registerOutParameter(7, Types.NUMERIC);
                /*postid = jdbcTemplate.queryForObject(
                                "INSERT INTO " +
                                "POSTS(id, parent, author, thread, forum, message, created)"  +
                                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id;", Integer.class,
                                post.getParent() == null ? 0 : post.getParent(), user_id, thread.getId(), forumResult,
                                "", new Timestamp(new DateTime(post.getCreated()).getMillis()));*/
                /*callableStatement2.setInt(1, forumResult.getId());
                callableStatement2.setInt(2, author_id.getId());
                callableStatement2.setString(3, author_id.getAbout());
                callableStatement2.setString(4, author_id.getEmail());
                callableStatement2.setString(5, author_id.getFullname());
                callableStatement2.setString(6, author_id.getNickname());
                callableStatement2.setString(7, author_id.getNickname().toLowerCase());
                callableStatement2.addBatch();*/
                callableStatement.addBatch();
                //TODO перенести вставку данных пользователя в тригер
                jdbcTemplate.update("INSERT INTO USERS_IN_FORUMS (forum_id, user_id, about, email, fullname, nickname, nickname_lower_bytea)\n" +
                        "  VALUES (?, ?, ?, ?, ?, ?, (?)::bytea)\n" +
                        "  ON CONFLICT (forum_id, user_id) DO NOTHING;", forumResult.getId(), author_id.getId(), author_id.getAbout(),
                        author_id.getEmail(), author_id.getFullname(), author_id.getNickname(), author_id.getNickname().toLowerCase());
//                postid = callableStatement.getInteger(7);
            } catch (Exception e) {
                e.printStackTrace();
            }
            post.setId(postid);
            posts.set(i, post);
        }
        try {
            callableStatement.executeBatch();
            if (callableStatement != null)
                callableStatement.close();
            conn.setAutoCommit(true);
            if (conn != null)
                conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* try {
            conn.commit();
            conn.setAutoCommit(true);
            conn.close();
        } catch (Exception e ) {
            e.printStackTrace();
        }*/
        jdbcTemplate.update(UPDATE_FORUM_POSTS_COUNTER, new Integer(posts.size()), forumResult.getId());
        return new ResponseEntity<>(posts, HttpStatus.CREATED);
    }

    @Override
    @ApiOperation(value = "Получение информации о ветке обсуждения", notes = "Получение информации о ветке обсуждения по его имени. ", response = Thread.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = Thread.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Thread.class) })
    @RequestMapping(value = "/{slugOrId}/details",
        produces = { "application/json" },
        method = RequestMethod.GET)
    public ResponseEntity<?> threadGetOne(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId) {
        //log.info("Получение информации о ветке обсуждения " + slugOrId);
        Thread thread = null;
        try {
            thread = this.searchThreadByIdOrSlug(slugOrId);
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутсвует в форуме."), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(thread, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Сообщения данной ветви обсуждения", notes = "Получение списка сообщений в данной ветке форуме. Сообщения выводятся отсортированные по дате создания. ", response = Posts.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Информация о сообщениях форума. ", response = Posts.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Posts.class) })
    @RequestMapping(value = "/{slugOrId}/posts",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> threadGetPosts(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
        @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
        @ApiParam(value = "Идентификатор поста, после которого будут выводиться записи (пост с данным идентификатором в результат не попадает). ") @RequestParam(value = "since", required = false) Integer since,
        @ApiParam(value = "Вид сортировки:  * flat - по дате, комментарии выводятся простым списком в порядке создания;  * tree - древовидный, комментарии выводятся отсортированные в дереве    по N штук;  * parent_tree - древовидные с пагинацией по родительским (parent_tree),    на странице N родительских комментов и все комментарии прикрепленные    к ним, в древвидном отображение. Подробности: https://park.mail.ru/blog/topic/view/1191/ ", allowableValues = "FLAT, TREE, PARENT_TREE", defaultValue = "flat") @RequestParam(value = "sort", required = false, defaultValue="flat") String sort,
        @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc) {
        /*log.info("Сообщения данной ветви обсуждения, параметры:\n" +
                "slug: " + slugOrId + "\n" +
                "since: " + (since == null ? "null" : since.toString()) + "\n" +
                "limit: " + (limit == null ? "null" : limit.toString()) + "\n" +
                "desc: " + (desc == null ? "null" : desc.toString()) + "\n" +
                "sort: " + (sort != null ? "flat" : sort));*/
        ArrayList<Post> posts = null;

        Thread thread = null;
        try {
            thread = this.searchThreadByIdOrSlug(slugOrId);
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутствует в форуме."), HttpStatus.NOT_FOUND);
        }
        if (since == null) {
            since = new Integer(0);
        }
        if (desc == null) {
            desc = false;
        }
        String query = "";

        if (sort == null || sort.equals("flat")) {
            query = this.postsSearchQuery(limit, since, desc, thread);
            if (since != 0) {
                posts = (ArrayList<Post>)jdbcTemplate.query(query, new Object[]{thread.getId(), since, limit}, new PostsMapper());
            } else {
                posts = (ArrayList<Post>)jdbcTemplate.query(query, new Object[]{thread.getId(), limit}, new PostsMapper());
            }
        } else if (sort.equals("tree")) {
            query = this.postsTreeSearchQuery(limit, since, desc, thread);
            if (since != 0) {
                posts = (ArrayList<Post>)jdbcTemplate.query(query, new Object[]{thread.getId(), since, limit}, new PostsMapper());
            } else {
                posts = (ArrayList<Post>)jdbcTemplate.query(query, new Object[]{thread.getId(), limit}, new PostsMapper());
            }
        } else {
            query = this.postsTreeParentSearchQuery(limit, since, desc, thread);
            if (since != 0) {
                posts = (ArrayList<Post>)jdbcTemplate.query(query, new Object[]{thread.getId(), since, limit}, new PostsMapper());
            } else {
                posts = (ArrayList<Post>)jdbcTemplate.query(query, new Object[]{thread.getId(), limit}, new PostsMapper());
            }
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Обновление ветки", notes = "Обновление ветки обсуждения на форуме. ", response = Thread.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = Thread.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Thread.class) })
    @RequestMapping(value = "/{slugOrId}/details",
        produces = { "application/json" },
        consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<?> threadUpdate(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
        @ApiParam(value = "Данные ветки обсуждения." ,required=true ) @RequestBody ThreadUpdate thread) {
        //log.info("Обновление ветки");
        Thread new_thread = null;
        try {
            new_thread = this.searchThreadByIdOrSlug(slugOrId);
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутсвует в форуме."), HttpStatus.NOT_FOUND);
        }
        if (thread.getTitle() != null) {
            new_thread.setTitle(thread.getTitle());
        }
        if (thread.getMessage() != null) {
            new_thread.setMessage(thread.getMessage());
        }
        jdbcTemplate.update(UPDATE_THREAD, new_thread.getTitle(), new_thread.getMessage(), new_thread.getId());
        return new ResponseEntity<>(new_thread, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Проголосовать за ветвь обсуждения", notes = "Изменение голоса за ветвь обсуждения. Один пользователь учитывается только один раз и может изменить своё мнение. ", response = Thread.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = Thread.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Thread.class) })
    @RequestMapping(value = "/{slugOrId}/vote",
        produces = { "application/json" },
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public ResponseEntity<?> threadVote(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
        @ApiParam(value = "Информация о голосовании пользователя." ,required=true ) @RequestBody Vote vote) {
        //log.info("Проголосовать за ветвь обсуждения");
        Integer threadId = null;
        try {
            threadId = searchThreadIdByIdOrSlug(slugOrId);
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутствует в базе данных."), HttpStatus.NOT_FOUND);
        }
        Integer user_id = null;
        try {
            user_id = jdbcTemplate.queryForObject(SEARCH_USER_ID_BY_NICKNAME, Integer.class, vote.getNickname().toLowerCase());
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("\"Can't find user by nickname: " + vote.getNickname()), HttpStatus.NOT_FOUND);
        }
        Connection conn = null;
        CallableStatement callableStatement = null;
        Integer test = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try {
                callableStatement = conn.prepareCall("select create_or_update_vote(?, ?, ?)");
                callableStatement.setInt(1, user_id.intValue());
                callableStatement.setInt(2, threadId.intValue());
                callableStatement.setInt(3, vote.getVoice().intValue());
                callableStatement.execute();
                conn.commit();
            } catch (SQLException sEx) {
                conn.rollback();
                throw new DataRetrievalFailureException(sEx.getLocalizedMessage());
            } finally {
                if (callableStatement != null)
                    callableStatement.close();
                conn.setAutoCommit(true);
            }
            //jdbcTemplate.update("select create_or_update_vote(?, ?, ?)", user_id.intValue(), threadId.intValue(), vote.getVoice().intValue());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                throw new DataRetrievalFailureException(e.getLocalizedMessage());
            }
        }
        /*Integer old_value = new Integer(0);
        Vote old_vote = null;
        try {
            old_vote = jdbcTemplate.queryForObject(SEARCH_VOTE_BY_THREAD_ID_AND_USER_ID, new Object[] {threadId, user_id}, new VoteMapper());
            old_value = old_vote.getVoice();
            jdbcTemplate.update(UPDATE_VOTE_BY_ID, vote.getVoice(), old_vote.getId());
        } catch (EmptyResultDataAccessException e1) {
            jdbcTemplate.update(CRATE_VOTE_BY_THREAD_AND_USER_ID, threadId, user_id, vote.getVoice());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (old_value != vote.getVoice()) {
            Integer newVotes = thread.getVotes().add(vote.getVoice().subtract(old_value));
            jdbcTemplate.update(UPDATE_THREAD_VOTE_BY_THREAD_ID, newVotes, thread.getId());
            thread.setVotes(newVotes);
            return new ResponseEntity<>(thread, HttpStatus.OK);
        }*/
        Thread thread = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_ID, new Object[] {threadId}, new ThreadsMapper());
        return new ResponseEntity<>(thread, HttpStatus.OK);
    }

    private Thread searchThreadByIdOrSlug(String slugOrId) {
        Thread thread = null;
        if (StringUtils.isNumeric(slugOrId))
            thread = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_ID, new Object[] {new Integer(slugOrId)}, new ThreadsMapper());
        else
            thread = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_SLUG, new Object[] {slugOrId.toLowerCase()}, new ThreadsMapper());
        return thread;
    }

    private Integer searchThreadIdByIdOrSlug(String slugOrId) {
        if (StringUtils.isNumeric(slugOrId))
            return jdbcTemplate.queryForObject("select id from threads where id = ?", Integer.class, new Integer(slugOrId));
        else
            return jdbcTemplate.queryForObject(SEARCH_THREAD_ID_BY_SLUG, Integer.class, slugOrId.toLowerCase());
    }

    private String postsSearchQuery(Integer limit,  Integer since,  Boolean desc, Thread thread) {
        String query =
                "SELECT posts.id,   \n" +
                "  posts.parent,   \n" +
                "  users.nickname AS author,   \n" +
                "  posts.thread,   \n" +
                "  forums.slug AS forum,   \n" +
                "  posts.message,   \n" +
                "  posts.isedited,   \n" +
                "  posts.created,  \n" +
                "  posts.branch_id,  \n" +
                "  posts.path_to_root  \n" +
                "FROM posts \n" +
                "JOIN users ON posts.author = users.id \n" +
                "JOIN forums ON posts.forum = forums.id \n" +
                "WHERE thread = ?\n";
        if (since != 0) {
            if (desc == true) {
                query += "and posts.id < ?\n";
            } else {
                query += "and posts.id > ?\n";
            }
        }
        if (desc == true) {
            query += "ORDER BY posts.id DESC\n";
        } else {
            query += "ORDER BY posts.id\n";
        }
        query += "LIMIT ?;";
        return query;
    }

    private String postsTreeSearchQuery(Integer limit, Integer since, Boolean desc, Thread thread) {
        String query = "SELECT posts.id,   \n" +
                "  posts.parent,   \n" +
                "  users.nickname AS author,   \n" +
                "  posts.thread,   \n" +
                "  forums.slug AS forum,   \n" +
                "  posts.message,   \n" +
                "  posts.isedited,   \n" +
                "  posts.created,  \n" +
                "  posts.branch_id,  \n" +
                "  posts.path_to_root  \n" +
                "FROM posts \n" +
                "JOIN users ON posts.author = users.id \n" +
                "JOIN forums ON posts.forum = forums.id \n" +
                "  WHERE thread = ?\n";
        if (since != 0) {
            if (desc == false || desc == null) {
                query += "AND posts.path_to_root > (select path_to_root from posts WHERE id = ?) \n";
            } else {
                query += "AND posts.path_to_root < (select path_to_root from posts WHERE id = ?)\n";
            }
        }
        if (desc == false || desc == null) {
            query += "ORDER BY posts.path_to_root\n";;
        } else {
            query += "ORDER BY posts.path_to_root DESC\n";
        }
        query += "LIMIT ?;";
        return query;
    }

    private String postsTreeParentSearchQuery(Integer limit, Integer since, Boolean desc, Thread thread) {

        String query = "";
        if (since != 0) {
            if (desc == false || desc == null) {
                query =
                        "SELECT posts.id,   \n" +
                        "  posts.parent,   \n" +
                        "  users.nickname AS author,   \n" +
                        "  posts.thread,   \n" +
                        "  forums.slug AS forum,   \n" +
                        "  posts.message,   \n" +
                        "  posts.isedited,   \n" +
                        "  posts.created,  \n" +
                        "  posts.branch_id,  \n" +
                        "  posts.path_to_root  \n" +
                        "FROM posts \n" +
                        "JOIN users ON posts.author = users.id \n" +
                        "JOIN forums ON posts.forum = forums.id \n" +
                        "WHERE posts.branch_id in (\n" +
                        "  SELECT id FROM posts WHERE thread = ? AND parent = 0 AND id > (\n" +
                        "    select branch_id from posts where id = ?\n" +
                        "  ) ORDER BY id limit ?\n" +
                        ")\n" +
                        "ORDER BY posts.path_to_root;";
            } else {
                query =
                        "SELECT posts.id,   \n" +
                        "  posts.parent,   \n" +
                        "  users.nickname AS author,   \n" +
                        "  posts.thread,   \n" +
                        "  forums.slug AS forum,   \n" +
                        "  posts.message,   \n" +
                        "  posts.isedited,   \n" +
                        "  posts.created,  \n" +
                        "  posts.branch_id,  \n" +
                        "  posts.path_to_root  \n" +
                        "FROM posts \n" +
                        "JOIN users ON posts.author = users.id \n" +
                        "JOIN forums ON posts.forum = forums.id \n" +
                        "WHERE posts.branch_id in (\n" +
                        "  SELECT id FROM posts WHERE thread = ? AND parent = 0 AND id < (\n" +
                        "    select branch_id from posts where id = ?\n" +
                        "  ) ORDER BY id DESC limit ?\n" +
                        ")\n" +
                        "ORDER BY posts.branch_id DESC, posts.path_to_root;";
            }
        } else {
            if (desc == false || desc == null) {
                query =
                        "SELECT posts.id,   \n" +
                        "  posts.parent,   \n" +
                        "  users.nickname AS author,   \n" +
                        "  posts.thread,   \n" +
                        "  forums.slug AS forum,   \n" +
                        "  posts.message,   \n" +
                        "  posts.isedited,   \n" +
                        "  posts.created,  \n" +
                        "  posts.branch_id,  \n" +
                        "  posts.path_to_root  \n" +
                        "FROM posts \n" +
                        "JOIN users ON posts.author = users.id \n" +
                        "JOIN forums ON posts.forum = forums.id \n" +
                        "WHERE posts.branch_id in (\n" +
                        "  SELECT id FROM posts WHERE thread = ? AND parent = 0\n" +
                        "  ORDER BY id limit ?\n" +
                        ")\n" +
                        "ORDER BY posts.path_to_root;";
            } else {
                query =
                        "SELECT posts.id,   \n" +
                        "  posts.parent,   \n" +
                        "  users.nickname AS author,   \n" +
                        "  posts.thread,   \n" +
                        "  forums.slug AS forum,   \n" +
                        "  posts.message,   \n" +
                        "  posts.isedited,   \n" +
                        "  posts.created,  \n" +
                        "  posts.branch_id,  \n" +
                        "  posts.path_to_root  \n" +
                        "FROM posts \n" +
                        "JOIN users ON posts.author = users.id \n" +
                        "JOIN forums ON posts.forum = forums.id \n" +
                        "WHERE posts.branch_id in (\n" +
                        "  SELECT id FROM posts WHERE thread = ? AND parent = 0\n" +
                        "  ORDER BY id DESC limit ?\n" +
                        ")\n" +
                        "ORDER BY posts.branch_id DESC, posts.path_to_root;";
            }
        }
        return query;
    }
}
