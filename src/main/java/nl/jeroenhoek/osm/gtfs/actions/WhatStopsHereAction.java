package nl.jeroenhoek.osm.gtfs.actions;

import nl.jeroenhoek.osm.gtfs.Action;
import nl.jeroenhoek.osm.gtfs.CsvReader;
import nl.jeroenhoek.osm.gtfs.GtfsConverterCliOptions;
import nl.jeroenhoek.osm.gtfs.TransportModel;
import org.apache.commons.cli.CommandLine;

import java.util.ArrayList;
import java.util.List;

public class WhatStopsHereAction implements Action {
    @Override
    public String name() {
        return "what-stops-here";
    }

    @Override
    public void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader) {
        csvReader.readStops(GtfsConverterCliOptions.stopFilterFromOptions(arguments));
        csvReader.readStopTimes(
                stopTime -> transportModel.getStops().containsKey(stopTime.getStop().getId())
        );
        List<String> routeIds = new ArrayList<>();
        csvReader.readTrips(
                trip -> transportModel.getItineraries().containsKey(trip.getId()),
                trip -> routeIds.add(trip.getRoute().getId())
        );
        csvReader.readRoutes(route -> routeIds.contains(route.getId()));

        transportModel.getRoutes().forEach((id, route) -> System.out.println(route));
    }
}
