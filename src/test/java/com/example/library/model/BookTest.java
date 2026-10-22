package com.example.library.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BookTest {
    @Test
    void testGettersAndSetters() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Cien años de soledad");
        book.setAuthor("Gabriel García Márquez");
        book.setGenre("Realismo mágico");
        book.setYear(1967);
        assertEquals(1L, book.getId());
        assertEquals("Cien años de soledad", book.getTitle());
        assertEquals("Gabriel García Márquez", book.getAuthor());
        assertEquals("Realismo mágico", book.getGenre());
        assertEquals(1967, book.getYear());
    }
}
