package nl.jeroenhoek.osm.gtfs.actions;

import nl.jeroenhoek.osm.gtfs.Action;
import nl.jeroenhoek.osm.gtfs.CsvReader;
import nl.jeroenhoek.osm.gtfs.GtfsConverterCliOptions;
import nl.jeroenhoek.osm.gtfs.TransportModel;
import org.apache.commons.cli.CommandLine;

import java.math.BigDecimal;

public class StopAnalysisAction implements Action {
    @Override
    public String name() {
        return "stops";
    }

    @Override
    public void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader) {
        csvReader.readStops(GtfsConverterCliOptions.stopFilterFromOptions(arguments));

        BoundingBox boundingBox = new BoundingBox();

        transportModel.getStops().forEach((id, stop) -> boundingBox.add(stop.getLatitude(), stop.getLongitude()));

        System.out.println(boundingBox);

        transportModel.getStops().forEach((id, stop) -> System.out.println(stop));

        System.out.println();
        System.out.println("---");
        System.out.println();
        System.out.println(transportModel.getStops().size());
    }

    public class BoundingBox {
        BigDecimal minLongitude = null;
        BigDecimal maxLongitude = null;
        BigDecimal minLatitude = null;
        BigDecimal maxLatitude = null;


        public void add(BigDecimal latitude, BigDecimal longitude) {
            if (minLongitude == null) {
                // Initialize.
                minLatitude = maxLatitude = latitude;
                minLongitude = maxLongitude = longitude;
            } else {
                minLatitude = minLatitude.min(latitude);
                maxLatitude = maxLatitude.max(latitude);
                minLongitude = minLongitude.min(longitude);
                maxLongitude = maxLongitude.max(longitude);
            }
        }

        @Override
        public String toString() {
            return "lat: " + minLatitude + " - " + maxLatitude + "\n" +
                    "lon: " + minLongitude + " - " + maxLongitude;
        }
    }
}
