package ru.edu.springdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.edu.springdata.model.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findBooksByLanguage(String language);

    List<Book> findBooksByCategory(String category);

    List<Book> findBooksByLanguageAndCategory(String language, String category);
}
