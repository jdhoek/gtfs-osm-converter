package nl.jeroenhoek.osm.gtfs.model.type;

import java.math.BigDecimal;
import java.util.Objects;

public class Coordinate {
    BigDecimal latitude;
    BigDecimal longitude;

    public Coordinate(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Coordinate that = (Coordinate) other;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

    @Override
    public String toString() {
        return latitude.toString() + "," + longitude.toString();
    }
}
