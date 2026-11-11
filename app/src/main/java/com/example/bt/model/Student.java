package com.example.bt.model;

import java.io.Serializable;

public class Student implements Serializable {
    private String studentId;
    private String fullName;
    private String className;
    private String email;

    public Student() {
    }

    public Student(String studentId, String fullName, String className, String email) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.className = className;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
