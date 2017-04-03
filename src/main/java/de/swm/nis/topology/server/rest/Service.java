package de.swm.nis.topology.server.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Service {

    @RequestMapping("/test")
    public String test() {
        return "Hello";
    }

}
