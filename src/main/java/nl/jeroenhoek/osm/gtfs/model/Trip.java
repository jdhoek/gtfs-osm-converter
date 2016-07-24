package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;
import nl.jeroenhoek.osm.gtfs.annotation.Id;
import nl.jeroenhoek.osm.gtfs.annotation.ReferenceId;

public class Trip {
    @ReferenceId("id")
    Reference<String, Route> route;

    @Id
    String id;

    String serviceId;

    String realtimeTripId;

    @ReferenceId("id")
    Reference<String, Itinerary> itinerary;

    @ReferenceId("id")
    Reference<String, Shape> shape;

    public Reference<String, Route> getRoute() {
        return route;
    }

    public void setRoute(Reference<String, Route> route) {
        this.route = route;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getRealtimeTripId() {
        return realtimeTripId;
    }

    public void setRealtimeTripId(String realtimeTripId) {
        this.realtimeTripId = realtimeTripId;
    }

    public Reference<String, Itinerary> getItinerary() {
        return itinerary;
    }

    public void setItinerary(Reference<String, Itinerary> itinerary) {
        this.itinerary = itinerary;
    }

    public Reference<String, Shape> getShape() {
        return shape;
    }

    public void setShape(Reference<String, Shape> shape) {
        this.shape = shape;
    }
}
