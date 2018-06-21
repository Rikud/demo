package com.example.demo;

import com.dturan.Mapper.ForumMapper;
import com.dturan.Mapper.ThreadsMapper;
import com.dturan.Mapper.UsersMapper;
import com.dturan.api.ForumApi;
import com.dturan.model.Error;
import com.dturan.model.*;
import com.dturan.model.Thread;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/forum/")
public class ForumApiImpl implements ForumApi {

    @NotNull
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ForumApiImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Logger log = Logger.getLogger(ForumApiImpl.class.getName());

    private static final String CREATE_FORUM_QUERY = "INSERT INTO FORUMS (tittle, slug, slug_lower, user_id) VALUES(?, ?, ?, ?)";
    private static final String SEARCH_USER_BY_NICKNAME_QUERY = "SELECT * FROM USERS WHERE nickname_lower = ?";
    private static final String SEARCH_FORIM_ID_BY_SLUG  = "SELECT ID FROM forums WHERE slug_lower = ?";
    private static final String SEARCH_FORUM_BY_SLUG = "select * from forums where slug_lower = ?";
    private static final String SEARCH_USER_NICKNAME_BY_ID_QUERY = "SELECT nickname FROM USERS WHERE id = ?";
    private static final String SEARCH_FORUM_THREADS =
        "SELECT users.nickname as author,\n" +
        "  threads.created,\n" +
        "  forums.slug as forum,\n" +
        "  threads.id,\n" +
        "  threads.message,\n" +
        "  threads.slug,\n" +
        "  threads.tittle,\n" +
        "  threads.votes\n" +
        "FROM forums, threads, users\n" +
        "WHERE forums.slug_lower = ?\n" +
        "      AND forums.id = threads.forum\n" +
        "      AND users.id =  threads.author\n" +
        "ORDER BY threads.created\n" +
                "LIMIT ?";

    private static final String SEARCH_FORUM_THREADS_DESC =
        "SELECT users.nickname as author,\n" +
                "  threads.created,\n" +
                "  forums.slug as forum,\n" +
                "  threads.id,\n" +
                "  threads.message,\n" +
                "  threads.slug,\n" +
                "  threads.tittle,\n" +
                "  threads.votes\n" +
                "FROM forums, threads, users\n" +
                "WHERE forums.slug_lower = ?\n" +
                "      AND forums.id = threads.forum\n" +
                "      AND users.id =  threads.author\n" +
                "ORDER BY threads.created DESC\n" +
                "LIMIT ?";

    private static final String SEARCH_FORUM_THREADS_DATE_LIMIT =
        "SELECT users.nickname as author,\n" +
                "  threads.created,\n" +
                "  forums.slug as forum,\n" +
                "  threads.id,\n" +
                "  threads.message,\n" +
                "  threads.slug,\n" +
                "  threads.tittle,\n" +
                "  threads.votes\n" +
                "FROM forums, threads, users\n" +
                "WHERE forums.slug_lower = ?\n" +
                "      AND forums.id = threads.forum\n" +
                "      AND users.id =  threads.author\n" +
                "      AND threads.created >= ?\n" +
                "ORDER BY threads.created\n" +
                "LIMIT ?";

    private static final String SEARCH_FORUM_THREADS_DATE_LIMIT_DESC =
            "SELECT users.nickname as author,\n" +
                    "  threads.created,\n" +
                    "  forums.slug as forum,\n" +
                    "  threads.id,\n" +
                    "  threads.message,\n" +
                    "  threads.slug,\n" +
                    "  threads.tittle,\n" +
                    "  threads.votes\n" +
                    "FROM forums, threads, users\n" +
                    "WHERE forums.slug_lower = ?\n" +
                    "      AND forums.id = threads.forum\n" +
                    "      AND users.id =  threads.author\n" +
                    "      AND threads.created <= ?\n" +
                    "ORDER BY threads.created DESC\n" +
                    "LIMIT ?";

    private static final String SEARCH_FORUM_USERS =
        "select * from users_in_forums where forum_id = ?" +
                " ORDER BY nickname_lower_bytea LIMIT ?;";

    private static final String SEARCH_FORUM_USERS_USER_ID_LIMIT =
            "select * from users_in_forums where forum_id = ?" +
                    "AND nickname_lower_bytea > ?\n" +
                    "ORDER BY nickname_lower_bytea LIMIT ?;";

