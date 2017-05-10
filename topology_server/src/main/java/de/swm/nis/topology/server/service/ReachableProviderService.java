package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.database.RWOService;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReachableProviderService {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoutingService routingService;

    @Autowired
    private RWOService rwoService;

    public ReachableProviderResult findProviders(String network, Node node) {
        return findProviders(network, node, null);
    }

    public ReachableProviderResult findProviders(String network, Node node, Node ignore) {
        List<Node> allProviders = nodeService.providers(network);
        ReachableProviderResult result = new ReachableProviderResult();
        result.setNodes(routingService.route(network, node, allProviders, ignore).stream()
                .filter(route -> route.found()).map(route -> route.getEnd()).collect(Collectors.toList()));
        result.setProviders(rwoService.getProducers(network, result.getNodes()));
        return result;
    }

}
