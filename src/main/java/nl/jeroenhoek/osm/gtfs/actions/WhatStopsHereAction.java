package nl.jeroenhoek.osm.gtfs.actions;

import nl.jeroenhoek.osm.gtfs.Action;
import nl.jeroenhoek.osm.gtfs.CsvReader;
import nl.jeroenhoek.osm.gtfs.GtfsConverterCliOptions;
import nl.jeroenhoek.osm.gtfs.TransportModel;
import nl.jeroenhoek.osm.gtfs.model.Route;
import nl.jeroenhoek.osm.gtfs.model.Trip;
import org.apache.commons.cli.CommandLine;

import java.util.*;
import java.util.stream.Collectors;

public class WhatStopsHereAction implements Action {
    @Override
    public String name() {
        return "what-stops-here";
    }

    @Override
    public void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader) {
        csvReader.readAgencies(GtfsConverterCliOptions.agencyFilterFromOptions(arguments));
        csvReader.readStops(GtfsConverterCliOptions.stopFilterFromOptions(arguments));
        csvReader.readStopTimes(
                stopTime -> transportModel.getStops().containsKey(stopTime.getStop().getId())
        );
        Set<String> routeIds = new HashSet<>();
        csvReader.readTrips(
                trip -> transportModel.getItineraries().containsKey(trip.getId()),
                trip -> routeIds.add(trip.getRoute().getId())
        );
        csvReader.readRoutes(route -> routeIds.contains(route.getId()) &&
                transportModel.getAgencies().containsKey(route.getAgency().getId()));
        transportModel.getTrips().clear();

        csvReader.readTrips(trip -> transportModel.getRoutes().containsKey(trip.getRoute().getId()));

        transportModel.getItineraries().clear();
        csvReader.readStopTimes(stopTime -> transportModel.getTrips().containsKey(stopTime.getTrip().getId()));

        Map<List<String>, String> uniqueRoutes = new HashMap<>();
        Set<String> allStopsConcerned = new HashSet<>();
        transportModel.getItineraries().values().forEach(itinerary -> {
            List<String> list = itinerary.getStopTimes().stream()
                    .map(stopTime -> stopTime.getStop().getId())
                    .collect(Collectors.toList());
            uniqueRoutes.put(list, itinerary.getTrip().getId());
            allStopsConcerned.addAll(list);
        });

        csvReader.readStops(stop -> allStopsConcerned.contains(stop.getId()));


        Map<Integer, Integer> count = new HashMap<>();

        transportModel.getItineraries().forEach((id, itinerary) -> {
            int size = itinerary.getStopTimes().size();
            count.put(size, count.getOrDefault(size, 0) + 1);
        });
        System.out.println();
        count.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println();
        System.out.println(transportModel);

        transportModel.getRoutes().forEach((id, route) -> System.out.println(route));

        System.out.println();
        for (Map.Entry<List<String>, String> entry : uniqueRoutes.entrySet()) {
            Trip trip = transportModel.getTrips().get(entry.getValue());
            Route route = transportModel.getRoutes().get(trip.getRoute().getId());
            System.out.println("Route " + route);
            for (String stopId : entry.getKey()) {
                System.out.println("  " + transportModel.getStops().get(stopId));
            }
        }
    }
}
