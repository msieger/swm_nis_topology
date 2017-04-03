package de.swm.nis.topology.server.service;

import org.springframework.jdbc.core.JdbcTemplate;

public class Schema {

    public static void set(JdbcTemplate templ, String ... schemas) {
        templ.execute(String.format("set search_path to '%s'", String.join(",", schemas)));
    }

}
