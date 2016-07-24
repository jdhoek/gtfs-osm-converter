package nl.jeroenhoek.osm.gtfs.actions;

import nl.jeroenhoek.osm.gtfs.Action;
import nl.jeroenhoek.osm.gtfs.CsvReader;
import nl.jeroenhoek.osm.gtfs.GtfsConverterCliOptions;
import nl.jeroenhoek.osm.gtfs.TransportModel;
import nl.jeroenhoek.osm.gtfs.model.Shape;
import nl.jeroenhoek.osm.gtfs.model.type.Coordinate;
import nl.jeroenhoek.osm.gtfs.osm.OSMDrawing;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RoutesInAreaAction implements Action {
    @Override
    public String name() {
        return "routes-in-area";
    }

    @Override
    public void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader) {
        csvReader.readAgencies(GtfsConverterCliOptions.agencyFilterFromOptions(arguments));
        csvReader.readStops(GtfsConverterCliOptions.stopFilterFromOptions(arguments));
        csvReader.readStopTimes(
                stopTime -> transportModel.getStops().containsKey(stopTime.getStop().getId())
        );

        // Scan the trips to get the route-ids that stop at the stops gathered above.
        Set<String> routeIds = new HashSet<>();
        csvReader.readTrips(
                trip -> transportModel.getItineraries().containsKey(trip.getId()),
                trip -> routeIds.add(trip.getRoute().getId())
        );

        csvReader.readRoutes(route -> routeIds.contains(route.getId()) &&
                transportModel.getAgencies().containsKey(route.getAgency().getId()));

        // Clear the trips, and get the full trip data for each route now that we know which routes we want.
        transportModel.getTrips().clear();
        csvReader.readTrips(trip -> transportModel.getRoutes().containsKey(trip.getRoute().getId()));

        // Same goes for the itineraries; clear the subset containing only the stops requested, and get the data
        // for the full routes.
        transportModel.getItineraries().clear();
        csvReader.readStopTimes(stopTime -> transportModel.getTrips().containsKey(stopTime.getTrip().getId()));

        Set<String> shapedIds = transportModel.getTrips().values().stream()
                .map(trip -> trip.getShape().getId())
                .collect(Collectors.toSet());
        csvReader.readShapes(shapePart -> shapedIds.contains(shapePart.getId()));

        Map<List<String>, String> uniqueRoutes = new HashMap<>();
        Set<String> allStopsConcerned = new HashSet<>();
        transportModel.getItineraries().values().forEach(itinerary -> {
            List<String> list = itinerary.getStopTimes().stream()
                    .map(stopTime -> stopTime.getStop().getId())
                    .collect(Collectors.toList());
            uniqueRoutes.put(list, itinerary.getTrip().getId());
            allStopsConcerned.addAll(list);
        });

        System.out.println(transportModel);

        csvReader.readStops(stop -> allStopsConcerned.contains(stop.getId()));

        Map<String, Set<String>> tripShapeMap = new HashMap<>();
        transportModel.getTrips().forEach((id, trip) -> {
            String shapeId = trip.getShape().getId();
            if (shapeId != null) {
                if (tripShapeMap.containsKey(shapeId)) {
                    tripShapeMap.get(shapeId).add(id);
                } else {
                    Set<String> ids = new HashSet<>();
                    ids.add(id);
                    tripShapeMap.put(shapeId, ids);
                }
            }
        });

        Map<List<Coordinate>, Set<String>> uniqueShapes = new HashMap<>();
        for (Shape shape : transportModel.getShapes().values()) {
            List<Coordinate> coordinates = shape.getCoordinates();
            if (!uniqueShapes.containsKey(coordinates)) {
                Set<String> tripIds = new HashSet<>();
                tripShapeMap.get(shape.getId()).forEach(tripIds::add);
                uniqueShapes.put(coordinates, tripIds);
            } else {
                Set<String> tripIds = new HashSet<>();
                tripShapeMap.get(shape.getId()).forEach(tripIds::add);
                uniqueShapes.get(coordinates).addAll(tripIds);
            }
        }

        uniqueShapes.forEach((k, v) -> System.out.println(k.size() + " (" + v.size() + ")"));

        if (arguments.hasOption("output")) {
            File file = new File(arguments.getOptionValue("output"));
            OSMDrawing osmDrawing = new OSMDrawing();
            osmDrawing.addStops(transportModel.getStops().values());
            osmDrawing.addShapes(uniqueShapes);
            osmDrawing.save(file);
        }
    }
}
