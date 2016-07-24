package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;

public class Trip {
    Route route;
    String id;
    String serviceId;
    String realtimeTripId;
    Reference<String, Shape> shape;
}
