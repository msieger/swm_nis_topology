package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.database.RWOService;
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

    @Autowired
    private RWOService rwoService;

    public UnreachableConsumerResult blockedNode(String network, Node stopNode) {
        List<Node> candiates = nodeService.getNeighbors(network, stopNode, NodeService.ExpandBehavior.ALWAYS)
                .stream().map(edge -> edge.getTarget()).collect(Collectors.toList());
        List<Node> providedBefore = candiates.stream().filter(node ->
                providerService.findProviders(network, node).getNodes().size() > 0
        ).collect(Collectors.toList());
        List<Node> notProvidedAfter = providedBefore.stream().filter(node ->
                        providerService.findProviders(network, node, stopNode).getNodes().size() == 0
                ).collect(Collectors.toList());
        UnreachableConsumerResult result = new UnreachableConsumerResult();
        result.setBlockedPaths(notProvidedAfter.stream()
                .map(node -> blockingService.getBlockedPath(network, node, stopNode)).collect(Collectors.toList()));
        List<Node> allNodes = result.getBlockedPaths().stream()
                .flatMap(path -> path.getNodes().stream()).collect(Collectors.toList());
        result.setConsumers(rwoService.getConsumers(network, allNodes));
        return result;
    }

}
