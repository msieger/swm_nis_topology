package de.swm.nis.topology.server.database;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RWOService {

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private RWOMapper rwoMapper;

    @Transactional
    public List<RWO> getConsumers(String network, List<Node> node) {
        Schema.set(templ, network);
        String sql =
                " select distinct con.rwo_id, def.table_name typ, attr.column_name field" +
                " from connection con" +
                " join definition def on con.rwo_code = def.rwo_code" +
                " join geom_attribute attr on con.rwo_code = attr.rwo_code and con.app_code = attr.app_code" +
                " join consumer_information inf on con.rwo_code = inf.rwo_code" +
                " where con.node_id = ANY(?::int8[])";
        return templ.query(sql,
                new Object[]{
                        Util.pgArray(node.stream().map(n -> Long.toString(n.getId())).collect(Collectors.toList()))
                }, rwoMapper);
    }

    @Transactional
    public List<RWO> getProducers(String network, List<Node> nodes) {
        Schema.set(templ, network);
        String sql =
                " select distinct con.rwo_id, def.table_name typ, attr.column_name field" +
                        " from connection con" +
                        " join definition def on con.rwo_code = def.rwo_code" +
                        " join geom_attribute attr on con.rwo_code = attr.rwo_code and con.app_code = attr.app_code" +
                        " join provider_information inf on con.rwo_code = inf.rwo_code" +
                        " where con.node_id = ANY(?::int8[])";
        return templ.query(sql,
                new Object[]{
                        Util.pgArray(nodes.stream().map(n -> Long.toString(n.getId())).collect(Collectors.toList()))
                }, rwoMapper);
    }

}
