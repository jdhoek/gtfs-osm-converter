package nl.jeroenhoek.osm.gtfs;

import java.nio.file.Path;
import java.util.Optional;

public enum GtfsTable {
    AGENCY(10),
    STOPS(10_000),
    ROUTES(1_000),
    TRIPS(100_000),
    STOP_TIMES(1_000_000),
    CALENDAR(null),
    CALENDAR_DATES(null),
    FARE_ATTRIBUTES(null),
    FARE_RULES(null),
    SHAPES(1_000_000),
    FREQUENCIES(null),
    TRANSFERS(null),
    FEED_INFO(null);

    private final Integer progressInterval;

    GtfsTable(Integer progressInterval) {
        this.progressInterval = progressInterval;
    }

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

    public int progressInterval() {
        return progressInterval == null ? 100_000 : progressInterval;
    }
}
