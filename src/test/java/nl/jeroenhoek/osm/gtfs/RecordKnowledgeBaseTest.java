package nl.jeroenhoek.osm.gtfs;

import nl.jeroenhoek.osm.gtfs.RecordKnowledgeBase.ClassDescriptor;
import nl.jeroenhoek.osm.gtfs.model.Book;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RecordKnowledgeBaseTest {
    @Test
    public void inspectTest() throws Throwable {
        Book book = new Book();
        book.setTitle("The little book of test");

        RecordKnowledgeBase recordKnowledgeBase = RecordKnowledgeBase.INSTANCE;
        ClassDescriptor<Book> descriptor = recordKnowledgeBase.inspect(Book.class);

        Object retVal = descriptor.getId(book);

        assertThat(retVal, is("The little book of test"));

    }

}