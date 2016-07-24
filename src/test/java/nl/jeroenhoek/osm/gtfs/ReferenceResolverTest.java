package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.model.Author;
import nl.jeroenhoek.osm.gtfs.model.Book;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ReferenceResolverTest {
    @Test
    public void resolveTest() {
        Map<String, Book> left = new HashMap<>();
        Map<String, Author> right = new HashMap<>();

        Author author = new Author();
        author.setName("Richard Castle");

        Book redVelvet = new Book();
        redVelvet.setTitle("Red Velvet");
        redVelvet.setAuthor(Reference.byId("Richard Castle"));

        Book theBodyInTheDumpster = new Book();
        theBodyInTheDumpster.setTitle("The Body in the Dumpster");
        theBodyInTheDumpster.setAuthor(Reference.byId("Richard Castle"));

        left.put(redVelvet.getTitle(), redVelvet);
        left.put(theBodyInTheDumpster.getTitle(), theBodyInTheDumpster);
        right.put(author.getName(), author);

        ReferenceResolver referenceResolver = new ReferenceResolver(RecordKnowledgeBase.INSTANCE);
        referenceResolver.resolveBidirectionalReferences(left, right);
    }

}