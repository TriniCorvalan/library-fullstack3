package com.example.library.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.library.model.Book;
import com.example.library.service.BookService;

import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(BooksController.class)
class BooksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private BookService bookService;

    private Book book;

    @BeforeEach
    void setBook() {
        book = new Book(1L, "Cien años de soledad", "Gabriel García Márquez", "Realismo mágico", 1967);
    }

    @Test
    void getAllBooksReturnsList() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Cien años de soledad"));
    }

    @Test
    void getBookByIdWhenFoundReturnsOk() throws Exception {
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Cien años de soledad"));
    }

    @Test
    void getBookByIdWhenMissingReturnsNotFound() throws Exception {
        when(bookService.getBookById(99L)).thenReturn(null);

        mockMvc.perform(get("/books/99"))
                .andExpect(status().isNotFound());

        verify(bookService).getBookById(99L);
    }

    @Test
    void createBookReturnsCreatedResource() throws Exception {
        when(bookService.createBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.author").value("Gabriel García Márquez"));

        verify(bookService).createBook(any(Book.class));
    }

    @Test
    void updateBookWhenExistsReturnsOk() throws Exception {
        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(book);

        mockMvc.perform(put("/books/1")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(book)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookService).updateBook(eq(1L), any(Book.class));
    }

    @Test
    void updateBookWhenMissingReturnsNotFoundBody() throws Exception {
        when(bookService.updateBook(eq(1L), any(Book.class)))
                .thenThrow(new RuntimeException("Book not found"));

        mockMvc.perform(put("/books/1")
                        .contentType(APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(book)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found"))
                .andExpect(jsonPath("$.message").value("Book not found"));
    }

    @Test
    void deleteBookWhenExistsReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteBook(1L);
    }

    @Test
    void deleteBookWhenMissingReturnsNotFound() throws Exception {
        doThrow(new RuntimeException("Book not found")).when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not found"))
                .andExpect(jsonPath("$.message").value("Book not found"));
    }
}