    private static final String SEARCH_FORUM_USERS_DESC =
            "select * from users_in_forums where forum_id = ?" +
                    "ORDER BY nickname_lower_bytea DESC LIMIT ?;";

    private static final String SEARCH_FORUM_USERS_USER_ID_LIMIT_DESC =
            "select * from users_in_forums where forum_id = ?" +
                    "AND nickname_lower_bytea < ?\n" +
                    "ORDER BY nickname_lower_bytea DESC LIMIT ?;";

    private static final String SEARCH_TRHEAD_BY_FORUM_AND_THREAD_SLUGS =
            "SELECT users.nickname as author,\n" +
            "  threads.created,\n" +
            "  forums.slug as forum,\n" +
            "  threads.id,\n" +
            "  threads.message,\n" +
            "  threads.slug,\n" +
            "  threads.tittle,\n" +
            "  threads.votes\n" +
            "FROM forums, threads, users\n" +
            "WHERE lower(forums.slug) = lower(?)\n" +
            "      AND threads.slug = ?\n" +
            "      AND forums.id = threads.forum\n" +
            "      AND users.id =  threads.author;";

    private static final String CREATE_THREAD_QUERY =
            "INSERT INTO THREADS (tittle, author, forum, message, slug, slug_lower, created, votes)\n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, 0) returning id;";

    private static final String SEARCH_TRHEAD_BY_SLUGS =
            "SELECT users.nickname as author,\n" +
            "  threads.created,\n" +
            "  forums.slug as forum,\n" +
            "  threads.id,\n" +
            "  threads.message,\n" +
            "  threads.slug,\n" +
            "  threads.tittle,\n" +
            "  threads.votes\n" +
            "FROM forums, threads, users\n" +
            "WHERE threads.slug_lower = ?\n" +
            "      AND forums.id = threads.forum\n" +
            "      AND users.id =  threads.author;";

    private static final String UPDATE_FORUM_THREADS_COUNTER =
            "UPDATE forums SET threads = threads + 1 WHERE slug_lower = ?";

