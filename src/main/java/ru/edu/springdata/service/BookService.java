package ru.edu.springdata.service;

import org.springframework.stereotype.Service;
import ru.edu.springdata.model.Book;
import ru.edu.springdata.repository.BookRepository;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book getBook(long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByLanguage(String language) {
        return bookRepository.findByLanguage(language);
    }

    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    public List<Book> getBooksByLanguageAndCategory(String language, String category) {
        return bookRepository.findByLanguageAndCategory(language, category);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void update(Book book) {
        bookRepository.update(book);
    }

    public void delete(Long id) {
        bookRepository.delete(id);
    }

    public long getRecordCount() {
        return bookRepository.getRecordCount();
    }
}
