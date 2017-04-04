package de.swm.nis.topology.server.database;

import java.util.List;

public class Util {

    public static String pgArray(Iterable<String> elements) {
        return "array[" + String.join(",", elements) + "]";
    }

}
