package ru.edu.springdata.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.edu.springdata.model.Book;
import ru.edu.springdata.service.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Book> getBook(@PathVariable long id) {
        Book book = bookService.getBook(id);
        if (book == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(book);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Book>> getBook() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> search(@RequestParam String language, @RequestParam String category) {
        return ResponseEntity.ok(bookService.getBooksByLanguageAndCategory(language, category));
    }

    @GetMapping("/language/{language}")
    public ResponseEntity<List<Book>> searchByLanguage(@PathVariable String language) {
        return ResponseEntity.ok(bookService.getBooksByLanguage(language));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Book>> searchByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookService.getBooksByCategory(category));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        book = bookService.save(book);
        return ResponseEntity.ok(book);
    }

    @PutMapping
    public ResponseEntity<Book> updateBook(@RequestBody Book book) {
        bookService.update(book);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable long id) {
        bookService.delete(id);
        return ResponseEntity.ok().build();
    }
}
