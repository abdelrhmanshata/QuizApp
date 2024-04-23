package com.example.quizapp.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ModelQuiz implements Serializable {

    String userID;
    String quizID, quizSubject, quizTitle, quizAcademicYear, quizDate, quizSTime, quizETime;
    double quizDegree;
    List<ModelQuestion> questions = new ArrayList<>();

    public ModelQuiz() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getQuizID() {
        return quizID;
    }

    public void setQuizID(String quizID) {
        this.quizID = quizID;
    }

    public String getQuizSubject() {
        return quizSubject;
    }

    public void setQuizSubject(String quizSubject) {
        this.quizSubject = quizSubject;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizAcademicYear() {
        return quizAcademicYear;
    }

    public void setQuizAcademicYear(String quizAcademicYear) {
        this.quizAcademicYear = quizAcademicYear;
    }

    public String getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(String quizDate) {
        this.quizDate = quizDate;
    }

    public String getQuizSTime() {
        return quizSTime;
    }

    public void setQuizSTime(String quizSTime) {
        this.quizSTime = quizSTime;
    }

    public String getQuizETime() {
        return quizETime;
    }

    public void setQuizETime(String quizETime) {
        this.quizETime = quizETime;
    }

    public double getQuizDegree() {
        return quizDegree;
    }

    public void setQuizDegree(double quizDegree) {
        this.quizDegree = quizDegree;
    }

    public List<ModelQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<ModelQuestion> questions) {
        this.questions = questions;
    }
}
