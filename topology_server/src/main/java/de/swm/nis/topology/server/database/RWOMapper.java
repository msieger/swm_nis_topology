package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.RWO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RWOMapper implements RowMapper<RWO>{
    @Override
    public RWO mapRow(ResultSet rs, int rowNum) throws SQLException {
        RWO rwo = new RWO();
        rwo.setId(rs.getLong("rwo_id"));
        rwo.setType(rs.getString("typ"));
        rwo.setField(rs.getString("field"));
        return rwo;
    }
}
