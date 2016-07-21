package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.model.Agency;
import nl.jeroenhoek.osm.gtfs.model.Route;

import java.util.Map;

public class TransportModel {
    Map<String, Agency> agencies;
    Map<String, Route> routes;

    public TransportModel() {

    }

    public Map<String, Agency> getAgencies() {
        return agencies;
    }

    public void setAgencies(Map<String, Agency> agencies) {
        this.agencies = agencies;
    }

    public Map<String, Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<String, Route> routes) {
        this.routes = routes;
    }
}
