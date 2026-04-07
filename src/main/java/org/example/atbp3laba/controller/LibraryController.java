package org.example.atbp3laba.controller;

import org.example.atbp3laba.entity.Book;
import org.example.atbp3laba.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId) {
        return libraryService.getBookById(bookId);
    }

    @PostMapping("/calculate-fine")
    public ResponseEntity<Double> calculate(@RequestBody Book book) {
        return libraryService.getFine(book);
    }
}