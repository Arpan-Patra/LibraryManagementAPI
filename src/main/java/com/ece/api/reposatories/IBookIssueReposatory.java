package com.ece.api.reposatories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ece.api.entities.BookIssue;

@Repository
public interface IBookIssueReposatory extends JpaRepository<BookIssue, Integer> {

    List<BookIssue> findByStudentStudentId(int studentId);
    List<BookIssue> findByStatusAndDueDateBefore(String status, LocalDate date);
    long countByStudentStudentIdAndStatus(int studentId, String status);
    boolean existsByBookBookId(int bookId);
    boolean existsByStudentStudentId(int studentId);
}
