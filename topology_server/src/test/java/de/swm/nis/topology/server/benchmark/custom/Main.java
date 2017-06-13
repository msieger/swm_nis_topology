package de.swm.nis.topology.server.benchmark.custom;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    private static void routing(String network) throws IOException {
        new BenchmarkRunner(new Routing(network), Paths.get(network + "_pairs.txt"), 3, Paths.get(network+"_routing.txt")).run();
    }

    private static void shortestPath(String network) throws IOException {
        new BenchmarkRunner(new ShortestPath(network), Paths.get(network + "_pairs.txt"),
                3, Paths.get(network+"_shortestPath.txt")).run();
    }

    private static void enclosedLeakage(String network) throws IOException {
        new BenchmarkRunner(new BlockedPath(network), Paths.get(network+"_enclosedLeakage.txt"),
                2, Paths.get(network+"_enclosedLeakageResult.txt")).run();
    }

    public static void main(String[] args) throws IOException {
        //routing("wa");
        //routing("fw");
        //routing("ga");
        //routing("ss");
        shortestPath("wa");
        //enclosedLeakage("ga");
    }

}
