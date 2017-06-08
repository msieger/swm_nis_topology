package de.swm.nis.topology.server.benchmark.testset;

import com.google.common.collect.Lists;
import de.swm.nis.topology.server.benchmark.jmh.BenchmarkApplication;
import de.swm.nis.topology.server.database.NodeService;
import de.swm.nis.topology.server.domain.Node;
import de.swm.nis.topology.server.routing.RoutingResult;
import de.swm.nis.topology.server.routing.RoutingService;
import de.swm.nis.topology.server.service.BlockedPath;
import de.swm.nis.topology.server.service.BlockingService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final AnnotationConfigApplicationContext app = BenchmarkApplication.instance;

    public static void main(String[] args) throws FileNotFoundException {
        //shortestPath("wa");
        //shortestPath("fw");
        //shortestPath("ss");
        //shortestPath("ga");
        enclosedLeakage("ga");
    }

    private static void enclosedLeakage(String network) throws FileNotFoundException {
        BlockingService service = app.getBean(BlockingService.class);
        NodeService nodeService = app.getBean(de.swm.nis.topology.server.database.NodeService.class);
        List<long[]> result = new ArrayList<>();
        List<Node> nodes = new ArrayList<>(nodeService.getNodes(network));
        Random rnd = new Random();
        while(result.size() < 100) {
            Node node = nodes.get(rnd.nextInt(nodes.size()));
            BlockedPath blockedPath = service.getBlockedPath(network, node);
            if(blockedPath.getNodes().size() < 100) {
                result.add(new long[]{node.getId(), blockedPath.getNodes().size()});
            }
        }
        write(network, result, 1, "_enclosedLeakage");
    }

    private static void shortestPath(String network) throws FileNotFoundException {
        RoutingService routing = app.getBean(RoutingService.class);
        NodeService nodeService = app.getBean(NodeService.class);
        List<Node> nodes = new ArrayList<>(nodeService.getNodes(network));
        Random rnd = new Random();
        List<long[]> result = new ArrayList<>();
        while(result.size() < 100) {
            Node from = nodes.get(rnd.nextInt(nodes.size()));
            Node to = nodes.get(rnd.nextInt(nodes.size()));
            List<RoutingResult> path = routing.route(network, from, Lists.newArrayList(to));
            if(path.get(0).found() && path.get(0).getNodes().size() < 100) {
                result.add(new long[]{ from.getId(), to.getId(), path.get(0).getNodes().size()});
            }
        }
        write(network, result, 2, "_pairs");
    }

    private static void write(String network, List<long[]> data, int sortIndex, String suffix) throws FileNotFoundException {
        data.sort((x, y) -> (int)(x[sortIndex] - y[sortIndex]));
        try(PrintStream str = new PrintStream(network + suffix + ".txt")) {
            for(long[] line : data) {
                for(int i = 0; i < line.length; i++) {
                    str.print(line[i]);
                    if(i != line.length - 1) {
                        str.print(",");
                    }
                }
                str.print("\r\n");
            }
        }
    }
}
