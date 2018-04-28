package com.dturan.api;

import java.math.BigDecimal;
import com.dturan.model.Item;
import java.util.List;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import javax.validation.constraints.*;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-20T11:21:44.341-07:00")

@Api(value = "api", description = "the api API")
public interface ApiApi {

    @ApiOperation(value = "", notes = "Добавление одной или нескольких записей. ", response = Item.class, responseContainer = "List", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "Created", response = Item.class) })
    @RequestMapping(value = "/api",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.POST)
    ResponseEntity<List<Item>> addMulti(@ApiParam(value = ""  ) @RequestBody List<Item> body);


    @ApiOperation(value = "", notes = "Удаление записи.", response = Void.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 204, message = "Запись удалена.", response = Void.class),
        @ApiResponse(code = 404, message = "Запись не найдена.", response = Void.class) })
    @RequestMapping(value = "/api/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.DELETE)
    ResponseEntity<Void> destroyOne(@ApiParam(value = "",required=true ) @PathVariable("id") BigDecimal id);


    @ApiOperation(value = "Получение списка записей", notes = "Получение списка записей.  Записи выводятся отсортированные по идентификатору. ", response = Item.class, responseContainer = "List", tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = Item.class) })
    @RequestMapping(value = "/api",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<List<Item>> find( @ApiParam(value = "Идентификатор записи, с которой будет возвращаться записи. ") @RequestParam(value = "since", required = false) BigDecimal since,
         @ApiParam(value = "") @RequestParam(value = "desc", required = false) Boolean desc,
         @Min(1) @Max(10000) @ApiParam(value = "Максимальное кол-во возвращаемых записей.", defaultValue = "100") @RequestParam(value = "limit", required = false, defaultValue="100") BigDecimal limit);


    @ApiOperation(value = "", notes = "Просмотр записи.", response = Item.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = Item.class),
        @ApiResponse(code = 404, message = "Запись не найдена.", response = Item.class) })
    @RequestMapping(value = "/api/{id}",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Item> getOne(@ApiParam(value = "",required=true ) @PathVariable("id") BigDecimal id);


    @ApiOperation(value = "", notes = "Изменение записи.", response = Item.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK", response = Item.class),
        @ApiResponse(code = 404, message = "Запись не найдена.", response = Item.class) })
    @RequestMapping(value = "/api/{id}",
        produces = { "application/json" }, 
        consumes = { "application/json" },
        method = RequestMethod.PUT)
    ResponseEntity<Item> updateOne(@ApiParam(value = "",required=true ) @PathVariable("id") BigDecimal id,
        @ApiParam(value = ""  ) @RequestBody Item body);

}
