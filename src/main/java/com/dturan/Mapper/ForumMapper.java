package com.dturan.Mapper;

import com.dturan.model.Forum;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ForumMapper implements RowMapper<Forum> {

    @Override
    public Forum mapRow(ResultSet resultSet, int i) throws SQLException {
        Forum forum = new Forum();
        forum.setSlug(resultSet.getString("slug"));
        forum.setTitle(resultSet.getString("tittle"));
        forum.setUser(resultSet.getString("user_id"));
        forum.setThreads(resultSet.getBigDecimal("threads"));
        forum.setPosts(resultSet.getBigDecimal("posts"));
        return forum;
    }
}