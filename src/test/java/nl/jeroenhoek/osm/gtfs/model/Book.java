package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;
import nl.jeroenhoek.osm.gtfs.annotation.Id;
import nl.jeroenhoek.osm.gtfs.annotation.ReferenceId;

public class Book {
    @Id
    String title;
    @ReferenceId("name")
    Reference<String, Author> author;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Reference<String, Author> getAuthor() {
        return author;
    }

    public void setAuthor(Reference<String, Author> author) {
        this.author = author;
    }
}
