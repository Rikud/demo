package com.example.demo;

import com.dturan.Mapper.ForumMapper;
import com.dturan.Mapper.PostsMapper;
import com.dturan.Mapper.ThreadsMapper;
import com.dturan.Mapper.UsersMapper;
import com.dturan.api.PostApi;
import com.dturan.model.*;
import com.dturan.model.Error;
import com.dturan.model.Thread;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/post/")
public class PostApiImpl implements PostApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SEARCH_POST_BY_ID =
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
    private static final String SEARCH_USER_BY_NICKNAME = "SELECT * FROM users WHERE lower(nickname) = lower(?);";
    private static final String SEARCH_FORUM_BY_SLUG =
            "SELECT\n" +
                "forums.slug,\n" +
                "forums.tittle,\n" +
                "users.nickname as user_id,\n" +
                "forums.threads,\n" +
                "forums.posts FROM forums, users\n" +
                "WHERE lower(forums.slug) = lower(?) AND forums.user_id = users.id;";
    private static final String SEARCH_THREAD_BY_ID =
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
    private static final String UPDATE_POST =
            "UPDATE posts SET message = ?, isedited = TRUE WHERE id = ?";

    @Override
    @ApiOperation(value = "Получение информации о ветке обсуждения", notes = "Получение информации о ветке обсуждения по его имени. ", response = PostFull.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = PostFull.class),
            @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = PostFull.class) })
    @RequestMapping(value = "/{id}/details",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<?> postGetOne(
    @ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") BigDecimal id,
    @ApiParam(value = "Включение полной информации о соответвующем объекте сообщения. Если тип объекта" +
    " не указан, то полная информация об этих объектах не передаётся. ",
    allowableValues = "USER, FORUM, THREAD")
    @RequestParam(value = "related", required = false) List<String> related) {
        Post post = null;
        try {
            post = jdbcTemplate.queryForObject(SEARCH_POST_BY_ID, new Object[] { id }, new PostsMapper());
        }
        catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("\"Can't find post with id: " + id), HttpStatus.NOT_FOUND);
        }
        PostFull postFull = new PostFull();
        postFull.setPost(post);
        if (related != null) {
            if (related.contains("user")) {
                User author = jdbcTemplate.queryForObject(SEARCH_USER_BY_NICKNAME, new Object[]{post.getAuthor()}, new UsersMapper());
                postFull.setAuthor(author);
            }
            if (related.contains("forum")) {
                Forum forum = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[]{post.getForum()}, new ForumMapper());
                postFull.setForum(forum);
            }
            if (related.contains("thread")) {
                Thread thread = jdbcTemplate.queryForObject(SEARCH_THREAD_BY_ID, new Object[]{post.getThread()}, new ThreadsMapper());
                postFull.setThread(thread);
            }
        }
        return new ResponseEntity<>(postFull, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Изменение сообщения", notes = "Изменение сообщения на форуме. Если сообщение поменяло текст, то оно должно получить отметку `isEdited`. ", response = Post.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Информация о сообщении. ", response = Post.class),
            @ApiResponse(code = 404, message = "Сообщение отсутсвует в форуме. ", response = Post.class) })
    @RequestMapping(value = "/{id}/details",
            produces = { "application/json" },
            consumes = { "application/json" },
            method = RequestMethod.POST)
    public ResponseEntity<?> postUpdate(@ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") BigDecimal id,
    @ApiParam(value = "Изменения сообщения." ,required=true ) @RequestBody PostUpdate post) {
        Post post1 = new Post();
        try {
            post1 = jdbcTemplate.queryForObject(SEARCH_POST_BY_ID, new Object[] { id }, new PostsMapper());
        }
        catch (Exception e) {
            //e.printStackTrace();
            return new ResponseEntity<>(new Error("Сообщение отсутсвует в форуме."), HttpStatus.NOT_FOUND);
        }
        if (post == null || post.getMessage() == null || post.getMessage().equals(post1.getMessage())) {
            return new ResponseEntity<>(post1, HttpStatus.OK);
        }
        jdbcTemplate.update(UPDATE_POST, post.getMessage(),post1.getId());
        post1.setMessage(post.getMessage());
        post1.setIsEdited(true);
        return new ResponseEntity<>(post1, HttpStatus.OK);
    }
}