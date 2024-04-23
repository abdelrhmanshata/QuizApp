package com.example.quizapp.Model;

public class ModelUser {

    private String ID, userFullName, userEmail, userPassword, userType, userAcademicYear;

    public ModelUser() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserAcademicYear() {
        return userAcademicYear;
    }

    public void setUserAcademicYear(String userAcademicYear) {
        this.userAcademicYear = userAcademicYear;
    }
}
