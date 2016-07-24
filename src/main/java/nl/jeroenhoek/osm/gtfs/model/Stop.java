package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;
import nl.jeroenhoek.osm.gtfs.annotation.Id;
import nl.jeroenhoek.osm.gtfs.annotation.ReferenceId;

import java.math.BigDecimal;

public class Stop {
    @Id
    String id;

    String code;

    String name;

    BigDecimal latitude;

    BigDecimal longitude;

    @ReferenceId("id")
    Reference<String, Stop> parent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Reference<String, Stop> getParent() {
        return parent;
    }

    public void setParent(Reference<String, Stop> parent) {
        this.parent = parent;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Stop && ((Stop) other).getId().equals(getId());
    }

    @Override
    public String toString() {
        return getId() + ": " + getName() + " (" + getLatitude() + ", " + getLongitude() + ")";
    }
}
