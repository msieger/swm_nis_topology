package de.swm.nis.topology.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sieger.michael on 06.02.2017.
 */
public class SqlScriptExecutor {

    private final Connection con;

    public SqlScriptExecutor(Connection con) {
        this.con = con;
    }

    public void run(String sql) throws SQLException {
        con.nativeSQL(sql);
    }

    private static String readSQL(String filename) throws IOException {
        return String.join("\r\n", Files.readAllLines(Paths.get(filename)));
    }

    public void execute(String filename) throws SQLException, IOException {
        execute(new ArrayList<>(), filename);
    }

    public void execute(List<String> schemas, String filename) throws IOException, SQLException {
        String sql = readSQL(filename);
        try(Statement stmt = con.createStatement()) {
            if(schemas.size() > 0) {
                stmt.execute("set search_path to " + schemas.stream().reduce((a, b) -> a + "," + b).get());
            }
            stmt.execute(sql);
        }
    }
}
