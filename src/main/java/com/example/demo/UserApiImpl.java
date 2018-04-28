package com.example.demo;

import com.dturan.Mapper.UsersMapper;
import com.dturan.api.UserApi;
import com.dturan.model.Error;
import com.dturan.model.User;
import com.dturan.model.UserUpdate;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/user")
public class UserApiImpl implements UserApi {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SEARCH_USER_BY_NICKNAME_OR_EMAIL_QUERY = "SELECT * FROM USERS WHERE lower(nickname) = lower(?) OR lower(email) = lower(?)";
    private static final String SEARCH_USER_BY_NICKNAME_QUERY = "SELECT * FROM USERS WHERE lower(nickname) = lower(?)";
    private static final String SEARCH_USER_BY_EMAIL_QUERY = "SELECT * FROM USERS WHERE lower(email) = lower(?)";
    private static final String CREATE_USER =
        "INSERT INTO users (nickname, fullname, about, email)\n" +
        "VALUES (?, ?, ?, ?);";
    private static final String UPDATE_USER_PROFILE_QUERY =
            "UPDATE users SET fullname = ?, about = ?, email = ?\n" +
            "WHERE nickname = ?";

    @Override
    @ApiOperation(value = "Создание нового пользователя", notes = "Создание нового пользователя в базе данных. ", response = User.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Пользователь успешно создан. Возвращает данные созданного пользователя. ", response = User.class),
        @ApiResponse(code = 409, message = "Пользователь уже присутсвует в базе данных. Возвращает данные ранее созданных пользователей с тем же nickname-ом иои email-ом. ", response = User.class) })
    @RequestMapping(value = "/{nickname}/create",
        produces = { "application/json" },
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public ResponseEntity<?> userCreate(@ApiParam(value = "Идентификатор пользователя.",required=true ) @PathVariable("nickname") String nickname,
    @ApiParam(value = "Данные пользовательского профиля." ,required=true ) @RequestBody User profile) {
        User user = null;
        ArrayList<User> users = null;

        try {
            users = (ArrayList<User>)jdbcTemplate.query(SEARCH_USER_BY_NICKNAME_OR_EMAIL_QUERY, new Object[] { nickname, profile.getEmail() }, new UsersMapper());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (users != null && users.size() != 0) {
            return new ResponseEntity<>(users, HttpStatus.CONFLICT);
        }

        user = profile;
        user.setNickname(nickname);

        jdbcTemplate.update(CREATE_USER, nickname, user.getFullname(), user.getAbout(), user.getEmail());

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @Override
    @ApiOperation(value = "Получение информации о пользователе", notes = "Получение информации о пользователе форума по его имени. ", response = User.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Информация о пользователе. ", response = User.class),
        @ApiResponse(code = 404, message = "Пользователь отсутсвует в системе. ", response = User.class) })
    @RequestMapping(value = "/{nickname}/profile",
        produces = { "application/json" },
        method = RequestMethod.GET)
    public ResponseEntity<?> userGetOne(@ApiParam(value = "Идентификатор пользователя.",required=true ) @PathVariable("nickname") String nickname) {

        User user = null;

        try {
            user = jdbcTemplate.queryForObject(SEARCH_USER_BY_NICKNAME_QUERY, new Object[] { nickname }, new UsersMapper());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Error("Пользователь не найден."), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @Override
    @ApiOperation(value = "Изменение данных о пользователе", notes = "Изменение информации в профиле пользователя. ", response = User.class, tags={  })
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Актуальная информация о пользователе после изменения профиля. ", response = User.class),
        @ApiResponse(code = 404, message = "Пользователь отсутсвует в системе. ", response = User.class),
        @ApiResponse(code = 409, message = "Новые данные профиля пользователя конфликтуют с имеющимися пользователями. ", response = User.class) })
    @RequestMapping(value = "/{nickname}/profile",
        produces = { "application/json" },
        consumes = { "application/json" },
        method = RequestMethod.POST)
    public ResponseEntity<?> userUpdate(@ApiParam(value = "Идентификатор пользователя.",required=true ) @PathVariable("nickname") String nickname,
    @ApiParam(value = "Изменения профиля пользователя." ,required=true ) @RequestBody UserUpdate profile) {

        User user = null;
        try {
            user = (User)this.userGetOne(nickname).getBody();
        }  catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new Error("Пользователь отсутсвует в системе."), HttpStatus.NOT_FOUND);
        }

        ArrayList<User> users = null;
        try {
            users = (ArrayList<User>)jdbcTemplate.query(SEARCH_USER_BY_EMAIL_QUERY, new Object[] { profile.getEmail() }, new UsersMapper());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (users != null && users.size() != 0) {
            return new ResponseEntity<>(new Error("This email is already registered by user: " + user.getNickname()), HttpStatus.CONFLICT);
        }

        user.setProfile(profile);

        try {
            jdbcTemplate.update(UPDATE_USER_PROFILE_QUERY, user.getFullname(), user.getAbout(), user.getEmail(), user.getNickname());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
