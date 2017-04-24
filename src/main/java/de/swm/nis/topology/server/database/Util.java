package de.swm.nis.topology.server.database;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Util {

    public static String pgArray(Collection<String> elements) {
        return "{" + String.join(",", elements) + "}";
    }

}
