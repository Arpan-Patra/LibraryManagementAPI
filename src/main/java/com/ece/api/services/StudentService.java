package com.ece.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ece.api.entities.Student;
import com.ece.api.exceptions.DuplicateResourceException;
import com.ece.api.exceptions.ResourceInUseException;
import com.ece.api.exceptions.ResourceNotFoundException;
import com.ece.api.reposatories.IBookIssueReposatory;
import com.ece.api.reposatories.IStudentReposatory;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentService implements IStudentService {

    //add the dependencies
    @Autowired
    IStudentReposatory studentReposatory;

    @Autowired
    IBookIssueReposatory bookIssueReposatory;

    @Override
    public Student createStudent(Student student) {
        //check whether the email is already registered
        if (studentReposatory.existsByEmailIgnoreCase(student.getEmail().trim())) {
            throw new DuplicateResourceException("A student with this email already exists");
        }

        student.setEmail(student.getEmail().trim());
        return studentReposatory.save(student);
    }

    @Override
    public Student updateStudent(int studentId, Student student) {
        //check whether the student exists
        Student oldStudent = studentReposatory.findById(studentId).orElse(null);
        if (oldStudent == null) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        //check whether another student is using the same email
        if (studentReposatory.existsByEmailIgnoreCaseAndStudentIdNot(student.getEmail().trim(), studentId)) {
            throw new DuplicateResourceException("A student with this email already exists");
        }

        student.setStudentId(studentId);
        student.setEmail(student.getEmail().trim());
        return studentReposatory.save(student);
    }

    @Override
    public boolean deleteStudent(int studentId) {
        Student student = studentReposatory.findById(studentId).orElse(null);
        if (student == null) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        //do not delete a student who has issue records
        if (bookIssueReposatory.existsByStudentStudentId(studentId)) {
            throw new ResourceInUseException("This student has issue records and cannot be deleted");
        }

        studentReposatory.delete(student);
        return true;
    }

    @Override
    public Student getStudentById(int studentId) {
        return studentReposatory.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentReposatory.findAll();
    }
}
