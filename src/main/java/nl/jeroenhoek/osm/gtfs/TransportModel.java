package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.model.*;

import java.util.HashMap;
import java.util.Map;

public class TransportModel {
    Map<String, Agency> agencies = new HashMap<>();
    Map<String, Route> routes = new HashMap<>();
    Map<String, Stop> stops = new HashMap<>();
    Map<String, Itinerary> itineraries = new HashMap<>();
    Map<String, Trip> trips = new HashMap<>();
    Map<String, Shape> shapes = new HashMap<>();

    public TransportModel() {

    }

    public Map<String, Agency> getAgencies() {
        return agencies;
    }

    public void addAgency(Agency agency) {
        this.agencies.put(agency.getId(), agency);
    }

    public Map<String, Route> getRoutes() {
        return routes;
    }

    public void addRoute(Route route) {
        this.routes.put(route.getId(), route);
    }

    public Map<String, Stop> getStops() {
        return stops;
    }

    public void addStop(Stop stop) {
        this.stops.put(stop.getId(), stop);
    }

    public Map<String, Itinerary> getItineraries() {
        return itineraries;
    }

    public void addItinerary(Itinerary itinerary) {
        this.itineraries.put(itinerary.getId(), itinerary);
    }

    public Map<String, Trip> getTrips() {
        return trips;
    }

    public void addTrip(Trip trip) {
        this.trips.put(trip.getId(), trip);
    }

    public Map<String, Shape> getShapes() {
        return shapes;
    }

    public void addShape(Shape shape) {
        this.shapes.put(shape.getId(), shape);
    }

    @Override
    public String toString() {
        return agencies.size() +  " agencies\n" +
                routes.size() + " routes\n" +
                stops.size() + " stops\n" +
                itineraries.size() + " itineraries\n" +
                shapes.size() + " shapes\n" +
                trips.size() + " trips\n";
    }
}
