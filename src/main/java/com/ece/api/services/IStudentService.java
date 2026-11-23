package com.ece.api.services;

import java.util.List;

import com.ece.api.entities.Student;

public interface IStudentService {

    public Student createStudent(Student student);
    public Student updateStudent(int studentId, Student student);
    public boolean deleteStudent(int studentId);
    public Student getStudentById(int studentId);
    public List<Student> getAllStudents();
}
