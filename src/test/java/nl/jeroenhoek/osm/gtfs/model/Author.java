package nl.jeroenhoek.osm.gtfs.model;

import nl.jeroenhoek.osm.gtfs.Reference;
import nl.jeroenhoek.osm.gtfs.annotation.Id;
import nl.jeroenhoek.osm.gtfs.annotation.ReferenceId;

import java.util.Map;

public class Author {
    @Id
    String name;
    @ReferenceId("title")
    Map<String, Reference<String, Book>> books;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Reference<String, Book>> getBooks() {
        return books;
    }

    public void setBooks(Map<String, Reference<String, Book>> books) {
        this.books = books;
    }
}
