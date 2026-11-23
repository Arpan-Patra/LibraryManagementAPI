package com.ece.api.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ece.api.entities.Book;
import com.ece.api.entities.BookIssue;
import com.ece.api.entities.Student;
import com.ece.api.exceptions.BookAlreadyReturnedException;
import com.ece.api.exceptions.MaximumActiveIssuesException;
import com.ece.api.exceptions.NoAvailableCopiesException;
import com.ece.api.exceptions.ResourceNotFoundException;
import com.ece.api.reposatories.IBookIssueReposatory;
import com.ece.api.reposatories.IBookReposatory;
import com.ece.api.reposatories.IStudentReposatory;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookIssueService implements IBookIssueService {

    //maximum number of active books allowed for one student
    private static final int MAX_ACTIVE_ISSUES = 3;

    //default borrowing period is 14 days
    private static final int BORROWING_DAYS = 14;

    //status values used in the issue table
    private static final String ISSUED = "ISSUED";
    private static final String RETURNED = "RETURNED";

    //add the dependencies
    @Autowired
    IBookIssueReposatory bookIssueReposatory;

    @Autowired
    IBookReposatory bookReposatory;

    @Autowired
    IStudentReposatory studentReposatory;

    @Override
    public BookIssue issueBook(int studentId, int bookId) {
        //first check whether the student exists
        Student student = studentReposatory.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        //then check whether the book exists
        Book book = bookReposatory.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        //check the number of books currently issued to this student
        long activeIssues = bookIssueReposatory.countByStudentStudentIdAndStatus(studentId, ISSUED);
        if (activeIssues >= MAX_ACTIVE_ISSUES) {
            throw new MaximumActiveIssuesException("A student can have a maximum of three active book issues");
        }

        //check whether any copy is available
        if (book.getAvailableCopies() <= 0) {
            throw new NoAvailableCopiesException("No copies of this book are currently available");
        }

        //create a new issue record
        BookIssue bookIssue = new BookIssue();
        bookIssue.setStudent(student);
        bookIssue.setBook(book);
        bookIssue.setIssueDate(LocalDate.now());
        bookIssue.setDueDate(LocalDate.now().plusDays(BORROWING_DAYS));
        bookIssue.setReturnDate(null);
        bookIssue.setStatus(ISSUED);

        //decrease the available copy count by one
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookReposatory.save(book);

        return bookIssueReposatory.save(bookIssue);
    }

    @Override
    public List<BookIssue> getAllIssues() {
        return bookIssueReposatory.findAll();
    }

    @Override
    public List<BookIssue> getIssuesByStudent(int studentId) {
        //check whether the student exists before searching
        if (!studentReposatory.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return bookIssueReposatory.findByStudentStudentId(studentId);
    }

    @Override
    public BookIssue returnBook(int issueId) {
        //find the issue record
        BookIssue bookIssue = bookIssueReposatory.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        //check whether this book has already been returned
        if (RETURNED.equalsIgnoreCase(bookIssue.getStatus()) || bookIssue.getReturnDate() != null) {
            throw new BookAlreadyReturnedException("This book has already been returned");
        }

        //update the issue record
        bookIssue.setReturnDate(LocalDate.now());
        bookIssue.setStatus(RETURNED);

        //increase the available copy count by one
        Book book = bookIssue.getBook();
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
        }
        bookReposatory.save(book);

        return bookIssueReposatory.save(bookIssue);
    }

    @Override
    public List<BookIssue> getOverdueIssues() {
        //overdue means due date has passed and status is still ISSUED
        return bookIssueReposatory.findByStatusAndDueDateBefore(ISSUED, LocalDate.now());
    }
}
