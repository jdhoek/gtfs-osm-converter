package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.annotation.Id;
import nl.jeroenhoek.osm.gtfs.model.type.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Shape {
    @Id
    String id;

    List<ShapePart> points;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ShapePart> getPoints() {
        return points;
    }

    public void addShapePart(ShapePart shapePart) {
        if (points == null) points = new ArrayList<>();
        int i;
        for (i = 0; i < points.size(); i++) {
            if (points.get(i).getSequence() > shapePart.getSequence()) {
                break;
            }
        }
        points.add(i, shapePart);
    }

    public boolean hasEqualShape(Shape other) {
        List<ShapePart> myPoints = getPoints();
        List<ShapePart> otherPoints = other.getPoints();
        if (myPoints == null || otherPoints == null) return false;
        if (myPoints.size() != otherPoints.size()) return false;

        for (int i = 0; i < myPoints.size(); i++) {
            ShapePart myPart = myPoints.get(i);
            ShapePart otherPart = otherPoints.get(i);
            if (!Objects.equals(myPart.getCoordinate(), otherPart.getCoordinate())) {
                return false;
            }
        }
        return true;
    }

    public List<Coordinate> getCoordinates() {
        List<Coordinate> coordinates = new ArrayList<>();
        points.forEach(shapePart -> coordinates.add(shapePart.getCoordinate()));
        return coordinates;
    }

    public static class ShapePart {
        String id;
        int sequence;
        Coordinate coordinate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getSequence() {
            return sequence;
        }

        public void setSequence(int sequence) {
            this.sequence = sequence;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }

        public void setCoordinate(Coordinate coordinate) {
            this.coordinate = coordinate;
        }
    }
}
