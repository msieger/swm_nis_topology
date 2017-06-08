package de.swm.nis.topology.server.routing;

import de.swm.nis.topology.server.domain.Node;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PgRoutingResultMapper implements RowMapper<PgRoutingResult>{


    @Override
    public PgRoutingResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        PgRoutingResult result = new PgRoutingResult();
        result.setNode(new Node(rs.getLong("node")));
        result.setTarget(new Node(rs.getLong("end_vid")));
        return result;
    }
}
