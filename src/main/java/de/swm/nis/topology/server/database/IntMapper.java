package de.swm.nis.topology.server.database;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class IntMapper implements RowMapper<Integer>{
    @Override
    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt(1);
    }
}
