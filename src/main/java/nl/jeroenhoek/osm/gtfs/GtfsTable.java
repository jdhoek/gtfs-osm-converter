package nl.jeroenhoek.osm.gtfs;

import java.nio.file.Path;
import java.util.Optional;

public enum GtfsTable {
    AGENCY,
    STOPS,
    ROUTES,
    TRIPS,
    STOP_TIMES,
    CALENDAR,
    CALENDAR_DATES,
    FARE_ATTRIBUTES,
    FARE_RULES,
    SHAPES,
    FREQUENCIES,
    TRANSFERS,
    FEED_INFO;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public String fileName() {
        return name().toLowerCase() + ".txt";
    }

    public static Optional<GtfsTable> fromPath(Path path) {
        Path file = path.getFileName();
        for (GtfsTable table : values()) {
            if (table.fileName().equals(file.toString())) {
                return Optional.of(table);
            }
        }
        return Optional.empty();
    }
}
