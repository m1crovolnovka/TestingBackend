package org.example.atbp3laba.service.impl;

import org.example.atbp3laba.entity.Book;
import org.example.atbp3laba.repository.BookRepository;
import org.example.atbp3laba.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LibraryServiceImpl implements LibraryService {
    private final BookRepository repository;
    public LibraryServiceImpl(BookRepository repository) {
        this.repository = repository;
    }
    public ResponseEntity<Book> getBookById(Long bookId) {
        return repository.findById(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    public ResponseEntity<Double> getFine(Book book) {
        return ResponseEntity.ok(calculateFine(book));
    }

    public double calculateFine(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Record not found");
        }
        if (book.isReturned()) {
            return 0;
        }
        if (book.getDaysOverdue() < 0) {
            throw new IllegalArgumentException("Days overdue cannot be negative");
        }
        if (book.getDailyRate() <= 0) {
            throw new IllegalArgumentException("Daily rate must be positive");
        }
        return book.getDaysOverdue() * book.getDailyRate();
    }
}
