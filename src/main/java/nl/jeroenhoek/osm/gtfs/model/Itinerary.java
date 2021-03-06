package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;
import nl.jeroenhoek.osm.gtfs.annotation.Id;
import nl.jeroenhoek.osm.gtfs.annotation.ReferenceId;

import java.util.ArrayList;
import java.util.List;

public class Itinerary {
    @Id
    String id;

    List<StopTime> stopTimes;

    @ReferenceId("id")
    Reference<String, Trip> trip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<StopTime> getStopTimes() {
        return stopTimes;
    }

    public void setStopTimes(List<StopTime> stopTimes) {
        this.stopTimes = stopTimes;
    }

    public void addStopTime(StopTime stopTime) {
        if (stopTimes == null) stopTimes = new ArrayList<>();
        int i;
        for (i = 0; i < stopTimes.size(); i++) {
            if (stopTimes.get(i).getSequence() > stopTime.getSequence()) {
                break;
            }
        }
        stopTimes.add(i, stopTime);
    }

    public Reference<String, Trip> getTrip() {
        return trip;
    }

    public void setTrip(Reference<String, Trip> trip) {
        this.trip = trip;
    }

    public boolean hasSameRoute(Itinerary other) {
        List<StopTime> myStopTimes = getStopTimes();
        List<StopTime> otherStopTimes = other.getStopTimes();

        if (myStopTimes == null || otherStopTimes == null) return false;
        if (myStopTimes.size() != otherStopTimes.size()) return false;

        for (int i = 0; i < myStopTimes.size(); i++) {
            Reference<String, Stop> myStop = myStopTimes.get(i).getStop();
            Reference<String, Stop> otherStop = otherStopTimes.get(i).getStop();

            if (!myStop.equals(otherStop)) return false;
        }
        return true;
    }
}
