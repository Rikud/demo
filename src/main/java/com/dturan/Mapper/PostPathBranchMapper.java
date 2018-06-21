package com.dturan.Mapper;

import com.dturan.model.PostPathBranch;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostPathBranchMapper implements RowMapper<PostPathBranch> {

    @Override
    public PostPathBranch mapRow(ResultSet resultSet, int i) throws SQLException {
        PostPathBranch postPathBranch = new PostPathBranch();
        postPathBranch.setPath(resultSet.getString("path"));
        try {
            postPathBranch.setBranch(resultSet.getInt("branch"));
        } catch (Exception e) {}
        return postPathBranch;
    }

}
