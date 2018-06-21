package com.dturan.Mapper;

import com.dturan.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusMapper implements RowMapper<Status> {
    @Override
    public Status mapRow(ResultSet resultSet, int i) throws SQLException {
        Status status = new Status();
        try {
            status.setForum(resultSet.getInt("forums"));
        } catch (Exception e) {
            status.setForum(new Integer((0)));
        }
        try {
            status.setPost(resultSet.getInt("posts"));
        } catch (Exception e) {
            status.setPost(new Integer((0)));
        }
        try {
            status.setThread(resultSet.getInt("threads"));
        } catch (Exception e) {
            status.setThread(new Integer((0)));
        }
        try {
            status.setUser(resultSet.getInt("users"));
        } catch (Exception e) {
            status.setUser(new Integer((0)));
        }
        return status;
    }

}
