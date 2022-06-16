package com.example.testgymapp;

import java.util.ArrayList;

public class Instructor extends User{
    private ArrayList<GymClass> classes;
    private String userID;

    public Instructor(){}
    public Instructor(String name, String email){
        super(name, email);
        classes = new ArrayList<>();
        super.setRole("Instructor");
    }
    public Instructor(String name, String email, String userID){
        super(name, email);
        this.userID = userID;
        classes = new ArrayList<>();
        super.setRole("Instructor");
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public  boolean addClass(GymClass gymClass){
        return classes.add(gymClass);
    }
    public boolean removeClass(GymClass gymClass){
        return classes.remove(gymClass);
    }
    public ArrayList<GymClass> getClasses() {
        return classes;
    }
}
