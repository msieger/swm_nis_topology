package de.swm.nis.topology.server.benchmark.custom;

import com.google.common.collect.Lists;
import de.swm.nis.topology.server.benchmark.jmh.BenchmarkApplication;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.RoutingService;

public class Routing implements Benchmark{

    private RoutingService routingService;

    private final String network;

    public Routing(String network) {
        this.network = network;
        routingService = BenchmarkApplication.instance.getBean(RoutingService.class);
    }

    @Override
    public void run(long[] args) {
        routingService.route(network, new Node(args[0]), Lists.newArrayList(new Node(args[1])));
    }
}
