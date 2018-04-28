package com.dturan.Mapper;

import com.dturan.model.Vote;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteMapper implements RowMapper<Vote> {

    @Override
    public Vote mapRow(ResultSet resultSet, int i) throws SQLException {
        Vote vote = new Vote();
        vote.setVoice(resultSet.getBigDecimal("voice"));
        vote.setId(resultSet.getBigDecimal("id"));
        return vote;
    }
}
