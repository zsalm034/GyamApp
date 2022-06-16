package com.example.testgymapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class JUnitTestUser {
    @Test
    public void testUser(){
        User user1 = new User("Steven", "steven4590@gmail.com");

        // Verify the name of the user
        assertEquals(user1.getName(), "Steven");
        user1.setName("Joe");
        assertEquals(user1.getName(), "Joe");

        // Verify the email of the user.
        assertEquals(user1.getEmail(), "steven4590@gmail.com");
        user1.setEmail("joerogers3321@yahoo.com");
        assertEquals(user1.getEmail(), "joerogers3321@yahoo.com");

        // Verify the role of the user
        assertEquals(user1.getRole(), null);

        // Check if the serRole function works.
        user1.setRole("Instructor");
        assertEquals(user1.getRole(), "Instructor");

        // Verify the empty user class is functional.
        User emptyUS = new User();
        assertNotEquals(emptyUS, null);

    }
}