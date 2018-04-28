package com.dturan.Mapper;

import com.dturan.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusMapper implements RowMapper<Status> {
    @Override
    public Status mapRow(ResultSet resultSet, int i) throws SQLException {
        Status status = new Status();
        try {
            status.setForum(resultSet.getBigDecimal("forums"));
        } catch (Exception e) {
            status.setForum(new BigDecimal((0)));
        }
        try {
            status.setPost(resultSet.getBigDecimal("posts"));
        } catch (Exception e) {
            status.setPost(new BigDecimal((0)));
        }
        try {
            status.setThread(resultSet.getBigDecimal("threads"));
        } catch (Exception e) {
            status.setThread(new BigDecimal((0)));
        }
        try {
            status.setUser(resultSet.getBigDecimal("users"));
        } catch (Exception e) {
            status.setUser(new BigDecimal((0)));
        }
        return status;
    }

}
