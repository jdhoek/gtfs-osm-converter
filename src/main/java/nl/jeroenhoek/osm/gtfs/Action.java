package nl.jeroenhoek.osm.gtfs;

import org.apache.commons.cli.CommandLine;

public interface Action {
    void perform(CommandLine arguments, TransportModel transportModel, CsvReader csvReader);
    String name();
}
