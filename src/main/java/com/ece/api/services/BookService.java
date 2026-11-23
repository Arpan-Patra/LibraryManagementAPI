package com.ece.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ece.api.entities.Book;
import com.ece.api.exceptions.DuplicateResourceException;
import com.ece.api.exceptions.ResourceInUseException;
import com.ece.api.exceptions.ResourceNotFoundException;
import com.ece.api.reposatories.IBookIssueReposatory;
import com.ece.api.reposatories.IBookReposatory;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookService implements IBookService {

    //add the dependencies
    @Autowired
    IBookReposatory bookReposatory;

    @Autowired
    IBookIssueReposatory bookIssueReposatory;

    @Override
    public Book createBook(Book book) {
        //check whether the ISBN is already used
        if (bookReposatory.existsByIsbn(book.getIsbn().trim())) {
            throw new DuplicateResourceException("A book with this ISBN already exists");
        }

        checkCopies(book);
        book.setIsbn(book.getIsbn().trim());
        return bookReposatory.save(book);
    }

    @Override
    public Book updateBook(int bookId, Book book) {
        //check whether the book exists
        Book oldBook = bookReposatory.findById(bookId).orElse(null);
        if (oldBook == null) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }

        //check whether another book is using the same ISBN
        if (bookReposatory.existsByIsbnAndBookIdNot(book.getIsbn().trim(), bookId)) {
            throw new DuplicateResourceException("A book with this ISBN already exists");
        }

        checkCopies(book);
        book.setBookId(bookId);
        book.setIsbn(book.getIsbn().trim());
        return bookReposatory.save(book);
    }

    @Override
    public boolean deleteBook(int bookId) {
        Book book = bookReposatory.findById(bookId).orElse(null);
        if (book == null) {
            throw new ResourceNotFoundException("Book not found with id: " + bookId);
        }

        //do not delete a book which is already referred to by issue records
        if (bookIssueReposatory.existsByBookBookId(bookId)) {
            throw new ResourceInUseException("This book has issue records and cannot be deleted");
        }

        bookReposatory.delete(book);
        return true;
    }

    @Override
    public Book getBookById(int bookId) {
        return bookReposatory.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
    }

    @Override
    public List<Book> getAllBooks() {
        return bookReposatory.findAll();
    }

    @Override
    public List<Book> getBooksByCategory(String category) {
        return bookReposatory.findByCategoryIgnoreCase(category);
    }

    //check that available copies should not be more than total copies
    private void checkCopies(Book book) {
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot be greater than total copies");
        }
    }
}
