package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.InvalidGeomException;
import de.swm.nis.topology.server.domain.Node;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EdgeMapper implements RowMapper<Edge> {

    @Autowired
    private Logger log;

    @Override
    public Edge mapRow(ResultSet rs, int rowNum) throws SQLException {
        Edge result = new Edge();
        result.setSource(new Node(rs.getLong("source")));
        result.setTarget(new Node(rs.getLong("target")));
        byte[] geom = rs.getBytes("geom");
        if(geom != null) {
            try {
                result.setGeomWkb(geom);
            } catch (InvalidGeomException e) {
                log.error(e);
            }
        }
        return result;
    }
}
