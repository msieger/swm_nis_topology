package de.swm.nis.topology.server.rest;

import com.google.common.collect.Lists;
import de.swm.nis.topology.server.database.RWOService;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;
import de.swm.nis.topology.server.service.*;
import de.swm.nis.topology.server.database.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    private RWOService rwoService;

    @RequestMapping("/route")
    public Path getRoute(
             @PathVariable String network,
             @RequestParam("from") long from_id,
             @RequestParam("to") long to_id
    ) {
        return routingService.find(network, new Node(from_id), new Node(to_id));
    }

    @RequestMapping("/node")
    public UnreachableConsumerResult unreachableConsumer(
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

    @RequestMapping(value="/consumer")
    public List<RWO> getConsumerByNode(
            @PathVariable String network,
            @RequestParam("by_node") String nodeIds
    ) {

        List<Node> nodes =
                Arrays.stream(nodeIds.split(",")).map(id -> new Node(Long.parseLong(id))).collect(Collectors.toList());
        return rwoService.getConsumers(network, nodes);
    }

    @RequestMapping(value="/producer")
    public List<RWO> getProducerByNode(
            @PathVariable String network,
            @RequestParam("by_node") String nodeIds
    ) {

        List<Node> nodes =
                Arrays.stream(nodeIds.split(",")).map(id -> new Node(Long.parseLong(id))).collect(Collectors.toList());
        return rwoService.getProducers(network, nodes);
    }

    @RequestMapping(value="/node", params={"rwo_id", "type", "app_code"})
    public Set<Node> getNodes(
            @PathVariable String network,
            @RequestParam("rwo_id") long rwoId,
            @RequestParam("type") String type,
            @RequestParam("field") String field) {
        return nodeService.getNodes(network, new RWO(rwoId, type, field));
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
