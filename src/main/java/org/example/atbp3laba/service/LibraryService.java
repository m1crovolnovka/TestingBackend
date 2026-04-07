package org.example.atbp3laba.service;

import org.example.atbp3laba.entity.Book;
import org.springframework.http.ResponseEntity;

public interface LibraryService {

    double calculateFine(Book book);
    ResponseEntity<Double> getFine(Book book);
    ResponseEntity<Book> getBookById(Long bookId);
}
