package com.dturan.api;

import com.dturan.model.Posts;
import com.dturan.model.Thread;
import com.dturan.model.ThreadUpdate;
import com.dturan.model.Vote;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

@Api(value = "thread", description = "the thread API")
public interface ThreadApi {

    @ApiOperation(value = "Создание новых постов", notes = "Добавление новых постов в ветку обсуждения на форум. Все посты, созданные в рамках одного вызова данного метода должны иметь одинаковую дату создания (Post.Created). ", response = Posts.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Посты успешно созданы. Возвращает данные созданных постов в том же порядке, в котором их передали на вход метода. ", response = Posts.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутствует в базе данных. ", response = Posts.class),
        @ApiResponse(code = 409, message = "Хотя бы один родительский пост отсутсвует в текущей ветке обсуждения. ", response = Posts.class) })
    @RequestMapping(value = "/thread/{slug_or_id}/create",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<?> postsCreate(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
        @ApiParam(value = "Список создаваемых постов." ,required=true ) @RequestBody Posts posts);


    @ApiOperation(value = "Получение информации о ветке обсуждения", notes = "Получение информации о ветке обсуждения по его имени. ", response = Thread.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = Thread.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Thread.class) })
    @RequestMapping(value = "/thread/{slug_or_id}/details",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<?> threadGetOne(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId);


    @ApiOperation(value = "Сообщения данной ветви обсуждения", notes = "Получение списка сообщений в данной ветке форуме. Сообщения выводятся отсортированные по дате создания. ", response = Posts.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о сообщениях форума. ", response = Posts.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Posts.class) })
    @RequestMapping(value = "/thread/{slug_or_id}/posts",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<?> threadGetPosts(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
         @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") Integer limit,
         @ApiParam(value = "Идентификатор поста, после которого будут выводиться записи (пост с данным идентификатором в результат не попадает). ") @RequestParam(value = "since", required = false) Integer since,
         @ApiParam(value = "Вид сортировки:  * flat - по дате, комментарии выводятся простым списком в порядке создания;  * tree - древовидный, комментарии выводятся отсортированные в дереве    по N штук;  * parent_tree - древовидные с пагинацией по родительским (parent_tree),    на странице N родительских комментов и все комментарии прикрепленные    к ним, в древвидном отображение. Подробности: https://park.mail.ru/blog/topic/view/1191/ ", allowableValues = "FLAT, TREE, PARENT_TREE", defaultValue = "flat") @RequestParam(value = "sort", required = false, defaultValue="flat") String sort,
         @ApiParam(value = "Флаг сортировки по убыванию. ") @RequestParam(value = "desc", required = false) Boolean desc);


    @ApiOperation(value = "Обновление ветки", notes = "Обновление ветки обсуждения на форуме. ", response = Thread.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = Thread.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Thread.class) })
    @RequestMapping(value = "/thread/{slug_or_id}/details",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<?> threadUpdate(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
        @ApiParam(value = "Данные ветки обсуждения." ,required=true ) @RequestBody ThreadUpdate thread);


    @ApiOperation(value = "Проголосовать за ветвь обсуждения", notes = "Изменение голоса за ветвь обсуждения. Один пользователь учитывается только один раз и может изменить своё мнение. ", response = Thread.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = Thread.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = Thread.class) })
    @RequestMapping(value = "/thread/{slug_or_id}/vote",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<?> threadVote(@ApiParam(value = "Идентификатор ветки обсуждения.",required=true ) @PathVariable("slugOrId") String slugOrId,
        @ApiParam(value = "Информация о голосовании пользователя." ,required=true ) @RequestBody Vote vote);

}
