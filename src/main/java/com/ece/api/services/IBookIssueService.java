package com.ece.api.services;

import java.util.List;

import com.ece.api.entities.BookIssue;

public interface IBookIssueService {

    public BookIssue issueBook(int studentId, int bookId);
    public List<BookIssue> getAllIssues();
    public List<BookIssue> getIssuesByStudent(int studentId);
    public BookIssue returnBook(int issueId);
    public List<BookIssue> getOverdueIssues();
}
