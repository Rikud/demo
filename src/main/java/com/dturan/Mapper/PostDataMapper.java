package com.dturan.Mapper;

import com.dturan.model.PostData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostDataMapper implements RowMapper<PostData> {

    @Override
    public PostData mapRow(ResultSet resultSet, int i) throws SQLException {
        PostData postData = new PostData();
        postData.setId(resultSet.getInt("id"));
        postData.setBranch(resultSet.getInt("branch"));
        return postData;
    }

}
