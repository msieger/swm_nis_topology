package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UnreachableConsumerService {

    @Autowired
    private ReachableProviderService providerService;

    @Autowired
    private BlockingService blockingService;

    @Autowired
    private NodeService nodeService;

    public List<BlockedPath> blockedNode(String network, Node stopNode) {
        List<Node> candiates = nodeService.getNeighbors(network, stopNode, NodeService.ExpandBehavior.ALWAYS)
                .stream().map(edge -> edge.getTarget()).collect(Collectors.toList());
        List<Node> providedBefore = candiates.stream().filter(node ->
                providerService.findProviders(network, node).size() > 0
        ).collect(Collectors.toList());
        List<Node> notProvidedAfter = providedBefore.stream().filter(node ->
                        providerService.findProviders(network, node, stopNode).size() == 0
                ).collect(Collectors.toList());
        return notProvidedAfter.stream()
                .map(node -> blockingService.getBlockedPath(network, node, stopNode)).collect(Collectors.toList());
    }

}
