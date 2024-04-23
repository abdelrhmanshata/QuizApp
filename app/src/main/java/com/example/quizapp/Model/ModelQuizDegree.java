package com.example.quizapp.Model;

import java.io.Serializable;

public class ModelQuizDegree implements Serializable {

    String ID, StudentID, QuizID;
    String StudentDegree, QuizDegree, StudentName;
    String QuizTitle, QuizSubject, quizAcademicYear;

    public ModelQuizDegree() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStudentID() {
        return StudentID;
    }

    public void setStudentID(String studentID) {
        StudentID = studentID;
    }

    public String getQuizID() {
        return QuizID;
    }

    public void setQuizID(String quizID) {
        QuizID = quizID;
    }

    public String getStudentDegree() {
        return StudentDegree;
    }

    public void setStudentDegree(String studentDegree) {
        StudentDegree = studentDegree;
    }

    public String getQuizDegree() {
        return QuizDegree;
    }

    public void setQuizDegree(String quizDegree) {
        QuizDegree = quizDegree;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getQuizTitle() {
        return QuizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        QuizTitle = quizTitle;
    }

    public String getQuizSubject() {
        return QuizSubject;
    }

    public void setQuizSubject(String quizSubject) {
        QuizSubject = quizSubject;
    }

    public String getQuizAcademicYear() {
        return quizAcademicYear;
    }

    public void setQuizAcademicYear(String quizAcademicYear) {
        this.quizAcademicYear = quizAcademicYear;
    }
}
