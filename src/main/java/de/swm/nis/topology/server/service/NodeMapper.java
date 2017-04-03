package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class NodeMapper implements RowMapper<Node> {

    @Override
    public Node mapRow(ResultSet resultSet, int rwoNum) throws SQLException {
        long nodeId = resultSet.getLong("node_id");
        return new Node(nodeId);
    }
}
