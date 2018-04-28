package com.dturan.Mapper;

import com.dturan.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersMapper  implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        try {
            user.setId(resultSet.getBigDecimal("id"));
        } catch (Exception e) {}
        user.setAbout(resultSet.getString("about"));
        user.setEmail(resultSet.getString("email"));
        user.setFullname(resultSet.getString("fullname"));
        user.setNickname(resultSet.getString("nickname"));
        return user;
    }

}
