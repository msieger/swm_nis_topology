package de.swm.nis.topology.server.benchmark;

import com.google.common.collect.Lists;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.RoutingService;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.ArrayList;

@State(Scope.Thread)
public class ShortestPath {

    private de.swm.nis.topology.server.service.ShortestPath shortestPath;
    private RoutingService routingService;

    public ShortestPath() {
        shortestPath = BenchmarkApplication.instance.getBean(de.swm.nis.topology.server.service.ShortestPath.class);
        routingService = BenchmarkApplication.instance.getBean(RoutingService.class);
    }

    @Benchmark
    public void testRouting() {
        routingService.route("wa", new Node(2260932), Lists.newArrayList(new Node(46221402)));
    }

    @Benchmark
    public void testShortestPath() {
        shortestPath.find("wa", new Node(2260932), new Node(46221402));
    }

}
