package com.example.demo;

import com.dturan.Mapper.PostsMapper;
import com.dturan.Mapper.ThreadsMapper;
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
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/thread/")
@Repository
public class ThreadApiImpl implements ThreadApi {

    @NotNull
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ThreadApiImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String SEARCH_THREAD_ID_BY_SLUG = "SELECT ID FROM THREADS WHERE slug_lower = ?";

    private static final String SEARCH_TRHEAD_BY_ID =
            "SELECT users.nickname as author,\n" +
                    "threads.created, \n" +
                    "  forums.slug as forum, \n" +
                    "  threads.id, \n" +
                    "  threads.message, \n" +
                    "  threads.slug, \n" +
                    "  threads.tittle, \n" +
                    "  threads.votes \n" +
                    "FROM forums, threads, users \n" +
                    "WHERE threads.id = ? AND\n" +
                    "  forums.id = threads.forum" +
                    "  AND users.id = threads.author\n;";

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

    private static final String SEARCH_PARENT_POST =
            "SELECT posts.id, \n" +
            "  posts.parent, \n" +
            "  users.nickname as author, \n" +
            "  posts.thread, \n" +
            "  forums.slug as forum, \n" +
            "  posts.message, \n" +
            "  posts.isedited, \n" +
            "  posts.created\n" +
            "FROM forums, users, posts\n" +
            "WHERE\n" +
            "  users.id =  posts.author AND\n" +
            "  forums.id = posts.forum AND\n" +
            "  posts.id = ?\n";

    private static final String SEARCH_USER_ID_BY_NICKNAME = "SELECT ID FROM USERS WHERE nickname_lower = ?;";
    private static final String SEARCH_USER_BY_NICKNAME = "SELECT * FROM USERS WHERE lower(nickname) = lower(?);";
    private static final String SEARCH_FORUM_ID_BY_SLUG = "SELECT ID FROM FORUMS WHERE slug_lower = ?;";
    private static final String SEARCH_FORUM_BY_SLUG = "SELECT * FROM FORUMS WHERE lower(slug) = ?;";
    private static final String SEARCH_POST_PATH_AND_BRANCH_BY_POST_ID = "SELECT PATH, BRANCH FROM POSTS WHERE ID = ?";
    private static final String UPDATE_POST_PATH_AND_BRANCH_BY_POST_ID = "UPDATE POSTS SET PATH = ?, BRANCH = ? WHERE ID = ?";
    private static final String CREATE_POST_QUERY =
        "INSERT INTO POSTS (id, parent, author, thread, forum, message, created, path, branch)\n" +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id;";
    private static final String SEARCH_POST_ID_BY_AUTHOR_AND_THREAD_AND_MESSAGE =
        "SELECT ID FROM POSTS WHERE author = ? and thread = ? AND message = ?";
    private static final String SEARCH_VOTE_BY_THREAD_ID_AND_USER_ID =
        "SELECT id, thread, vote_maker, voice \n" +
        "FROM votes\n" +
        "WHERE thread = ? AND \n" +
        "vote_maker = ?";
    private static final String UPDATE_VOTE_BY_ID =
        "UPDATE votes SET voice = ? WHERE id = ?";
    private static final String CRATE_VOTE_BY_THREAD_AND_USER_ID =
        "INSERT INTO votes(thread, vote_maker, voice)\n" +
        "VALUES (?, ?, ?)";
    private static final String UPDATE_THREAD_VOTE_BY_THREAD_ID =
            "UPDATE threads SET Votes = ? WHERE id = ?";
    private static final String GET_LAST_POSTS_TREE_BRANCH =
            "SELECT branch FROM POSTS WHERE parent ISNULL AND branch > 0 ORDER BY branch DESC limit 1;";
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
        if (posts.isEmpty()) {
            return new ResponseEntity<>(posts, HttpStatus.CREATED);
        }
        Thread thread = null;
        try {
            thread = this.searchThreadByIdOrSlug(slugOrId);
        } catch (Exception e5) {
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутствует в базе данных."), HttpStatus.NOT_FOUND);
        }
        BigDecimal forumResult = jdbcTemplate.queryForObject(SEARCH_FORUM_ID_BY_SLUG,BigDecimal.class,thread.getForum().toLowerCase());
        Connection conn = null;
        CallableStatement createPost = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try {
                createPost = conn.prepareCall("select create_post(?, ?, ?, ?, ?, ?, ?)");
                for (int i = 0; i < posts.size(); ++i) {
                    Post post = posts.get(i);
                    post.setThread(thread.getId());
                    post.setForum(thread.getForum());
                    if (post.getCreated() == null) {
                        post.setCreated(now);
                    }
                    BigDecimal author = null;
                    try {
                        author = jdbcTemplate.queryForObject(SEARCH_USER_ID_BY_NICKNAME, BigDecimal.class, post.getAuthor().toLowerCase());
                    } catch (Exception e) {
                        return new ResponseEntity<>(new Error("Can't find post author by nickname: " + post.getAuthor()), HttpStatus.NOT_FOUND);
                    }
                    BigDecimal postId = jdbcTemplate.queryForObject("SELECT nextval('posts_id_seq')", BigDecimal.class);
                    post.setId(postId);
                    createPost.setBigDecimal(1, postId);
                    if (post.getParent() != null) {
                        createPost.setBigDecimal(2, post.getParent());
                    } else {
                        createPost.setNull(2, Types.NUMERIC);
                    }
                    createPost.setBigDecimal(3, author);
                    createPost.setBigDecimal(4, post.getThread());
                    createPost.setBigDecimal(5, forumResult);
                    createPost.setString(6, post.getMessage());
                    createPost.setTimestamp(7, new Timestamp(new DateTime(post.getCreated()).getMillis()));
                    createPost.addBatch();
                    posts.set(i, post);
                }
                createPost.executeBatch();
                conn.commit();
            } catch (Exception ex) {
                conn.rollback();
                throw new DataRetrievalFailureException(ex.getLocalizedMessage());
            }
            finally {
                if (createPost != null)
                    createPost.close();
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new DataRetrievalFailureException(ex.getLocalizedMessage());
        }  finally {
            try {
                if (conn != null)
                    conn.close();
            }
            catch (Exception ex2)
            {
                throw new DataRetrievalFailureException(ex2.getLocalizedMessage());
            }
        }

