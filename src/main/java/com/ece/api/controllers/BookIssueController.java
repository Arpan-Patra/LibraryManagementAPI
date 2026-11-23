package com.ece.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ece.api.entities.BookIssue;
import com.ece.api.services.IBookIssueService;

@RestController
@RequestMapping("/api/issues")
public class BookIssueController {

    //add the dependency
    @Autowired
    IBookIssueService bookIssueService;

    //issue a book to a student
    @PostMapping("/{studentId}/{bookId}")
    public ResponseEntity<BookIssue> issueBook(@PathVariable int studentId, @PathVariable int bookId) {
        BookIssue bookIssue = bookIssueService.issueBook(studentId, bookId);
        return new ResponseEntity<>(bookIssue, HttpStatus.CREATED);
    }

    //get all issue records
    @GetMapping
    public List<BookIssue> getAllIssues() {
        return bookIssueService.getAllIssues();
    }

    //get issue records of one student
    @GetMapping("/student/{studentId}")
    public List<BookIssue> getIssuesByStudent(@PathVariable int studentId) {
        return bookIssueService.getIssuesByStudent(studentId);
    }

    //return a book
    @PutMapping("/return/{issueId}")
    public BookIssue returnBook(@PathVariable int issueId) {
        return bookIssueService.returnBook(issueId);
    }

    //get books whose due date has passed and are not returned
    @GetMapping("/overdue")
    public List<BookIssue> getOverdueIssues() {
        return bookIssueService.getOverdueIssues();
    }
}
