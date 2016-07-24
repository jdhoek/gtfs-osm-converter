package nl.jeroenhoek.osm.gtfs.actions;

import nl.jeroenhoek.osm.gtfs.Action;
import nl.jeroenhoek.osm.gtfs.CsvReader;
import nl.jeroenhoek.osm.gtfs.GtfsConverterCliOptions;
import nl.jeroenhoek.osm.gtfs.TransportModel;
import org.apache.commons.cli.CommandLine;

public class AgencyRoutesAction implements Action {
    @Override
    public String name() {
        return "agency-routes";
    }

    @Override
    public void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader) {
        csvReader.readAgencies(GtfsConverterCliOptions.agencyFilterFromOptions(arguments));
        csvReader.readRoutes(route -> route.getAgency() != null &&
                transportModel.getAgencies().containsKey(route.getAgency().getId())
        );

        transportModel.getRoutes().forEach((id, route) -> System.out.println(route));
    }
}
