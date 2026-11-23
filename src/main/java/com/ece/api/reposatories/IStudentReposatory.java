package com.ece.api.reposatories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ece.api.entities.Student;

@Repository
public interface IStudentReposatory extends JpaRepository<Student, Integer> {

    //check whether the email is already present
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndStudentIdNot(String email, int studentId);
}
