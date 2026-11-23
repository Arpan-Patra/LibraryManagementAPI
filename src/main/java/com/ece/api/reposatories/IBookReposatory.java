package com.ece.api.reposatories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ece.api.entities.Book;

@Repository
public interface IBookReposatory extends JpaRepository<Book, Integer> {

    //check whether the ISBN is already present
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndBookIdNot(String isbn, int bookId);

    //search books by category
    List<Book> findByCategoryIgnoreCase(String category);
}
