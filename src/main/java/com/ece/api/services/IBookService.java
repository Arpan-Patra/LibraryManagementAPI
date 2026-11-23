package com.ece.api.services;

import java.util.List;

import com.ece.api.entities.Book;

public interface IBookService {

    public Book createBook(Book book);
    public Book updateBook(int bookId, Book book);
    public boolean deleteBook(int bookId);
    public Book getBookById(int bookId);
    public List<Book> getAllBooks();
    public List<Book> getBooksByCategory(String category);
}
