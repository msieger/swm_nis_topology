package de.swm.nis.topology.server.service;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.RoutingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReachableProviderService {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private RoutingService routingService;

    public Set<Node> findProviders(String network, Node node) {
        Set<Node> allProviders = nodeService.providers(network);
        return allProviders.stream().filter(provider -> {
            return routingService.route(network, node, provider) != null;
        }).collect(Collectors.toSet());
    }

}
