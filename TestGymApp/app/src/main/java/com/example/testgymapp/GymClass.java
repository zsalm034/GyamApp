package com.example.testgymapp;

import java.util.ArrayList;

public class GymClass {
    private String className;
    private String description;
    private String startTime, endTime;
    private int maximumCapacity;
    private String day;
    private String difficulty;
    private User instructor;
    private ArrayList<User> members;
    private int numberOfUsers;

    public GymClass(){}
    public GymClass(String className, String description){
        this.className = className;
        this.description = description;
    }
    public GymClass(String className, String description, String startTime, String endTime, int maximumCapacity, String day, String difficulty, User instructor, int numberOfUsers){
        this.className = className;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maximumCapacity = maximumCapacity;
        this.day = day;
        this.difficulty = difficulty;
        this.instructor = instructor;
        members = new ArrayList<User>();
        this.numberOfUsers = numberOfUsers;
    }
    public GymClass(String className, String description, String startTime, String endTime, int maximumCapacity, String day, String difficulty, User instructor){
        this.className = className;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maximumCapacity = maximumCapacity;
        this.day = day;
        this.difficulty = difficulty;
        this.instructor = instructor;
        members = new ArrayList<User>();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartTime(){return startTime;}

    public void setStartTime(String startTime){this.startTime = startTime;}

    public String getEndTime(){return endTime;}

    public void setEndTime(String endTime){this.endTime = endTime;}

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDifficulty(){return difficulty;}

    public void setDifficulty(String diff){this.difficulty = diff;}

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public ArrayList<User> getMembers() {
        return members;
    }

    public boolean addMember(User user) {
        if (numberOfUsers<maximumCapacity){
            members.add(user);
            numberOfUsers+=1;
            return true;
        }
        return false;
    }

    public boolean removeMember (User user){
        if (numberOfUsers>0){
            members.remove(user);
            numberOfUsers-=1;
            return true;
        }
        return false;
    }

    public int getNumberOfUsers() {
        return numberOfUsers;
    }

    public void setNumberOfUsers(int numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }
}
