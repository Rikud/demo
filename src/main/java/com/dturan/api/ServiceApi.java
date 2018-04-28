package com.dturan.api;

import com.dturan.model.Status;

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
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-25T22:28:04.198-07:00")

@Api(value = "service", description = "the service API")
public interface ServiceApi {

    @ApiOperation(value = "Очистка всех данных в базе", notes = "Безвозвратное удаление всей пользовательской информации из базы данных. ", response = Void.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Очистка базы успешно завершена", response = Void.class) })
    @RequestMapping(value = "/service/clear",
        produces = { "application/json" }, 
        consumes = { "application/json", "application/octet-stream" },
        method = RequestMethod.POST)
    ResponseEntity<Void> clear();


    @ApiOperation(value = "Получение инфомарции о базе данных", notes = "Получение инфомарции о базе данных. ", response = Status.class, tags={  })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Кол-во записей в базе данных, включая помеченные как \"удалённые\". ", response = Status.class) })
    @RequestMapping(value = "/service/status",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Status> status();

}
