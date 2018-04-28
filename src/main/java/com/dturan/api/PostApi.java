package com.dturan.api;

import com.dturan.model.Post;
import com.dturan.model.PostFull;
import com.dturan.model.PostUpdate;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

@Api(value = "post", description = "the post API")
public interface PostApi {

    @ApiOperation(value = "Получение информации о ветке обсуждения", notes = "Получение информации о ветке обсуждения по его имени. ", response = PostFull.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о ветке обсуждения. ", response = PostFull.class),
        @ApiResponse(code = 404, message = "Ветка обсуждения отсутсвует в форуме. ", response = PostFull.class) })
    @RequestMapping(value = "/post/{id}/details",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<?> postGetOne(@ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") BigDecimal id,
         @ApiParam(value = "Включение полной информации о соответвующем объекте сообщения. Если тип объекта не указан, то полная информация об этих объектах не передаётся. ", allowableValues = "USER, FORUM, THREAD") @RequestParam(value = "related", required = false) List<String> related);


    @ApiOperation(value = "Изменение сообщения", notes = "Изменение сообщения на форуме. Если сообщение поменяло текст, то оно должно получить отметку `isEdited`. ", response = Post.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Информация о сообщении. ", response = Post.class),
        @ApiResponse(code = 404, message = "Сообщение отсутсвует в форуме. ", response = Post.class) })
    @RequestMapping(value = "/post/{id}/details",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<?> postUpdate(@ApiParam(value = "Идентификатор сообщения.",required=true ) @PathVariable("id") BigDecimal id,
        @ApiParam(value = "Изменения сообщения." ,required=true ) @RequestBody PostUpdate post);

}
