package de.swm.nis.topology.server.benchmark.custom;

import com.google.common.collect.Lists;
import de.swm.nis.topology.server.benchmark.jmh.BenchmarkApplication;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.RoutingService;

public class ShortestPath implements Benchmark{

    private de.swm.nis.topology.server.service.ShortestPath shortestPath;

    private final String network;

    public ShortestPath(String network) {
        this.network = network;
        shortestPath = BenchmarkApplication.instance.getBean(de.swm.nis.topology.server.service.ShortestPath.class);
    }

    @Override
    public void run(long[] args) {
        shortestPath.find(network, new Node(args[0]), new Node(args[1]));
    }
}
