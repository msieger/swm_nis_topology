package de.swm.nis.topology.server.rest;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;
import de.swm.nis.topology.server.service.*;
import de.swm.nis.topology.server.database.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RequestMapping("/{network}")
@RestController
public class Service {

    @Autowired
    private NodeService nodeService;

    @Autowired
    private BlockingService blockingService;

    @Autowired
    private ShortestPath routingService;

    @Autowired
    private ReachableProviderService providerService;

    @Autowired
    private UnreachableConsumerService consumerService;

    @RequestMapping("/route")
    public Path getRoute(
             @PathVariable String network,
             @RequestParam("from") long from_id,
             @RequestParam("to") long to_id
    ) {
        return routingService.find(network, new Node(from_id), new Node(to_id));
    }

    @RequestMapping("/node")
    public List<Node> unreachableConsumer(
            @PathVariable String network,
            @RequestParam("unreachable-when-blocked") long nodeId
    ) {
        return consumerService.blockedNode(network, new Node(nodeId));
    }

    @RequestMapping(value="/node", params={"provides"})
    public List<Node> providedBy(
            @PathVariable String network,
            @RequestParam("provides") long nodeId
    ) {
        return providerService.findProviders(network, new Node(nodeId));
    }

    @RequestMapping(value="/node", params={"rwo_id", "rwo_code", "app_code"})
    public Set<Node> getNodes(
            @PathVariable String network,
            @RequestParam("rwo_id") long rwoId,
            @RequestParam("rwo_code") int rwoCode,
            @RequestParam("app_code") int appCode) {
        return nodeService.getNodes(network, new RWO(rwoId, rwoCode, appCode));
    }

    @RequestMapping(value="/node", params={"rwo_name", "geom_name", "x", "y"})
    public Node findNode(
            @PathVariable String network,
            @RequestParam("rwo_name") String rwoName,
            @RequestParam("geom_name") String geomName,
            @RequestParam("x") double x,
            @RequestParam("y") double y
    ) {
        return nodeService.findNode(network, rwoName, geomName, x, y, 31468);
    }

    @RequestMapping("/blocked_path")
    public BlockedPath getBlockedPath(
            @PathVariable String network,
            @RequestParam("node_id") long nodeId
    ) {
        return blockingService.getBlockedPath(network, new Node(nodeId));
    }

}
