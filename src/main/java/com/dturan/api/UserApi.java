package com.dturan.api;

import com.dturan.model.User;
import com.dturan.model.UserUpdate;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

@Api(value = "user", description = "the user API")
public interface UserApi {

    @ApiOperation(value = "Создание нового пользователя", notes = "Создание нового пользователя в базе данных. ", response = User.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Пользователь успешно создан. Возвращает данные созданного пользователя. ", response = User.class),
        @ApiResponse(code = 409, message = "Пользователь уже присутсвует в базе данных. Возвращает данные ранее созданных пользователей с тем же nickname-ом иои email-ом. ", response = User.class) })
    @RequestMapping(value = "/user/{nickname}/create",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<?> userCreate(@ApiParam(value = "Идентификатор пользователя.",required=true ) @PathVariable("nickname") String nickname,
        @ApiParam(value = "Данные пользовательского профиля." ,required=true ) @RequestBody User profile);


    @ApiOperation(value = "Получение информации о пользователе", notes = "Получение информации о пользователе форума по его имени. ", response = User.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о пользователе. ", response = User.class),
        @ApiResponse(code = 404, message = "Пользователь отсутсвует в системе. ", response = User.class) })
    @RequestMapping(value = "/user/{nickname}/profile",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<?> userGetOne(@ApiParam(value = "Идентификатор пользователя.",required=true ) @PathVariable("nickname") String nickname);


    @ApiOperation(value = "Изменение данных о пользователе", notes = "Изменение информации в профиле пользователя. ", response = User.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Актуальная информация о пользователе после изменения профиля. ", response = User.class),
        @ApiResponse(code = 404, message = "Пользователь отсутсвует в системе. ", response = User.class),
        @ApiResponse(code = 409, message = "Новые данные профиля пользователя конфликтуют с имеющимися пользователями. ", response = User.class) })
    @RequestMapping(value = "/user/{nickname}/profile",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<?> userUpdate(@ApiParam(value = "Идентификатор пользователя.",required=true ) @PathVariable("nickname") String nickname,
        @ApiParam(value = "Изменения профиля пользователя." ,required=true ) @RequestBody UserUpdate profile);

}
