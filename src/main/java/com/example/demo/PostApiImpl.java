package com.example.demo;

import com.dturan.Mapper.ForumMapper;
import com.dturan.Mapper.PostsMapper;
import com.dturan.Mapper.ThreadsMapper;
import com.dturan.Mapper.UsersMapper;
import com.dturan.api.PostApi;
import com.dturan.model.Error;
import com.dturan.model.*;
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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/post/")
public class PostApiImpl implements PostApi {

    @NotNull
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PostApiImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private static Logger log = Logger.getLogger(PostApiImpl.class.getName());

    private static final String SEARCH_POST_BY_ID =
            "SELECT posts.id,   \n" +
            "  posts.parent,   \n" +
            "  users.nickname AS author,   \n" +
            "  posts.thread,   \n" +
            "  forums.slug AS forum,   \n" +
            "  posts.message,   \n" +
            "  posts.isedited,   \n" +
            "  posts.created  \n" +
            "FROM posts \n" +
            "JOIN users ON posts.author = users.id \n" +
            "JOIN forums ON posts.forum = forums.id \n" +
            "WHERE posts.id = ?;";
    private static final String SEARCH_USER_BY_NICKNAME = "SELECT * FROM users WHERE nickname_lower = ?;";
    private static final String SEARCH_FORUM_BY_SLUG =
            "SELECT forums.id, forums.slug,\n" +
            "forums.tittle, \n" +
            "users.nickname as user_id, \n" +
            "forums.threads, \n" +
            "forums.posts FROM forums, users \n" +
            "WHERE forums.slug_lower = ? AND forums.user_id = users.id;";
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
    @ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") Integer id,
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
        /*log.info("Получение подробной информации о посте:" +
                "id: " + id.toString() + "\n" +
                "user " + post.getAuthor().toString() + "\n" +
                "forum " + post.getForum().toString() + "\n" +
                "thread " + post.getThread().toString());
        if (related != null)
            for (int i = 0; i < related.size(); i++) {
                log.info(related.get(i).toString() + "\n");
            }*/
        if (related != null) {
            for (int i = 0; i < related.size(); i++) {
                if (related.get(i).equals("user")) {
                    User author = jdbcTemplate.queryForObject(SEARCH_USER_BY_NICKNAME, new Object[]{post.getAuthor().toLowerCase()}, new UsersMapper());
                    postFull.setAuthor(author);
                }
                if (related.get(i).equals("forum")) {
                    Forum forum = jdbcTemplate.queryForObject(SEARCH_FORUM_BY_SLUG, new Object[]{post.getForum().toLowerCase()}, new ForumMapper());
                    postFull.setForum(forum);
                }
                if (related.get(i).equals("thread")) {
                    Thread thread = jdbcTemplate.queryForObject(SEARCH_THREAD_BY_ID, new Object[]{post.getThread()}, new ThreadsMapper());
                    postFull.setThread(thread);
                }
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
    public ResponseEntity<?> postUpdate(@ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") Integer id,
    @ApiParam(value = "Изменения сообщения." ,required=true ) @RequestBody PostUpdate post) {
        //log.info("Изменение сообщения");
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