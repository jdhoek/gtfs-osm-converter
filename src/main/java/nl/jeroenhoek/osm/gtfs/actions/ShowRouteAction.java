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
        List<String> agencyFilterList = GtfsConverterCliOptions.listFromArguments(arguments, "agencies");
        List<String> routeFilterList = GtfsConverterCliOptions.listFromArguments(arguments, "routes");

        csvReader.readAgencies(Collections.singletonList(
                agency -> {
                    if (agencyFilterList == null) return true;
                    for (String inList : agencyFilterList) {
                        if (inList.equals(agency.getId()) || inList.equals(agency.getName())) return true;
                    }
                    return false;
                }
        ));

        csvReader.readRoutes(Arrays.asList(
                route -> route.getAgency() != null,
                route -> {
                    if (routeFilterList == null) return true;
                    for (String inList : routeFilterList) {
                        if (inList.equals(route.getId())) return true;
                    }
                    return false;
                }
        ));

        transportModel.getRoutes().forEach((id, route) -> System.out.println(route));
    }
}
