package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;
import nl.jeroenhoek.osm.gtfs.annotation.ReferenceId;

import java.time.LocalTime;

public class StopTime {
    @ReferenceId("id")
    Reference<String, Trip> trip;
    LocalTime arrival;
    LocalTime departure;
    @ReferenceId("id")
    Reference<String, Stop> stop;
    int sequence;

    public Reference<String, Trip> getTrip() {
        return trip;
    }

    public void setTrip(Reference<String, Trip> trip) {
        this.trip = trip;
    }

    public LocalTime getArrival() {
        return arrival;
    }

    public void setArrival(LocalTime arrival) {
        this.arrival = arrival;
    }

    public LocalTime getDeparture() {
        return departure;
    }

    public void setDeparture(LocalTime departure) {
        this.departure = departure;
    }

    public Reference<String, Stop> getStop() {
        return stop;
    }

    public void setStop(Reference<String, Stop> stop) {
        this.stop = stop;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
