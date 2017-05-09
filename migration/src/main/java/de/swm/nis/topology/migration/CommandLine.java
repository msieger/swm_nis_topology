package de.swm.nis.topology.migration;

import java.util.Scanner;

/**
 * Created by sieger.michael on 06.02.2017.
 */
public class CommandLine {

    private String host;
    private String user;
    private String password;
    private String[] schemas;

    public CommandLine() {

    }

    public boolean parseArgs(String[] args) {
        if(args.length != 4) {
            System.err.println(String.format("Usage: nis_migration <host> <user> <password> <schemas>"));
            return false;
        }
        host = args[0];
        user = args[1];
        password = args[2];
        schemas = args[3].split(",");
        return true;
    }


    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String[] getSchemas() {
        return schemas;
    }

    public String getPassword() {
        return password;
    }
}
