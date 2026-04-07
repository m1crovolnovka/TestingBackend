package org.example.atbp3laba.repository;

import org.example.atbp3laba.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book,Long> {
}
