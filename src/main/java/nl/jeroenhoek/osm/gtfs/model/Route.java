package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;

public class Route {
    String id;
    Reference<String, Agency> agency;
    String shortName;
    String longName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Reference<String, Agency> getAgency() {
        return agency;
    }

    public void setAgency(Reference<String, Agency> agency) {
        this.agency = agency;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Route && ((Route) other).getId().equals(getId());
    }

    @Override
    public String toString() {
        String name = "";
        String agencyName = agency.isResolved() ? agency.getReferred().getName() : agency.getId();
        if (getShortName() != null && !getShortName().isEmpty()) {
            name += "(" + getShortName() + ") ";
        }
        if (getLongName() != null && !getLongName().isEmpty()) {
            name += getLongName();
        } else {
            name += "-unnamed-";
        }
        return getId() + ": " + agencyName + " -> " + name;
    }
}
