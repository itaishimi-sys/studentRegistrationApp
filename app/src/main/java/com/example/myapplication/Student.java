package com.example.myapplication;
import java.io.Serializable;

public class Student implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String grade;
    private String major;
    private boolean notificationsEnabled;
    private boolean termsAccepted;

    public Student(String firstName, String lastName, String email,
                   String grade, String major,
                   boolean notificationsEnabled, boolean termsAccepted) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.grade = grade;
        this.major = major;
        this.notificationsEnabled = notificationsEnabled;
        this.termsAccepted = termsAccepted;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }
}