package de.swm.nis.topology.server.benchmark.jmh;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Node;
import org.openjdk.jmh.annotations.*;

@State(Scope.Thread)
public class Neighbor {

    private NodeService service;

    public Neighbor() {
        service = BenchmarkApplication.instance.getBean(NodeService.class);
    }

    @Benchmark
    public void testNeighbor() {
        service.getNeighbors("wa", new Node(6238317), NodeService.ExpandBehavior.NEVER);
    }

}
