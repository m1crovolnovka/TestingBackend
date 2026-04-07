package org.example.atbp3laba.service;

import org.example.atbp3laba.entity.Book;
import org.example.atbp3laba.repository.BookRepository;
import org.example.atbp3laba.service.impl.LibraryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryServiceImplTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private LibraryServiceImpl libraryService;

    private Book validBook;

    @BeforeEach
    void setUp() {
        validBook = new Book();
        validBook.setId(1L);
        validBook.setDailyRate(10.0);
        validBook.setDaysOverdue(5);
        validBook.setReturned(false);
    }

    @Test
    @DisplayName("Позитивный сценарий: разные тарифы для разных bookId")
    void testDifferentRatesForDifferentBooks() {
        Book expensiveBook = new Book();
        expensiveBook.setDailyRate(100.0);

        Book cheapBook = new Book();
        cheapBook.setDailyRate(5.0);

        when(repository.findById(101L)).thenReturn(Optional.of(expensiveBook));
        when(repository.findById(102L)).thenReturn(Optional.of(cheapBook));

        ResponseEntity<Book> response1 = libraryService.getBookById(101L);
        ResponseEntity<Book> response2 = libraryService.getBookById(102L);

        assertEquals(100.0, response1.getBody().getDailyRate());
        assertEquals(5.0, response2.getBody().getDailyRate());
    }

    @Test
    @DisplayName("Имитация задержки ответа и успешного выполнения")
    void testGetBookWithDelaySimulation() {
        when(repository.findById(1L)).thenAnswer(invocation -> {
            Thread.sleep(100);
            return Optional.of(validBook);
        });

        ResponseEntity<Book> response = libraryService.getBookById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Имитация пустых данных (запись не найдена)")
    void testGetBookNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Book> response = libraryService.getBookById(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Имитация падения сервера (ошибка БД)")
    void testDatabaseError() {
        when(repository.findById(anyLong())).thenThrow(new RuntimeException("DB Connection Crash"));

        assertThrows(RuntimeException.class, () -> libraryService.getBookById(1L));
    }


    @Test
    @DisplayName("Сложный сценарий: штраф 0, если книга уже возвращена")
    void testCalculateFineWhenReturned() {
        validBook.setReturned(true);
        validBook.setDaysOverdue(100);

        double fine = libraryService.calculateFine(validBook);
        assertEquals(0, fine, "Штраф должен быть 0 для возвращенной книги");
    }

    @Test
    @DisplayName("Граничное значение: просрочка 0 дней")
    void testCalculateFineZeroDays() {
        validBook.setDaysOverdue(0);
        double fine = libraryService.calculateFine(validBook);
        assertEquals(0, fine);
    }

    @Test
    @DisplayName("Негативный сценарий: отрицательные дни")
    void testCalculateFineNegativeDays() {
        validBook.setDaysOverdue(-1);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                libraryService.calculateFine(validBook)
        );
        assertEquals("Days overdue cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Граничное значение: тариф 0.01 (минимально положительный)")
    void testCalculateFineMinimalRate() {
        validBook.setDailyRate(0.01);
        validBook.setDaysOverdue(10);
        assertEquals(0.1, libraryService.calculateFine(validBook), 0.001);
    }

    @Test
    @DisplayName("Негативный сценарий: нулевой или отрицательный тариф")
    void testCalculateFineInvalidRate() {
        validBook.setDailyRate(0);
        assertThrows(IllegalArgumentException.class, () -> libraryService.calculateFine(validBook));

        validBook.setDailyRate(-5.0);
        assertThrows(IllegalArgumentException.class, () -> libraryService.calculateFine(validBook));
    }

    @Test
    @DisplayName("Проверка взаимодействия: вызов репозитория с правильным ID")
    void testRepositoryInteraction() {
        libraryService.getBookById(55L);
        verify(repository, times(1)).findById(55L);
    }
}
