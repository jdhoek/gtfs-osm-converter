package nl.jeroenhoek.osm.gtfs.model;

public class Agency {
    String id;
    String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Agency && ((Agency) other).getId().equals(getId());
    }

    @Override
    public String toString() {
        return getId() + ": " + getName();
    }
}
