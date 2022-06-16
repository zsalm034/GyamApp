package com.example.testgymapp;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;

public class JUnitTestGymMember {
    @Test
    public void testGymMember(){
        // Initialize the gym member and 2 gym classes.
        GymMember gymM1 = new GymMember("Marco", "marcopolo142@outlook.com");
        GymClass swimmingClass = new GymClass("Swimming", "Swimming lessons on doing front-crawl and back-crawl.");
        GymClass weightLiftingClass = new GymClass("Weight Lifting", "Lift weights to improve upper and lower body strength.");

        // Initialize an empty array to compare the empty registered class for the gym member.
        ArrayList<GymClass> tempArr = new ArrayList<GymClass>();
        assertEquals(gymM1.getRegisteredClasses(), tempArr);

        // Verify the information matches for the gym member.
        assertEquals(gymM1.getName(), "Marco");
        assertEquals(gymM1.getEmail(), "marcopolo142@outlook.com");
        assertEquals(gymM1.getRole(), "Member");

        // Add the gym classes to the gym member.
        gymM1.addClass(swimmingClass);
        gymM1.addClass(weightLiftingClass);

        // Verify the registered classes are added,
        assertEquals(gymM1.getRegisteredClasses().get(0).getClassName(), "Swimming");
        assertEquals(gymM1.getRegisteredClasses().get(1).getClassName(), "Weight Lifting");

        // Remove a gym class and verify that it is removed.
        assertEquals(gymM1.getRegisteredClasses().size(), 2);
        gymM1.removeClass(weightLiftingClass);
        assertEquals(gymM1.getRegisteredClasses().size(), 1);

        // Verify that the empty GymMember class is functional.
        GymMember emptyGM = new GymMember();
        assertNotEquals(emptyGM, null);
    }
}