        /*ArrayList<BigDecimal> authors = new ArrayList<BigDecimal>();
        if (thread == null)
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутствует в базе данных."), HttpStatus.NOT_FOUND);
        for (int i = 0; i < posts.size(); ++i) {
            Post post = posts.get(i);
            Post parent = null;
            if (post.getParent() != null) {
                try {
                    parent = jdbcTemplate.queryForObject(SEARCH_PARENT_POST, new Object[] {post.getParent() }, new PostsMapper());
                } catch (Exception e) {
                    return new ResponseEntity<>(new Error("Родительский пост " + posts.get(i).getParent() + " отсутствует в базе данных."), HttpStatus.CONFLICT);
                }
                if (parent == null) {
                    return new ResponseEntity<>(new Error("Родительский пост " + posts.get(i).getParent() + " отсутствует в базе данных."), HttpStatus.CONFLICT);
                }
                if (!parent.getThread().equals(thread.getId())) {
                    return new ResponseEntity<>(new Error("Parent post was created in another thread."), HttpStatus.CONFLICT);
                }
            }
            post.setThread(thread.getId());
            post.setForum(thread.getForum());
            if (post.getCreated() == null) {
                post.setCreated(now);
            }
            BigDecimal author = null;
            try {
                author = jdbcTemplate.queryForObject(SEARCH_USER_ID_BY_NICKNAME, BigDecimal.class, post.getAuthor().toLowerCase());
                authors.add(author);
            } catch (Exception e) {
                return new ResponseEntity<>(new Error("Can't find post author by nickname: " + post.getAuthor()), HttpStatus.NOT_FOUND);
            }
            posts.set(i, post);
        }*/

