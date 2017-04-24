package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BlockingService {

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private NodeService nodeService;

    public BlockedPath getBlockedPath(String network, Node startNode) {
        Set<Node> workingSet = new HashSet<>();
        Set<Node> expanded = new HashSet<>();
        Set<String> geoms = new HashSet<>();
        workingSet.add(startNode);
        while(!workingSet.isEmpty()) {
            Iterator<Node> it = workingSet.iterator();
            Node toExpand = it.next();
            it.remove();
            expanded.add(toExpand);
            Set<Edge> edges = nodeService.getNeighbors(network, toExpand, NodeService.ExpandBehavior.NEVER);
            Set<Node> newNodes = edges.stream().map(x -> x.getTarget()).collect(Collectors.toSet());
            geoms.addAll(edges.stream().map(x -> x.getGeom()).collect(Collectors.toList()));
            newNodes.removeAll(expanded);
            workingSet.addAll(newNodes);
        }
        BlockedPath block = new BlockedPath();
        block.setNodes(expanded);
        block.setBlockingNodes(nodeService.filter(network, expanded, NodeService.ExpandBehavior.NEVER));
        block.setGeometry(nodeService.collect(geoms));
        return block;
    }

}
