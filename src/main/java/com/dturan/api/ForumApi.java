package com.dturan.api;

import com.dturan.model.Forum;
import com.dturan.model.Thread;
import com.dturan.model.Threads;
import com.dturan.model.Users;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

@Api(value = "forum", description = "the forum API")
public interface ForumApi {

    @ApiOperation(value = "Создание форума", notes = "Создание нового форума. ", response = Forum.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Форум успешно создан. Возвращает данные созданного форума. ", response = Forum.class),
        @ApiResponse(code = 404, message = "Владелец форума не найден. ", response = Forum.class),
        @ApiResponse(code = 409, message = "Форум уже присутсвует в базе данных. Возвращает данные ранее созданного форума. ", response = Forum.class) })
    @RequestMapping(value = "/forum/create",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<?> forumCreate(@ApiParam(value = "Данные форума." ,required=true ) @RequestBody Forum forum);


    @ApiOperation(value = "Получение информации о форуме", notes = "Получение информации о форуме по его идентификаторе. ", response = Forum.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о форуме. ", response = Forum.class),
        @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Forum.class) })
    @RequestMapping(value = "/forum/{slug}/details",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<?> forumGetOne(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug);


    @ApiOperation(value = "Список ветвей обсужления форума", notes = "Получение списка ветвей обсужления данного форума. Ветви обсуждения выводятся отсортированные по дате создания. ", response = Threads.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о ветках обсуждения на форуме. ", response = Threads.class),
        @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Threads.class) })
    @RequestMapping(value = "/forum/{slug}/threads",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<?> forumGetThreads(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
         @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") BigDecimal limit,
         @ApiParam(value = "Дата создания ветви обсуждения, с которой будут выводиться записи (ветвь обсуждения с указанной датой попадает в результат выборки). ") @RequestParam(value = "since", required = false) String since,
         @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc);


    @ApiOperation(value = "Пользователи данного форума", notes = "Получение списка пользователей, у которых есть пост или ветка обсуждения в данном форуме. Пользователи выводятся отсортированные по nickname в порядке возрастания. Порядок сотрировки должен соответсвовать побайтовому сравнение в нижнем регистре. ", response = Users.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о пользователях форума. ", response = Users.class),
        @ApiResponse(code = 404, message = "Форум отсутсвует в системе. ", response = Users.class) })
    @RequestMapping(value = "/forum/{slug}/users",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<?> forumGetUsers(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
         @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") BigDecimal limit,
         @ApiParam(value = "Идентификатор пользователя, с которого будут выводиться пользоватли (пользователь с данным идентификатором в результат не попадает). ") @RequestParam(value = "since", required = false) String since,
         @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc);


    @ApiOperation(value = "Создание ветки", notes = "Добавление новой ветки обсуждения на форум. ", response = Thread.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Ветка обсуждения успешно создана. Возвращает данные созданной ветки обсуждения. ", response = Thread.class),
        @ApiResponse(code = 404, message = "Автор ветки или форум не найдены. ", response = Thread.class),
        @ApiResponse(code = 409, message = "Ветка обсуждения уже присутсвует в базе данных. Возвращает данные ранее созданной ветки обсуждения. ", response = Thread.class) })
    @RequestMapping(value = "/forum/{slug}/create",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
        ResponseEntity<?> threadCreate(@ApiParam(value = "Идентификатор форума.",required=true ) @PathVariable("slug") String slug,
            @ApiParam(value = "Данные ветки обсуждения." ,required=true ) @RequestBody Thread thread);

}