        jdbcTemplate.update(UPDATE_FORUM_POSTS_COUNTER, new BigDecimal(posts.size()), forumResult);
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
        @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") BigDecimal limit,
        @ApiParam(value = "Идентификатор поста, после которого будут выводиться записи (пост с данным идентификатором в результат не попадает). ") @RequestParam(value = "since", required = false) BigDecimal since,
        @ApiParam(value = "Вид сортировки:  * flat - по дате, комментарии выводятся простым списком в порядке создания;  * tree - древовидный, комментарии выводятся отсортированные в дереве    по N штук;  * parent_tree - древовидные с пагинацией по родительским (parent_tree),    на странице N родительских комментов и все комментарии прикрепленные    к ним, в древвидном отображение. Подробности: https://park.mail.ru/blog/topic/view/1191/ ", allowableValues = "FLAT, TREE, PARENT_TREE", defaultValue = "flat") @RequestParam(value = "sort", required = false, defaultValue="flat") String sort,
        @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc) {

        ArrayList<Post> posts = null;

        Thread thread = null;
        try {
            thread = this.searchThreadByIdOrSlug(slugOrId);
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутствует в форуме."), HttpStatus.NOT_FOUND);
        }
        if (since == null) {
            since = new BigDecimal(0);
        }
        if (desc == null) {
            desc = false;
        }
        String query = "";

        if (sort == null || sort.equals("flat")) {
            query = this.postsSearchQuery(limit, since, desc, thread);
        } else if (sort.equals("tree")) {
            query = this.postsTreeSearchQuery(limit, since, desc, thread);
        } else {
            query = this.postsTreeParentSearchQuery(limit, since, desc, thread);
        }

        posts = (ArrayList<Post>)jdbcTemplate.query(query, new Object[]{}, new PostsMapper());

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

        BigDecimal threadId = null;
        try {
            threadId = searchThreadIdByIdOrSlug(slugOrId);
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Ветка обсуждения отсутствует в базе данных."), HttpStatus.NOT_FOUND);
        }
        BigDecimal user_id = null;
        try {
            user_id = jdbcTemplate.queryForObject(SEARCH_USER_ID_BY_NICKNAME, BigDecimal.class, vote.getNickname().toLowerCase());
        } catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("\"Can't find user by nickname: " + vote.getNickname()), HttpStatus.NOT_FOUND);
        }
        Connection conn = null;
        CallableStatement callableStatement = null;
        try {
            conn = jdbcTemplate.getDataSource().getConnection();
            conn.setAutoCommit(false);
            try {
                callableStatement = conn.prepareCall("select create_or_update_vote(?, ?, ?)");
                callableStatement.setInt(1, user_id.intValue());
                callableStatement.setInt(2, threadId.intValue());
                callableStatement.setInt(3, vote.getVoice().intValue());
                callableStatement.execute();
                conn.setAutoCommit(true);
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
        /*BigDecimal old_value = new BigDecimal(0);
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
            BigDecimal newVotes = thread.getVotes().add(vote.getVoice().subtract(old_value));
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
            thread = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_ID, new Object[] {new BigDecimal(slugOrId)}, new ThreadsMapper());
        else
            thread = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_SLUG, new Object[] {slugOrId.toLowerCase()}, new ThreadsMapper());
        return thread;
    }

    private BigDecimal searchThreadIdByIdOrSlug(String slugOrId) {
        if (StringUtils.isNumeric(slugOrId))
            return new BigDecimal(slugOrId);
        else
            return jdbcTemplate.queryForObject(SEARCH_THREAD_ID_BY_SLUG, BigDecimal.class, slugOrId.toLowerCase());
    }

    private String postsSearchQuery(BigDecimal limit,  BigDecimal since,  boolean desc, Thread thread) {
        String query =
            "SELECT posts.id, \n" +
                "  posts.parent, \n" +
                "  users.nickname as author, \n" +
                "  posts.thread, \n" +
                "  forums.slug as forum, \n" +
                "  posts.message, \n" +
                "  posts.isedited, \n" +
                "  posts.created\n" +
                "FROM forums, users, posts\n" +
                "WHERE\n" +
                "  users.id =  posts.author AND\n" +
                "  forums.id = posts.forum AND\n" +
                "  posts.thread = " + thread.getId().toString() + "\n";
        if (since.compareTo(BigDecimal.ZERO) != 0) {
            if (desc == false)
                query += "  AND posts.id > " + since.toString() + "\n";
            else
                query += "  AND posts.id < " + since.toString() + "\n";
        }
        if (desc) {
            query += "ORDER BY posts.id DESC\n";
        } else {
            query += "ORDER BY posts.id\n";
        }
        query += "LIMIT " + limit.toString() + " ;";
        return query;
    }

    private String postsTreeSearchQuery(BigDecimal limit, BigDecimal since, boolean desc, Thread thread) {
        String query = "";
        if (since.compareTo(BigDecimal.ZERO) != 0) {
            if (desc == false) {
                query =
                "SELECT post.id, \n" +
                "  post.parent, \n" +
                "  users.nickname as author, \n" +
                "  post.thread, \n" +
                "  forums.slug as forum, \n" +
                "  post.message, \n" +
                "  post.isedited, \n" +
                "  post.created,\n" +
                "  post.path\n" +
                "FROM forums, users,\n" +
                    "(select *, ROW_NUMBER() OVER (ORDER BY path) as number from posts\n" +
                    "where thread = " + thread.getId().toString() + "\n" +
                    "ORDER BY path) as post\n" +
                    "where \n" +
                    "  users.id =  post.author AND\n" +
                    "  forums.id = post.forum AND\n" +
                    "  number > (\n" +
                    "SELECT p.row_number\n" +
                    "FROM (\n" +
                    "SELECT\n" +
                    "*,\n" +
                    "ROW_NUMBER()\n" +
                    "OVER (\n" +
                    "ORDER BY path )\n" +
                    "FROM posts\n" +
                    "WHERE thread = " + thread.getId().toString() + "\n" +
                    ") AS p\n" +
                    "WHERE id >= " + since.toString() + " ORDER BY id LIMIT 1\n"+
                    ") ORDER BY path\n";
            }
            else
                query =
                "SELECT post.id, \n" +
                        "  post.parent, \n" +
                        "  users.nickname as author, \n" +
                        "  post.thread, \n" +
                        "  forums.slug as forum, \n" +
                        "  post.message, \n" +
                        "  post.isedited, \n" +
                        "  post.created,\n" +
                        "  post.path\n" +
                        "FROM forums, users,\n" +
                    "(select *, ROW_NUMBER() OVER (ORDER BY path) as number from posts\n" +
                    "where thread = " + thread.getId().toString() + "\n" +
                    "ORDER BY path) as post\n" +
                    "where \n" +
                    "  users.id =  post.author AND\n" +
                    "  forums.id = post.forum AND\n" +
                    "  number < (\n" +
                    "SELECT p.row_number\n" +
                    "FROM (\n" +
                    "SELECT\n" +
                    "*,\n" +
                    "ROW_NUMBER()\n" +
                    "OVER (\n" +
                    "ORDER BY path )\n" +
                    "FROM posts\n" +
                    "WHERE thread = " + thread.getId().toString() + "\n" +
                    ") AS p\n" +
                    "WHERE id >= " + since.toString() + " ORDER BY id LIMIT 1\n" +
                    ") ORDER BY path DESC\n";
        } else {
            query =
            "SELECT posts.id, \n" +
                "  posts.parent, \n" +
                "  users.nickname as author, \n" +
                "  posts.thread, \n" +
                "  forums.slug as forum, \n" +
                "  posts.message, \n" +
                "  posts.isedited, \n" +
                "  posts.created,\n" +
                "  posts.path\n" +
                "FROM forums, users, posts\n" +
                "WHERE\n" +
                "  users.id =  posts.author AND\n" +
                "  forums.id = posts.forum AND\n" +
                "  posts.thread = " + thread.getId().toString() + "\n";
            if (desc) {
                query += "ORDER BY posts.path DESC\n";
            } else {
                query += "ORDER BY posts.path\n";
            }
        }
        query += "LIMIT " + limit.toString() + " ;";
        return query;
    }

    private String postsTreeParentSearchQuery(BigDecimal limit, BigDecimal since, boolean desc, Thread thread) {

        String query = "";
        if (since.compareTo(BigDecimal.ZERO) != 0) {
            if (desc == false) {
                query =
                "WITH thread_posts as (\n" +
                    "SELECT posts.id,    \n" +
                    "  posts.parent,    \n" +
                    "  users.nickname as author,    \n" +
                    "  posts.thread,    \n" +
                    "  forums.slug as forum,    \n" +
                    "  posts.message,    \n" +
                    "  posts.isedited,    \n" +
                    "  posts.created,   \n" +
                    "  posts.path, \n" +
                    "  posts.branch, \n" +
                    "  ROW_NUMBER() OVER (ORDER BY path)\n" +
                    "FROM forums, users, posts   \n" +
                    "WHERE   \n" +
                    "  users.id =  posts.author AND   \n" +
                    "  forums.id = posts.forum AND   \n" +
                    "  posts.thread = " + thread.getId().toString() + "\n" +
                ")\n" +
                "SELECT * from thread_posts\n" +
                "WHERE branch IN (\n" +
                "  SELECT branch FROM (\n" +
                "     SELECT thread_posts.* from thread_posts,\n" +
                "       (\n" +
                "         SELECT * FROM thread_posts\n" +
                "         WHERE id >= " + since.toString() + "\n" +
                "         ORDER BY id LIMIT 1\n" +
                "       ) as p\n" +
                "     WHERE thread_posts.row_number > p.row_number\n" +
                "   ) as p GROUP BY branch limit " + limit.toString() + "\n" +
                ");";
            }
            else
                query =
                "WITH thread_posts as (\n" +
                "    SELECT posts.id,\n" +
                "      posts.parent,\n" +
                "      users.nickname as author,\n" +
                "      posts.thread,\n" +
                "      forums.slug as forum,\n" +
                "      posts.message,\n" +
                "      posts.isedited,\n" +
                "      posts.created,\n" +
                "      posts.path,\n" +
                "      posts.branch\n" +
                "    FROM forums, users, posts\n" +
                "    WHERE\n" +
                "      users.id =  posts.author AND\n" +
                "      forums.id = posts.forum AND\n" +
                "      posts.thread =" + thread.getId().toString() + "\n" +
                ")\n" +
                "SELECT * FROM thread_posts\n" +
                "WHERE branch in (\n" +
                "    SELECT branch FROM thread_posts\n" +
                "           WHERE branch < (\n" +
                "    SELECT branch FROM thread_posts\n" +
                "    WHERE id <= " + since.toString() + "\n" +
                "    ORDER BY id DESC limit 1)\n" +
                "    GROUP BY branch\n" +
                "    ORDER BY branch DESC\n" +
                "    LIMIT " + limit.toString() + "\n" +
                ")\n" +
                "ORDER BY branch DESC, path;";
        } else {
            if (desc) {
                query +=
                "SELECT posts.id,    \n" +
                "  posts.parent,    \n" +
                "  users.nickname as author,    \n" +
                "  posts.thread,    \n" +
                "  forums.slug as forum,    \n" +
                "  posts.message,    \n" +
                "  posts.isedited,    \n" +
                "  posts.created,   \n" +
                "  posts.path, \n" +
                "  posts.branch \n" +
                "FROM forums, users, posts   \n" +
                "WHERE   \n" +
                "  users.id =  posts.author AND   \n" +
                "  forums.id = posts.forum AND   \n" +
                "  posts.thread = " + thread.getId().toString() + " AND\n" +
                "  posts.branch IN (\n" +
                "    SELECT branch from ( \n" +
                "                         SELECT branch, ROW_NUMBER() OVER (ORDER BY branch DESC)  FROM posts \n" +
                "                         WHERE posts.thread = " + thread.getId().toString() + " AND\n" +
                "                               parent ISNULL \n" +
                "                       ) as p \n" +
                "    WHERE row_number <= "+ limit.toString() + "\n" +
                "  ) \n" +
                "ORDER BY branch DESC, path;";
            } else {
                query +=
                "SELECT posts.id,    \n" +
                "  posts.parent,    \n" +
                "  users.nickname as author,    \n" +
                "  posts.thread,    \n" +
                "  forums.slug as forum,    \n" +
                "  posts.message,    \n" +
                "  posts.isedited,    \n" +
                "  posts.created,   \n" +
                "  posts.path, \n" +
                "  posts.branch \n" +
                "FROM forums, users, posts   \n" +
                "WHERE   \n" +
                "  users.id =  posts.author AND   \n" +
                "  forums.id = posts.forum AND   \n" +
                "  posts.thread = " + thread.getId().toString() + " AND\n" +
                "  posts.branch <= (\n" +
                "    SELECT branch from ( \n" +
                "                         SELECT branch, ROW_NUMBER() OVER (ORDER BY branch)  FROM posts \n" +
                "                         WHERE posts.thread = " + thread.getId().toString() + " AND\n" +
                "                               parent ISNULL \n" +
                "                       ) as p \n" +
                "    WHERE row_number <= "+ limit.toString() + "\n" +
                "    ORDER BY row_number DESC LIMIT 1\n" +
                "  ) \n" +
                "ORDER BY path;";
            }
        }
        return query;
    }
}
