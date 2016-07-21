package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.model.Agency;
import nl.jeroenhoek.osm.gtfs.model.Route;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class CsvReader {
    private final Map<GtfsTable, Path> gtfsPaths;
    private final TransportModel transportModel;

    public CsvReader(Map<GtfsTable, Path> gtfsPaths, TransportModel transportModel) {
        this.gtfsPaths = gtfsPaths;
        this.transportModel = transportModel;
    }

    public void readAgencies(List<Predicate<Agency>> filters) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.AGENCY);
        Map<String, Agency> agencies = new HashMap<>();
        for (CSVRecord record : records) {
            Agency agency = new Agency();
            agency.setId(record.get("agency_id"));
            agency.setName(record.get("agency_name"));
            if (passesFilters(agency, filters)) {
                agencies.put(agency.getId(), agency);
            }
        }
        transportModel.setAgencies(agencies);
    }

    public void readRoutes(List<Predicate<Route>> filters) {
        Iterable<CSVRecord> records = readRecords(GtfsTable.ROUTES);
        Map<String, Route> routes = new HashMap<>();
        Map<String, Agency> agencies = transportModel.getAgencies();
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
            route.setAgency(agencies.get(record.get("agency_id")));
            if (passesFilters(route, filters)) {
                routes.put(route.getId(), route);
            }
        }
        transportModel.setRoutes(routes);
    }

    static <T> boolean passesFilters(T object, List<Predicate<T>> filters) {
        if (filters == null) return true;
        for (Predicate<T> filter : filters) {
            if (!filter.test(object)) return false;
        }
        return true;
    }

    Iterable<CSVRecord> readRecords(GtfsTable gtfsTable) {
        Path tableFile = gtfsPaths.get(gtfsTable);
        assertTableFileExists(tableFile, gtfsTable);
        Reader in;
        try {
            in = new FileReader(tableFile.toFile());
        } catch (FileNotFoundException e) {
            // Already established that file exists.
            throw new RuntimeException(e);
        }

        try {
            return CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
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
}
