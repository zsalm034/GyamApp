package com.example.testgymapp;

import java.util.ArrayList;

public class GymMember extends  User{
    private ArrayList<GymClass> registeredClasses;

    public GymMember(){}

    public GymMember(String name, String email){
        super(name, email);
        registeredClasses = new ArrayList<GymClass>();
        super.setRole("Member");
    }

    public boolean addClass (GymClass gymClass){
        return registeredClasses.add(gymClass);
    }

    public boolean removeClass(GymClass gymClass){
        return registeredClasses.remove(gymClass);
    }

    public ArrayList<GymClass> getRegisteredClasses() {
        return registeredClasses;
    }
}
