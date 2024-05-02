package ru.edu.springdata.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.edu.springdata.model.Book;
import ru.edu.springdata.repository.BookRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;



    public Book getBook(long id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getBooksByLanguage(String language) {
        return bookRepository.findBooksByLanguage(language);
    }

    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findBooksByCategory(category);
    }

    public List<Book> getBooksByLanguageAndCategory(String language, String category) {
        return bookRepository.findBooksByLanguageAndCategory(language, category);
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public void update(Book book) {
        bookRepository.save(book);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
    }

    public long getRecordCount() {
        return bookRepository.count();
    }
}
