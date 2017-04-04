package de.swm.nis.topology.server.rest;

import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.domain.RWO;
import de.swm.nis.topology.server.service.BlockedPath;
import de.swm.nis.topology.server.service.BlockingService;
import de.swm.nis.topology.server.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequestMapping("/{network}")
@RestController
public class Service {

    @Autowired
    private NodeService nodeService;

    private BlockingService blockingService;

    @RequestMapping("/node")
    public Set<Node> getNodes(
            @PathVariable String network,
            @RequestParam("rwo_id") long rwoId,
            @RequestParam("rwo_code") int rwoCode,
            @RequestParam("app_code") int appCode) {
        return nodeService.getNodes(network, new RWO(rwoId, rwoCode, appCode));
    }

    @RequestMapping("/")
    public BlockedPath getBlockedPath(
            @PathVariable String network,
            @RequestParam("node_id") long nodeId
    ) {
        return blockingService.getBlockedPath(new Node(nodeId));
    }

}
