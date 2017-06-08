package de.swm.nis.topology.server.benchmark.custom;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class BenchmarkRunner {

    private final Benchmark target;
    private final Path out;

    private long[][] args;

    public BenchmarkRunner(Benchmark target, Path in, int numValues, Path out) throws IOException {
        this.target = target;
        this.out = out;
        List<String> lines = Files.readAllLines(in);
        args = new long[lines.size()][numValues];
        for(int i = 0; i < lines.size(); i++){
            String line = lines.get(i);
            String[] values = line.split(",");
            if(values.length != numValues) {
                throw new RuntimeException("values.length != numValues");
            }
            for(int j = 0; j < values.length; j++) {
                args[i][j] = Long.parseLong(values[j]);
            }
        }
    }

    public void run() throws FileNotFoundException {
        long start = System.currentTimeMillis();
        try(PrintStream str = new PrintStream(new FileOutputStream(out.toFile()))) {
            for(int i = 0; i < 20; i++) {
                target.run(args[i % args.length]);
            }
            for(int i = 0; i < args.length; i++) {
                double avg = 0;
                int repeats = 10;
                for(int j = 0; j < repeats; j++) {
                    long time = System.currentTimeMillis();
                    target.run(args[i]);
                    avg += (System.currentTimeMillis() - time) / (double)repeats;
                    if(System.currentTimeMillis() > start + 1000 * 60 * 10) {
                        System.out.println("10 minutes passed");
                        return;
                    }
                }
                for(int j = 0; j < args[0].length; j++) {
                    str.print(args[i][j]);
                    str.print(",");
                }
                str.print(avg);
                str.print("\r\n");
            }
        }

    }



}
