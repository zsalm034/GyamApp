package com.example.testgymapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class JUnitTestInstructor {
    @Test
    public void testInstructor(){
        // Verify the information of the instructor.
        Instructor instructor = new Instructor("Tracey", "traceyJames1023@yahoo.com");
        assertEquals(instructor.getName(), "Tracey");
        assertEquals(instructor.getEmail(), "traceyJames1023@yahoo.com");
        assertEquals(instructor.getRole(), "Instructor");

        // Initialize Gym Class that are to added to the instructor.
        GymClass g1 = new GymClass("Volleyball", "Play volleyball and learn various moves.");
        GymClass g2 = new GymClass("Boxing", "Learn how to box.");
        GymClass g3 = new GymClass("Karate", "Learn the various moves used in karate.");
        GymClass g4 = new GymClass("Tennis", "Play tennis with your friends.");
        GymClass g5 = new GymClass("Swimming", "Learn the various techniques when swimming.");

        // Add the Gym Classes to the Instructor.
        instructor.addClass(g1);
        instructor.addClass(g2);
        instructor.addClass(g3);
        instructor.addClass(g4);
        instructor.addClass(g5);

        // Verify that the items are added and use the getClasses() function.
        assertEquals(instructor.getClasses().size(), 5);

        // Remove 2 classes and verify their removal.
        instructor.removeClass(g2);
        instructor.removeClass(g5);
        assertEquals(instructor.getClasses().size(), 3);

        // Verify that the empty Instructor class is functional.
        Instructor emptyI = new Instructor();
        assertNotEquals(emptyI, null);
    }
}
