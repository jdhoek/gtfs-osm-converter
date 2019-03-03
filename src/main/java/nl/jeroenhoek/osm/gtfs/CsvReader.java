package nl.jeroenhoek.osm.gtfs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import nl.jeroenhoek.osm.gtfs.model.Agency;
import nl.jeroenhoek.osm.gtfs.model.Itinerary;
import nl.jeroenhoek.osm.gtfs.model.Route;
import nl.jeroenhoek.osm.gtfs.model.Shape;
import nl.jeroenhoek.osm.gtfs.model.Stop;
import nl.jeroenhoek.osm.gtfs.model.StopTime;
import nl.jeroenhoek.osm.gtfs.model.Trip;
import nl.jeroenhoek.osm.gtfs.model.type.Coordinate;

public class CsvReader {
    private final Map<GtfsTable, Path> gtfsPaths;
    private final TransportModel transportModel;

    public CsvReader(Map<GtfsTable, Path> gtfsPaths, TransportModel transportModel) {
        this.gtfsPaths = gtfsPaths;
        this.transportModel = transportModel;
    }

    public void readAgencies(Predicate<Agency> filter) {
        readAgencies(filter, transportModel::addAgency);
    }

    public void readAgencies(Predicate<Agency> filter, Consumer<Agency> action) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.AGENCY);
        for (CSVRecord record : records) {
            Agency agency = new Agency();
            agency.setId(record.get("agency_id"));
            agency.setName(record.get("agency_name"));

            if (filter.test(agency)) {
                action.accept(agency);
            }
        }
    }

    public void readRoutes(Predicate<Route> filter) {
        readRoutes(filter, transportModel::addRoute);
    }

    public void readRoutes(Predicate<Route> filter, Consumer<Route> action) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.ROUTES);
        for (CSVRecord record : records) {
            Route route = new Route();
            String id = record.get("route_id");
            if (id == null) {
                System.err.println("Route without id found.");
                continue;
            }
            route.setId(id);
            route.setShortName(record.get("route_short_name"));
            route.setLongName(record.get("route_long_name"));
            route.setAgency(Reference.byId(record.get("agency_id")));

            if (filter.test(route)) {
                action.accept(route);
            }
        }
    }

    public void readStops(Predicate<Stop> filter) {
        readStops(filter, transportModel::addStop);
    }

    public void readStops(Predicate<Stop> filter, Consumer<Stop> action) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.STOPS);
        for (CSVRecord record : records) {
            Stop stop = new Stop();
            stop.setId(record.get("stop_id"));
            stop.setCode(record.get("stop_code"));
            stop.setName(record.get("stop_name"));
            stop.setLatitude(new BigDecimal(record.get("stop_lat")));
            stop.setLongitude(new BigDecimal(record.get("stop_lon")));
            if (record.isMapped("parent_station")) {
            	stop.setParent(Reference.byId(record.get("parent_station")));
            }
            if (filter.test(stop)) {
                action.accept(stop);
            }
        }
    }

    public void readStopTimes(Predicate<StopTime> filter) {
        readStopTimes(filter, stopTime -> {
            String tripId = stopTime.getTrip().getId();
            Itinerary itinerary = transportModel.getItineraries().get(tripId);
            if (itinerary == null) {
                itinerary = new Itinerary();
                itinerary.setId(tripId);
                itinerary.setTrip(Reference.byId(tripId));
                transportModel.addItinerary(itinerary);
            }
            itinerary.addStopTime(stopTime);
        });
    }

    public void readStopTimes(Predicate<StopTime> filter, Consumer<StopTime> action) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.STOP_TIMES);
        for (CSVRecord record : records) {
            String tripId = record.get("trip_id");
            if (tripId == null) {
                System.err.println("Stop time without trip-id found.");
                continue;
            }
            String stopId = record.get("stop_id");
            if (stopId == null) {
                System.err.println("Stop time without stop-id found.");
                continue;
            }

            StopTime stopTime = new StopTime();
            stopTime.setTrip(Reference.byId(tripId));
            stopTime.setStop(Reference.byId(stopId));

            if (filter.test(stopTime)) {
                action.accept(stopTime);
            }
        }
    }

    public void readTrips(Predicate<Trip> filter) {
        readTrips(filter, transportModel::addTrip);
    }

    public void readTrips(Predicate<Trip> filter, Consumer<Trip> action) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.TRIPS);
        for (CSVRecord record : records) {
            String tripId = record.get("trip_id");
            if (tripId == null) {
                System.err.println("Stop time without trip-id found.");
                continue;
            }

            Trip trip = new Trip();
            trip.setId(tripId);
            trip.setRoute(Reference.byId(record.get("route_id")));
            trip.setShape(Reference.byId(record.get("shape_id")));

            if (filter.test(trip)) {
                action.accept(trip);
            }
        }
    }

    public void readShapes(Predicate<Shape.ShapePart> filter) {
        readShapes(filter, shapePart -> {
            String shapeId = shapePart.getId();
            Shape shape = transportModel.getShapes().get(shapeId);
            if (shape == null) {
                shape = new Shape();
                shape.setId(shapeId);
                transportModel.addShape(shape);
            }
            shape.addShapePart(shapePart);
        });
    }

    public void readShapes(Predicate<Shape.ShapePart> filter, Consumer<Shape.ShapePart> action) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.SHAPES);
        for (CSVRecord record : records) {
            Shape.ShapePart shapePart = new Shape.ShapePart();
            shapePart.setId(record.get("shape_id"));
            BigDecimal lat = new BigDecimal(record.get("shape_pt_lat"));
            BigDecimal lon = new BigDecimal(record.get("shape_pt_lon"));
            shapePart.setCoordinate(new Coordinate(lat, lon));
            shapePart.setSequence(Integer.valueOf(record.get("shape_pt_sequence")));
            if (filter.test(shapePart)) {
                action.accept(shapePart);
            }
        }
    }


    Iterable<CSVRecord> readRecords(GtfsTable gtfsTable) {
        System.out.println("-- Reading " + gtfsTable + " --");
        Path tableFile = gtfsPaths.get(gtfsTable);
        assertTableFileExists(tableFile, gtfsTable);
        Reader in;
        try {
            in = new InputStreamReader(new BOMInputStream(new FileInputStream(tableFile.toFile())));
        } catch (FileNotFoundException e) {
            // Already established that file exists.
            throw new RuntimeException(e);
        }

        try {
            return new ProgressPrintingIterable<>(
                    CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in), gtfsTable.progressInterval()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void assertTableFileExists(Path tableFile, GtfsTable gtfsTable) {
        if (tableFile == null) {
            String message = "No %s table found. Is there a %s in the directory passed?";
            throw new RuntimeException(String.format(message, gtfsTable, gtfsTable.fileName()));
        }
    }

    /**
     * Wrapper around an {@link Iterable} (and the iterator it returns) that prints progress information at an interval.
     *
     * @param <T> Type.
     */
    public static class ProgressPrintingIterable<T> implements Iterable<T> {
        final Iterable<T> wrapped;
        final int interval;

        public ProgressPrintingIterable(Iterable<T> iterable, int interval) {
            wrapped = iterable;
            this.interval = interval;
        }

        @Override
        public Iterator<T> iterator() {
            return new ProgressPrintingIterator<>(wrapped.iterator(), interval);
        }
    }

    /**
     * Wrapper around an {@link Iterator} that prints progress information at an interval.
     *
     * @param <T> Type.
     */
    public static class ProgressPrintingIterator<T> implements Iterator<T> {
        int count = 0;
        final Iterator<T> wrapped;
        final int interval;

        public ProgressPrintingIterator(Iterator<T> iterator, int interval) {
            wrapped = iterator;
            this.interval = interval;
        }

        @Override
        public boolean hasNext() {
            return wrapped.hasNext();
        }

        @Override
        public T next() {
            count++;
            if (count % interval == 0) {
                System.out.println(count + " records parsed");
            }
            return wrapped.next();
        }
    }
}
