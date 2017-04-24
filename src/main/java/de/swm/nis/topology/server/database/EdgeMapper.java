package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EdgeMapper implements RowMapper<Edge> {
    @Override
    public Edge mapRow(ResultSet rs, int rowNum) throws SQLException {
        Edge result = new Edge();
        result.setSource(new Node(rs.getLong("source")));
        result.setTarget(new Node(rs.getLong("target")));
        result.setGeom(rs.getString("geom"));
        result.setLength(rs.getDouble("length"));
        return result;
    }
}
