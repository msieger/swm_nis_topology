package de.swm.nis.topology.server.benchmark;

import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Node;
import org.openjdk.jmh.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

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
