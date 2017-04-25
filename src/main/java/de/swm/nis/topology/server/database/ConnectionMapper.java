package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.Connection;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ConnectionMapper implements RowMapper<Connection> {
    @Override
    public Connection mapRow(ResultSet rs, int rowNum) throws SQLException {
        Connection con = new Connection();
        con.setNodeId(rs.getLong("node_id"));
        con.setRwoId(rs.getLong("rwo_id"));
        con.setRwoCode(rs.getInt("rwo_code"));
        con.setAppCode(rs.getInt("app_code"));
        con.setPointIdx(rs.getInt("point_idx"));
        return con;
    }
}
