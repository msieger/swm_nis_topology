package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.domain.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Block;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class BlockingService {

    @Autowired
    private JdbcTemplate templ;

    @Autowired
    private NodeService nodeService;

    public BlockedPath getBlockedPath(Node startNode) {
        Set<Node> workingSet = new HashSet<>();
        Set<Node> expanded = new HashSet<>();
        workingSet.add(startNode);
        while(!workingSet.isEmpty()) {
            Iterator<Node> it = workingSet.iterator();
            Node toExpand = it.next();
            it.remove();
            expanded.add(toExpand);
            Set<Node> newNodes = nodeService.getNeighbors(toExpand, NodeService.ExpandBehavior.NEVER);
            newNodes.removeAll(expanded);
            workingSet.addAll(newNodes);
        }
        BlockedPath block = new BlockedPath();
        block.setNodes(expanded);
        block.setBlockingNodes(nodeService.filter(expanded, NodeService.ExpandBehavior.NEVER));
        return block;
    }

}
