package com.dturan.Mapper;

import com.dturan.model.Vote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteMapper implements RowMapper<Vote> {

    @Override
    public Vote mapRow(ResultSet resultSet, int i) throws SQLException {
        Vote vote = new Vote();
        vote.setVoice(resultSet.getInt("voice"));
        vote.setId(resultSet.getInt("id"));
        return vote;
    }
}
