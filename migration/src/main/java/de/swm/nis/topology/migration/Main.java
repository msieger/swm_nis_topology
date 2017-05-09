package de.swm.nis.topology.migration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sieger.michael on 06.02.2017.
 */
public class Main {

    private static final Logger log = LogManager.getLogger("migration");


    public static void migrate(Connection con, String schema, boolean verify, boolean layer, boolean pgrouting) throws IOException, SQLException {
        List<String> schemas = new ArrayList<>();
        schemas.add(schema);
        schemas.add("public");
        SqlScriptExecutor mig = new SqlScriptExecutor(con);
        log.info(String.format("Migrating data in schema %s", schema));
        con.setSchema(schema);
        mig.execute("node.sql");
        log.info("Inserted nodes");
        mig.execute(schemas, "connection.sql");
        log.info("Inserted connections");
        mig.execute("connectivity_information.sql");
        log.info("Created connectivity_information");
        mig.execute("provider.sql");
        log.info("Created provider");
        mig.execute("consumer.sql");
        log.info("Created consumer");
        mig.execute("definition.sql");
        log.info("Inserted definitions");
        mig.execute("geom_attribute.sql");
        log.info("Inserted geom_attributes");
        mig.execute(schemas, "function.sql");
        log.info("Created functions");
        con.commit();
        if(layer) {
            mig.execute("layer.sql");
            log.info("Created viewable topology layer");
        }
        if(verify) {
            mig.execute(schemas, "verification.sql");
            log.info("Created verification data");
        }
        if(pgrouting) {
            mig.execute(schemas, "pgrouting.sql");
            log.info("Created pgRouting tables");
        }
        String fname = schema + ".sql";
        try {
            mig.execute(fname);
        }catch(NoSuchFileException e) {
            log.warn("There is no network dependent SQL file. Was looking for '" + fname + "'.");
        }
    }

    public static void main(String[] args){

        CommandLine cmd = new CommandLine();
        if(!cmd.parseArgs(args)) {
            return;
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL Driver is missing");
        }
        Connection con = null;
        try{
            String url = String.format("jdbc:postgresql://%s/%s", cmd.getHost(), cmd.getDatabase());
            con = DriverManager.getConnection(url, cmd.getUser(), cmd.getPassword());
            con.setAutoCommit(false);
            con.setClientInfo("ApplicationName", "NIS Topology Migration");
            for(String schema : cmd.getSchemas()) {
                migrate(con, schema, cmd.verify(), cmd.topologyLayer(), cmd.pgrouting());
                con.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            if(con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
