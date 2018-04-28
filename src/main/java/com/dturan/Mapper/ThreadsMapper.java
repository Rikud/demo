package com.dturan.Mapper;

import com.dturan.model.Thread;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThreadsMapper implements RowMapper<Thread> {
    @Override
    public Thread mapRow(ResultSet resultSet, int i) throws SQLException {
        Thread thread = new Thread();
        thread.setAuthor(resultSet.getString("author"));
        thread.setCreated(new DateTime(resultSet.getTimestamp("created").getTime()));
        thread.setForum(resultSet.getString("forum"));
        thread.setId(resultSet.getBigDecimal("id"));
        thread.setMessage(resultSet.getString("message"));
        thread.setSlug(resultSet.getString("slug"));
        thread.setTitle(resultSet.getString("tittle"));
        thread.setVotes(resultSet.getBigDecimal("votes"));
        return thread;
    }
}
