package de.swm.nis.topology.server.benchmark.custom;

import de.swm.nis.topology.server.benchmark.jmh.BenchmarkApplication;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.service.BlockingService;

public class BlockedPath implements Benchmark {

    private final String network;

    private BlockingService service;

    public BlockedPath(String network) {
        this.network = network;
        service = BenchmarkApplication.instance.getBean(BlockingService.class);
    }


    @Override
    public void run(long[] args) {
        service.getBlockedPath(network, new Node(args[0]));
    }
}
