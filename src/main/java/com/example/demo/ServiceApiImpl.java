package com.example.demo;

import com.dturan.Mapper.StatusMapper;
import com.dturan.api.ServiceApi;
import com.dturan.model.Status;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/service/")
public class ServiceApiImpl implements ServiceApi {
    private static Logger log = Logger.getLogger(ServiceApiImpl.class.getName());

    private static final String SERVECE_STATUS =
        "SELECT COUNT(forums.*) as forums,\n" +
                "SUM(forums.posts) as posts,\n" +
                "SUM(forums.threads) as threads,\n" +
                "u.count as users\n" +
        "FROM forums, (SELECT COUNT(*) FROM users) as u\n" +
        "GROUP BY users;";

    private static final String CLEAR_BASE =
            "DELETE FROM VOTES;\n" +
            "DELETE FROM POSTS;\n" +
            "DELETE FROM THREADS;\n" +
            "DELETE FROM USERS_IN_FORUMS;\n" +
            "DELETE FROM FORUMS;\n" +
            "DELETE FROM USERS;";

    @NotNull
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ServiceApiImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @ApiOperation(value = "Очистка всех данных в базе", notes = "Безвозвратное удаление всей пользовательской информации из базы данных. ", response = Void.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Очистка базы успешно завершена", response = Void.class) })
    @RequestMapping(value = "/clear",
            produces = { "application/json" },
            consumes = { "application/json", "application/octet-stream" },
            method = RequestMethod.POST)
    public ResponseEntity<Void> clear() {
        //log.info("Очистка всех данных в базе");
        jdbcTemplate.update(CLEAR_BASE);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    @ApiOperation(value = "Получение инфомарции о базе данных", notes = "Получение инфомарции о базе данных. ", response = Status.class, tags={  })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Кол-во записей в базе данных, включая помеченные как \"удалённые\". ", response = Status.class) })
    @RequestMapping(value = "/status",
            produces = { "application/json" },
            method = RequestMethod.GET)
    public ResponseEntity<Status> status() {
        //log.info("Получение инфомарции о базе данных");
        Status status = null;
        try {
            status = jdbcTemplate.queryForObject(SERVECE_STATUS, new StatusMapper());
        } catch (Exception e) {
            status = new Status();
            status.setUser(new Integer(0));
            status.setThread(new Integer(0));
            status.setPost(new Integer(0));
            status.setForum(new Integer(0));
        }
        return new ResponseEntity<Status>(status, HttpStatus.OK);
    }

}
