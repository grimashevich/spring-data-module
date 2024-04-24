package ru.edu.springdata.repository;

import ru.edu.springdata.model.Book;

import java.util.List;

public interface BookRepository extends Repository<Book, Long> {
    List<Book> findByLanguage(String language);

    List<Book> findByCategory(String category);

    List<Book> findByLanguageAndCategory(String language, String category);
}
