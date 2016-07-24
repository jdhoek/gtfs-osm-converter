package nl.jeroenhoek.osm.gtfs.actions;

import nl.jeroenhoek.osm.gtfs.Action;
import nl.jeroenhoek.osm.gtfs.CsvReader;
import nl.jeroenhoek.osm.gtfs.GtfsConverterCliOptions;
import nl.jeroenhoek.osm.gtfs.TransportModel;
import nl.jeroenhoek.osm.gtfs.model.Agency;
import nl.jeroenhoek.osm.gtfs.model.Route;
import org.apache.commons.cli.CommandLine;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class AgencyRoutesAction implements Action {
    @Override
    public String name() {
        return "agency-routes";
    }

    @Override
    public void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader) {
        csvReader.readAgencies(GtfsConverterCliOptions.agencyFilterFromOptions(arguments));

        Predicate<Route> nullAgencyFilter = route -> route.getAgency() != null;
        csvReader.readRoutes(Collections.singletonList(nullAgencyFilter));

        transportModel.getRoutes().forEach((id, route) -> {
            System.out.println(route);
        });
    }
}
