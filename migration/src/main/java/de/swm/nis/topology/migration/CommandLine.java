package de.swm.nis.topology.migration;

import java.util.*;

/**
 * Created by sieger.michael on 06.02.2017.
 */
public class CommandLine {

    private static final String USAGE =
            "Usage: nis_migration <host> <user> <password> <schemas> [--verify] [--pgrouting] [--topology-layer]";

    private static final String PGROUTING = "--pgrouting";
    private static final String VERIFY = "--verify";
    private static final String TOPOLOGY_LAYER = "--topology-layer";

    private String host;
    private String user;
    private String password;
    private String[] schemas;
    private Set<String> switches = new HashSet<>();

    public CommandLine() {

    }

    public boolean parseArgs(String[] args) {
        if(args.length < 4) {
            System.err.println(USAGE);
            return false;
        }
        host = args[0];
        user = args[1];
        password = args[2];
        schemas = args[3].split(",");
        List<String> validFlags = Arrays.asList(VERIFY, PGROUTING, TOPOLOGY_LAYER);
        for(int i = 4; i < args.length; i++) {
            String flag = args[i];
            if(!validFlags.contains(flag)) {
                System.err.println(flag + " is not a valid argument");
                return false;
            }
            switches.add(flag);
        }
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

    private boolean getSwitch(String name) {
        return switches.contains(name);
    }

    public boolean verify() {
        return getSwitch(VERIFY);
    }

    public boolean pgrouting() {
        return getSwitch(PGROUTING);
    }

    public boolean topologyLayer() {
        return getSwitch(TOPOLOGY_LAYER);
    }
}
