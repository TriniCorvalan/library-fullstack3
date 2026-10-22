package com.example.library.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookServiceImpl service;

    private Book book;

    @BeforeEach
    void setBook() {
        book = new Book(1L, "Cien años de soledad", "Gabriel García Márquez", "Realismo mágico", 1967);
    }

    @Test
    void testGetAllBooks() {
        List<Book> expected = Arrays.asList(book);
        when(repository.findAll()).thenReturn(expected);
        assertEquals(expected, service.getAllBooks());
    }

    @Test
    void testGetBookById() {
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        assertEquals(book, service.getBookById(1L));
    }

    @Test
    void testCreateBook() {
        when(repository.save(book)).thenReturn(book);
        assertEquals(book, service.createBook(book));
    }

    @Test
    void testUpdateBookExists() {
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(book)).thenReturn(book);
        Book result = service.updateBook(1L, book);
        assertEquals(1L, book.getId());
        assertEquals(book, result);
        verify(repository).save(book);
    }

    @Test
    void testUpdateBookNotExists() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.updateBook(1L, book));
        verify(repository, never()).save(any());
    }

    @Test
    void testDeleteBook() {
        when(repository.existsById(1L)).thenReturn(true);
        service.deleteBook(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteBookNotExists() {
        when(repository.existsById(1L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> service.deleteBook(1L));
        verify(repository, never()).deleteById(1L);
    }
}
