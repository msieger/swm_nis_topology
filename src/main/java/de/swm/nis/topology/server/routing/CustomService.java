package de.swm.nis.topology.server.routing;

import com.vividsolutions.jts.geom.Point;
import de.swm.nis.topology.server.database.LineStringParser;
import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Edge;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.service.NotLineStringException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomService implements RoutingService{

    private static final int INITIAL_CAPACITY = 1000;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private LineStringParser parser;

    @Autowired
    private Logger logger;

    private Point getLocation(String network, Node node) {
        Set<Edge> edges = nodeService.getNeighbors(network, node, NodeService.ExpandBehavior.ALWAYS);
        if(edges.size() == 0) {
            return null;
        }
        Edge edge = edges.iterator().next();
        try {
            return parser.parse(edge.getGeom()).getStartPoint();
        } catch (com.vividsolutions.jts.io.ParseException | NotLineStringException e) {
            throw new RuntimeException("Can not determine location of node", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public RoutingResult route(String network, Node from, Node to) {
        Point toLocation = getLocation(network, to);
        if(toLocation == null) {
            return null;
        }
        Map<Node, Point> locations = new HashMap<>();
        locations.put(to, toLocation);
        Comparator<CustomNode> comp = (o1, o2) -> {
            double d1 = o1.getTotal() + locations.get(o1.getNode()).distance(toLocation);
            double d2 = o2.getTotal() + locations.get(o2.getNode()).distance(toLocation);
            if(d1 < d2) {
                return -1;
            }
            return 1;
        };
        PriorityQueue<CustomNode> working = new PriorityQueue<>(INITIAL_CAPACITY, comp);
        Map<Node, CustomNode> expanded = new HashMap<>();
        working.add(new CustomNode(from, null, 0, null));
        while(!working.isEmpty()) {
            Iterator<CustomNode> it = working.iterator();
            CustomNode toExpand = it.next();
            if(toExpand.getNode().equals(to)) {
                return new RoutingResult(traceback(toExpand));
            }
            expanded.put(toExpand.getNode(), toExpand);
            it.remove();
            Set<Edge> edges = nodeService.getNeighbors(network, toExpand.getNode(), NodeService.ExpandBehavior.IF_OPEN);
            edges = edges.stream().filter(edge -> {
                if(edge.getGeom() == null) {
                    logger.warn("Edge was ignored because its geometry was null " + edge);
                    return false;
                }
                return true;
            }).collect(Collectors.toSet());
            edges.forEach(edge -> {
                try {
                    locations.put(edge.getTarget(), parser.parse(edge.getGeom()).getEndPoint());
                } catch (NotLineStringException e) {
                    e.printStackTrace();
                } catch (com.vividsolutions.jts.io.ParseException e) {
                    e.printStackTrace();
                }
            });
            edges = edges.stream().filter(x -> !expanded.containsKey(x.getTarget())).collect(Collectors.toSet());
            Set<CustomNode> newNodes = edges.stream().map(edge -> {
                try {
                    return new CustomNode(edge.getTarget(), edge, toExpand.getTotal() + parser.parse(edge.getGeom()).getLength(), toExpand);
                } catch (NotLineStringException | com.vividsolutions.jts.io.ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toSet());
            working.addAll(newNodes);
        }
        return null;
    }

    private List<Edge> traceback(CustomNode start) {
        List<Edge> result = new ArrayList<>();
        CustomNode node = start;
        while(node != null) {
            if(node.getPrevious() != null) {
                result.add(node.getShortest());
            }
            node = node.getPrevious();
        }
        Collections.reverse(result);
        return result;
    }
}
