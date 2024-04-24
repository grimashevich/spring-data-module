package ru.edu.springdata;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.edu.springdata.model.Book;
import ru.edu.springdata.service.BookService;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookService bookService;

    private final static String API_URL = "/api/books";
    private final static String DROP_TABLE_IF_EXISTS_SQL = "DROP TABLE IF EXISTS books";
    private final static String CREATE_TABLE_SQL = """
                                    CREATE TABLE books
                                    (
                                        id       BIGSERIAL PRIMARY KEY,
                                        name     VARCHAR(255) NOT NULL,
                                        language VARCHAR(64)  NOT NULL,
                                        category VARCHAR(64)
                                    )
            """;
    private final static String INSERT_TEST_DATA_SQL = """
                                    INSERT INTO books (name, language, category)
                                    VALUES ('Война и мир', 'Русский', 'Роман'),
                                           ('Евгений Онегин', 'Русский', 'Поэзия'),
                                           ('Мастер и Маргарита', 'Русский', 'Роман'),
                                           ('The Great Gatsby', 'Английский', 'Новелла'),
                                           ('Don Quixote', 'Испанский', 'Новелла'),
                                           ('The Little Prince', 'Французский', 'Детская литература')
            """;


    @BeforeEach
    void setup() {
        jdbcTemplate.update(DROP_TABLE_IF_EXISTS_SQL);
        jdbcTemplate.update(CREATE_TABLE_SQL);
        jdbcTemplate.update(INSERT_TEST_DATA_SQL);
    }

    @Test
    void getByIdTest() throws Exception {
        mockMvc.perform(get(API_URL + "/get/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Война и мир"))
                .andExpect(jsonPath("$.language").value("Русский"))
                .andExpect(jsonPath("$.category").value("Роман"));

        mockMvc.perform(get(API_URL + "/get/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBooksTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(API_URL + "/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)))
                .andReturn();
        List<Book> books = toBookList(mvcResult);
        Book book = books.stream().filter(b -> b.getId() == 6).findFirst().orElse(null);
        Assertions.assertNotNull(book);
        Assertions.assertEquals("The Little Prince", book.getName());
    }

    @Test
    void searchByCategoryTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(API_URL + "/category/Роман")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
        List<Book> books = toBookList(mvcResult);
        Book book = books.stream().filter(b -> b.getId() == 3).findFirst().orElse(null);
        Assertions.assertNotNull(book);
        Assertions.assertEquals("Мастер и Маргарита", book.getName());
    }

    @Test
    void searchByLanguageTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(API_URL + "/language/Английский")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andReturn();
        List<Book> books = toBookList(mvcResult);
        Book book = books.get(0);
        Assertions.assertNotNull(book);
        Assertions.assertEquals("The Great Gatsby", book.getName());
    }

    @Test
    void searchTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(API_URL + "/search?language=Русский&category=Роман")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn();
        List<Book> books = toBookList(mvcResult);
        Book book = books.stream().filter(b -> b.getId() == 1).findFirst().orElse(null);
        Assertions.assertNotNull(book);
        Assertions.assertEquals("Война и мир", book.getName());
    }

    @Test
    void addRecordTest() throws Exception {
        long oldRecordCount = bookService.getRecordCount();
        Book newBook = new Book();
        newBook.setName("Остров сокровищ");
        newBook.setLanguage("Русский");
        newBook.setCategory("Приключения");
        mockMvc.perform(post(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isOk());
        Assertions.assertEquals(oldRecordCount + 1, bookService.getRecordCount());
        List<Book> books = bookService.getAllBooks();
        Book book = books.stream().max(Comparator.comparingLong(Book::getId)).orElse(null);
        Assertions.assertNotNull(book);
        Assertions.assertEquals("Остров сокровищ", book.getName());
    }

    @Test
    void updateRecordTest() throws Exception {
        long oldRecordCount = bookService.getRecordCount();
        Book book = bookService.getBook(2);
        book.setName("Онегий Евгенин");
        mockMvc.perform(put(API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isOk());
        Assertions.assertEquals(oldRecordCount, bookService.getRecordCount());
        Book newBook = bookService.getBook(2);
        Assertions.assertNotNull(book);
        Assertions.assertEquals("Онегий Евгенин", newBook.getName());
    }

    @Test
    void deleteRecordTest() throws Exception {
        long oldRecordCount = bookService.getRecordCount();
        mockMvc.perform(delete(API_URL + "/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(oldRecordCount - 1, bookService.getRecordCount());
        List<Book> books = bookService.getAllBooks();
        Book book = books.stream().filter(b -> b.getId() == 5).findFirst().orElse(null);
        Assertions.assertNull(book);
    }

    private List<Book> toBookList(MvcResult mvcResult) throws Exception {
        String responseBody = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(responseBody, new TypeReference<>() {
        });
    }
}
