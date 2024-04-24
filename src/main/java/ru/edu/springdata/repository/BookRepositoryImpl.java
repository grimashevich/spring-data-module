package ru.edu.springdata.repository;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.edu.springdata.model.Book;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class BookRepositoryImpl implements BookRepository {
    private final JdbcTemplate template;

    public BookRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<Book> findByLanguage(String language) {
        return template.query("SELECT * FROM books WHERE language = ?", new BeanPropertyRowMapper<>(Book.class),
                language);
    }

    @Override
    public List<Book> findByCategory(String category) {
        return template.query("SELECT * FROM books WHERE category = ?", new BeanPropertyRowMapper<>(Book.class),
                category);
    }

    @Override
    public List<Book> findByLanguageAndCategory(String language, String category) {
        return template.query("SELECT * FROM books WHERE language = ? AND category = ?",
                new BeanPropertyRowMapper<>(Book.class), language, category);
    }

    @Override
    public List<Book> findAll() {
        return template.query("SELECT * FROM books", new BeanPropertyRowMapper<>(Book.class));
    }

    @Override
    public Optional<Book> findById(Long id) {
        List<Book> books = template.query("SELECT * FROM books WHERE id = ?",
                new BeanPropertyRowMapper<>(Book.class), id);
        if (books.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(books.get(0));
    }

    @Override
    public Book save(Book book) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String insertSQL = "INSERT INTO books(name, language, category) VALUES (?, ?, ?)";
        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(insertSQL, new String[]{"id"});
            ps.setString(1, book.getName());
            ps.setString(2, book.getLanguage());
            ps.setString(3, book.getCategory());
            return ps;
        }, keyHolder);
        book.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return book;
    }

    @Override
    public void update(Book book) {
        template.update("UPDATE books SET name = ?, category = ?, language = ? WHERE id = ?",
                book.getName(), book.getCategory(), book.getLanguage(), Objects.requireNonNull(book.getId()));
    }

    @Override
    public void delete(Long id) {
        template.update("DELETE FROM books WHERE id = ?", id);
    }

    public long getRecordCount() {
        return Objects.requireNonNull(template.queryForObject("SELECT COUNT(*) FROM books", Long.class));
    }
}
