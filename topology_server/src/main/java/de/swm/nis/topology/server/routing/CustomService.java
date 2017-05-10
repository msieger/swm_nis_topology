package de.swm.nis.topology.server.routing;

import com.vividsolutions.jts.geom.Point;
import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.InvalidGeomException;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.service.NotLineStringException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomService implements RoutingService{

    @Autowired
    private NodeService nodeService;

    @Autowired
    private Logger logger;

    @Transactional(readOnly = true)
    public List<RoutingResult> route(String network, Node from, List<Node> to, Node ignore) {
        CustomAStar astar = new CustomAStar(nodeService, logger);
        return to.stream().map(node -> {
            return astar.run(network, from, node, ignore);
        }).collect(Collectors.toList());
    }

}