    @Override
    @ApiOperation(value = "Создание форума", notes = "Создание нового форума. ", response = Forum.class, tags={  })
    @ApiResponses(value = {
    @ApiResponse(code = 201, message = "Форум успешно создан. Возвращает данные созданного форума. ", response = Forum.class),
    @ApiResponse(code = 404, message = "Владелец форума не найден. ", response = Forum.class),
    @ApiResponse(code = 409, message = "Форум уже присутсвует в базе данных. Возвращает данные ранее созданного форума. ", response = Forum.class) })
    @RequestMapping(value = "/create",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<?> forumCreate(@ApiParam(value = "Данные форума." ,required=true ) @RequestBody Forum forum) {
        //log.info("Создание форума по слагу " + forum.getSlug());
        Forum forumSearchResult = null;
        try {
            forumSearchResult = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] { forum.getSlug().toLowerCase() }, new ForumMapper());
            forumSearchResult.setUser(jdbcTemplate.queryForObject(SEARCH_USER_NICKNAME_BY_ID_QUERY, String.class, Long.parseLong(forumSearchResult.getUser())));
        }
        catch (Exception e) {
            ////e.printStackTrace();
        }
        if (forumSearchResult != null) {
            return new ResponseEntity<>(forumSearchResult, HttpStatus.CONFLICT);
        }
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(SEARCH_USER_BY_NICKNAME_QUERY, new Object[] {forum.getUser().toLowerCase()}, new UsersMapper());
        }
        catch (Exception e) {
            ////e.printStackTrace();
            return new ResponseEntity<>(new Error("Владелец форума не найден."), HttpStatus.NOT_FOUND);
        }
        int result = 0;
        if (user != null)
            result = jdbcTemplate.update(CREATE_FORUM_QUERY, forum.getTitle(), forum.getSlug(), forum.getSlug().toLowerCase(), user.getId());
        forum.setUser(user.getNickname());
        return new ResponseEntity<>(forum, HttpStatus.CREATED);
    }

    @Override
    @ApiOperation(value = "Получение информации о форуме", notes = "Получение информации о форуме по его идентификаторе. ", response = Forum.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о форуме. ", response = Forum.class),
            @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Forum.class) })
    @RequestMapping(value = "/{slug}/details",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> forumGetOne(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug) {
        //log.info("Получение информации о форуме по слагу " + slug);
        Forum forumSearchResult = null;
        try {
            forumSearchResult = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] { slug.toLowerCase() }, new ForumMapper());
            forumSearchResult.setUser(jdbcTemplate.queryForObject(SEARCH_USER_NICKNAME_BY_ID_QUERY, String.class, Long.parseLong(forumSearchResult.getUser())));
        }
        catch (Exception e) {
            //e.printStackTrace();
            Error error = new Error();
            error.setMessage("Форум отсутсвует в системе.");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(forumSearchResult, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Список ветвей обсужления форума", notes = "Получение списка ветвей обсужления данного форума. Ветви обсуждения выводятся отсортированные по дате создания. ", response = Threads.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о ветках обсуждения на форуме. ", response = Threads.class),
            @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Threads.class) })
    @RequestMapping(value = "/{slug}/threads",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> forumGetThreads(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
    @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    @ApiParam(value = "Дата создания ветви обсуждения, с которой будут выводиться записи (ветвь обсуждения с указанной датой попадает в результат выборки). ") @RequestParam(value = "since", required = false) String since,
    @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc) {
/*        log.info("Список ветвей обсужления форума " + slug + "\n" +
                "since" + (since == null ? "null" : since.toString()) + "\n" +
                "desc: " + (desc == null ? "null" : desc.toString()) + "\n" +
                "limit: " + limit.toString());*/
        ArrayList<Thread> threads = null;

        try {
            jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] { slug.toLowerCase() }, new ForumMapper());
        }
        catch (Exception e) {
            return new ResponseEntity<>(new Error("Форум отсутсвует в системе."), HttpStatus.NOT_FOUND);
        }

        try {
            if (since == null) {
             if (desc == null || desc == false) {
                 threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS, new Object[] { slug.toLowerCase(), limit}, new ThreadsMapper());
             } else {
                 threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DESC, new Object[] { slug.toLowerCase(), limit}, new ThreadsMapper());
             }
            } else {
                if (desc == null || desc == false) {
                    threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DATE_LIMIT, new Object[] { slug.toLowerCase(), new Timestamp(new DateTime(since).getMillis()), limit}, new ThreadsMapper());
                } else {
                    threads = (ArrayList<Thread>)jdbcTemplate.query(SEARCH_FORUM_THREADS_DATE_LIMIT_DESC, new Object[] { slug.toLowerCase(), new Timestamp(new DateTime(since).getMillis()), limit}, new ThreadsMapper());
                }
            }
        }
        catch (Exception e) {
            //e.printStackTrace();
        }

        return new ResponseEntity<>(threads, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Пользователи данного форума", notes = "Получение списка пользователей, у которых есть пост или ветка обсуждения в данном форуме. Пользователи выводятся отсортированные по nickname в порядке возрастания. Порядок сотрировки должен соответсвовать побайтовому сравнение в нижнем регистре. ", response = Users.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о пользователях форума. ", response = Users.class),
            @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Users.class) })
    @RequestMapping(value = "/{slug}/users",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> forumGetUsers(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
    @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
    @ApiParam(value = "Идентификатор пользователя, с которого будут выводиться пользоватли (пользователь с данным идентификатором в результат не попадает). ") @RequestParam(value = "since", required = false) String since,
    @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc) {
        /*log.info("Пользователи данного форума:\n" +
                "slug: " + slug + "\n" +
                "since" + (since == null ? "null" : since.toString()) + "\n" +
                "desc: " + (desc == null ? "null" : desc.toString()) + "\n" +
                "limit: " + limit.toString());*/
        ArrayList<User> users = null;
        Forum forum = null;
        try{
            forum = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] {slug.toLowerCase()}, new ForumMapper());
        }catch (Exception e) {
            //e.printStackTrace();
            Error error = new Error();
            error.setMessage("Форум отсутсвует в системе.");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        try {
            if (since == null) {
                if (desc == null || desc == false) {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_USERS, new Object[] {forum.getId(), limit}, new UsersMapper());
                } else {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_USERS_DESC, new Object[] {forum.getId(), limit}, new UsersMapper());
                }
            } else {
                if (desc == null || desc == false) {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_USERS_USER_ID_LIMIT, new Object[] {forum.getId(), since.toLowerCase().getBytes(), limit}, new UsersMapper());
                } else {
                    users = (ArrayList<User>)jdbcTemplate.query(SEARCH_FORUM_USERS_USER_ID_LIMIT_DESC, new Object[] {forum.getId(), since.toLowerCase().getBytes(), limit}, new UsersMapper());
                }
            }
        }
        catch (Exception e) {
            //e.printStackTrace();
            Error error = new Error();
            error.setMessage("Форум отсутсвует в системе.");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    private String searchForumUsers() {
        String query = null;

        return query;
    }

    @Override
    @ApiOperation(value = "Создание ветки", notes = "Добавление новой ветки обсуждения на форум. ", response = Thread.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Ветка обсуждения успешно создана. Возвращает данные созданной ветки обсуждения. ", response = Thread.class),
            @ApiResponse(code = 404, message = "Автор ветки или форум не найдены. ", response = Thread.class),
            @ApiResponse(code = 409, message = "Ветка обсуждения уже присутсвует в базе данных. Возвращает данные ранее созданной ветки обсуждения. ", response = Thread.class) })
    @RequestMapping(value = "/{slug}/create",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<?> threadCreate(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
    @ApiParam(value = "Данные ветки обсуждения." ,required=true ) @RequestBody Thread thread) {
        //log.info("Создание ветки");
        Forum forumSearchResult = null;
        try {
            forumSearchResult = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[] { slug.toLowerCase() }, new ForumMapper());
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
        if (forumSearchResult == null) {
            return new ResponseEntity<>(new Error("Автор ветки или форум не найдены."), HttpStatus.NOT_FOUND);
        }
        Thread threadSearchResult = null;
/*        try {
            threadSearchResult = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_FORUM_AND_THREAD_SLUGS, new Object[] { slug, thread.getTitle() }, new ThreadsMapper());
        }
        catch (Exception e) {
            //e.printStackTrace();
        }*/
        if (thread.getSlug() != null/* && threadSearchResult == null*/) {
            try {
                threadSearchResult = jdbcTemplate.queryForObject(SEARCH_TRHEAD_BY_SLUGS, new Object[] { thread.getSlug().toLowerCase() }, new ThreadsMapper());
            }
            catch (Exception e) {
                //e.printStackTrace();
            }
        }
        if (threadSearchResult != null) {
            return new ResponseEntity<>(threadSearchResult, HttpStatus.CONFLICT);
        }
        User user = null;
        try {
            user = jdbcTemplate.queryForObject(SEARCH_USER_BY_NICKNAME_QUERY, new Object[] {thread.getAuthor().toLowerCase()}, new UsersMapper());
        }
        catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Автор ветки или форум не найдены."), HttpStatus.NOT_FOUND);
        }
        Integer thread_id = new Integer(0);
        Timestamp time = new Timestamp(DateTime.now().toDateTime(DateTimeZone.UTC).getMillis());
        if (thread.getCreated() != null) {
            time = new Timestamp((new DateTime(thread.getCreated()).toDateTime(DateTimeZone.UTC)).getMillis());
        }
        try {
            if (user != null) {
                long forum_id = 0;
                forum_id = jdbcTemplate.queryForObject(SEARCH_FORIM_ID_BY_SLUG, Long.class, slug.toLowerCase());
                thread_id = jdbcTemplate.queryForObject(CREATE_THREAD_QUERY, Integer.class, thread.getTitle(), user.getId(), forum_id, thread.getMessage(), thread.getSlug(), thread.getSlug() == null ? thread.getSlug() : thread.getSlug().toLowerCase(), time);
            }
            else throw new Exception();
        }
        catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        thread.setForum(forumSearchResult.getSlug());
        thread.setVotes(new Integer(0));
        thread.setId(thread_id);
        jdbcTemplate.update(UPDATE_FORUM_THREADS_COUNTER,forumSearchResult.getSlug().toLowerCase());
        return new ResponseEntity<>(thread, HttpStatus.CREATED);
    }
}
