package de.swm.nis.topology.server;

import de.swm.nis.topology.server.routing.GraphhopperService;
import de.swm.nis.topology.server.routing.RoutingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;

@SpringBootApplication
@ImportResource("classpath:datasource.xml")
public class Application extends SpringBootServletInitializer{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    @Primary
    public RoutingService getRoutingService() {
        return new GraphhopperService();
    }

    @Bean
    public Logger getLogger() {
        return Logger.getLogger("topology_server");
    }
}
