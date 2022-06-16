package com.example.testgymapp;

public class Administrator extends User{
    public Administrator(){}
    public Administrator(String name, String email){
        super(name, email);
        super.setRole("Administrator");
    }
}
