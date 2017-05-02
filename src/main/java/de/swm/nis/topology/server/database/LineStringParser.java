package de.swm.nis.topology.server.database;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import de.swm.nis.topology.server.service.NotLineStringException;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class LineStringParser {

    private final WKTReader reader = new WKTReader();
    private final WKBReader wkbReader = new WKBReader();

    public LineString parse(byte[] bytes) throws com.vividsolutions.jts.io.ParseException, NotLineStringException {
        Geometry geom = wkbReader.read(bytes);
        if(geom.getClass() == LineString.class) {
            return (LineString) geom;
        }
        throw new NotLineStringException("The passed bytes are not a valid linestring");
    }

    public LineString parse(String text) throws NotLineStringException, com.vividsolutions.jts.io.ParseException {
        Geometry geom = reader.read(text);
        if(geom.getClass() == LineString.class) {
            return (LineString) geom;
        }
        throw new NotLineStringException("The text '" + text + "' is not a valid linestring");
    }

}
