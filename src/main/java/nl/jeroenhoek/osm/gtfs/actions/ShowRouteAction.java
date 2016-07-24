package nl.jeroenhoek.osm.gtfs.actions;

import nl.jeroenhoek.osm.gtfs.Action;
import nl.jeroenhoek.osm.gtfs.CsvReader;
import nl.jeroenhoek.osm.gtfs.GtfsConverterCliOptions;
import nl.jeroenhoek.osm.gtfs.TransportModel;
import org.apache.commons.cli.CommandLine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShowRouteAction implements Action {
    @Override
    public String name() {
        return "show-route";
    }

    @Override
    public void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader) {
        csvReader.readAgencies(GtfsConverterCliOptions.agencyFilterFromOptions(arguments));
        csvReader.readRoutes(GtfsConverterCliOptions.routeFilterFromOptions(arguments));

        transportModel.getRoutes().forEach((id, route) -> System.out.println(route));
    }
}
