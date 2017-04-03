package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class NodeService {

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private JdbcTemplate templ;

    @Transactional
    public Set<Node> getNodes(String network, RWO rwo) {
        Schema.set(templ, network);
        return new HashSet<>(templ.query("select node_id from connection where rwo_id = ? and rwo_code = ? and app_code = ?",
                new Object[]{rwo.getId(), rwo.getCode(), rwo.getApp()}, nodeMapper));
    }

}
