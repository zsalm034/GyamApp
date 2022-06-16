package com.example.testgymapp;

public class GymClassType {
    private String className;
    private String description;

    public GymClassType(String className, String description){
        this.className = className;
        this.description = description;
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
}
