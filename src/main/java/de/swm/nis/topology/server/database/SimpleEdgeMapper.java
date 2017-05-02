package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.SimpleEdge;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SimpleEdgeMapper implements RowMapper<SimpleEdge> {


    @Override
    public SimpleEdge mapRow(ResultSet rs, int rowNum) throws SQLException {
        SimpleEdge edge = new SimpleEdge();
        edge.setSource(new Node(rs.getLong("source")));
        edge.setTarget(new Node(rs.getLong("target")));
        edge.setDistance(rs.getDouble("length"));
        return null;
    }
}
