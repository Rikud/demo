package com.dturan.Mapper;

import com.dturan.model.Post;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostsMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet resultSet, int i) throws SQLException {
        Post post = new Post();
        post.setId(resultSet.getBigDecimal("id"));
        post.setParent(resultSet.getBigDecimal("parent"));
        post.setAuthor(resultSet.getString("author"));
        post.setThread(resultSet.getBigDecimal("thread"));
        post.setForum(resultSet.getString("forum"));
        post.setMessage(resultSet.getString("message"));
        post.setIsEdited(resultSet.getBoolean("isedited"));
        post.setCreated(new DateTime(resultSet.getTimestamp("created")).toDateTime(DateTimeZone.UTC));
        return post;
    }
}
