package de.swm.nis.topology.server.benchmark;

import de.swm.nis.topology.server.Application;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

@Configuration
@ComponentScan("de.swm.nis.topology.server")
public class BenchmarkApplication {

    public static final AnnotationConfigApplicationContext instance =
            new AnnotationConfigApplicationContext(BenchmarkApplication.class);

    public static void main(String[] args) throws IOException, RunnerException {
        Options opt = new OptionsBuilder()
                .include(Neighbor.class.getSimpleName())
                .forks(1)
                .measurementIterations(100)
                .build();

        new Runner(opt).run();
    }

}
