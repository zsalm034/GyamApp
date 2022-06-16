package com.example.testgymapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class JUnitTestGymClass {
    @Test
    public void testGymClass(){
        // Initialize the gym class for badminton
        GymClass badminton = new GymClass("Badminton", "Enjoy playing badminton with your friends.");

        // Verify the information for the gym class of badminton matches.
        assertEquals(badminton.getClassName(), "Badminton");
        String description = "Enjoy playing badminton with your friends.";
        assertEquals(badminton.getDescription(), description);

        // Verify the setter classes are functional
        badminton.setClassName("Cricket");
        assertEquals(badminton.getClassName(), "Cricket");
        badminton.setDescription("Have fun with your friends in a game of cricket.");
        String description2 = "Have fun with your friends in a game of cricket.";
        assertEquals(badminton.getDescription(), description2);

        // Initialize a more complex gym class for running
        User instructor = new User("Bob", "bob@gmail.com");
        GymClass running = new GymClass("Running", "Run and improve your stamina",
                "1:30pm", "2:30pm", 3, "Monday", "Medium", instructor);

        // Verify the information for the gym class of running matches.
        assertEquals(running.getStartTime(), "1:30pm");
        assertEquals(running.getEndTime(), "2:30pm");
        assertEquals(running.getMaximumCapacity(), 3);
        assertEquals(running.getDay(), "Monday");
        assertEquals(running.getInstructor().getName(), instructor.getName());
        assertEquals(running.getDifficulty(), "Medium");
        assertEquals(running.getNumberOfUsers(), 0);

        // Verify the setters classes are functional for the complex gym class.
        running.setStartTime("1:00pm");
        running.setEndTime("2:00pm");
        assertEquals(running.getStartTime(), "1:00pm");
        assertEquals(running.getEndTime(), "2:00pm");
        running.setMaximumCapacity(2);
        assertEquals(running.getMaximumCapacity(), 2);
        running.setDay("Friday");
        assertEquals(running.getDay(), "Friday");
        User newInstructor = new User("Johnathan", "johnathan@outlook.com");
        running.setInstructor(newInstructor);
        assertEquals(running.getInstructor().getEmail(), newInstructor.getEmail());
        running.setDifficulty("Hard");
        assertEquals(running.getDifficulty(), "Hard");

        // Initialize user who will join the gym class.
        User user1 = new User("Joe", "joe@yahoo.com");
        User user2 = new User("John", "john@outlook.com");
        User user3 = new User("Daniel", "daniel@gmail.com");

        // Add the users except for Daniel since he cannot join when the maximum capacity is 2.
        running.addMember(user1);
        running.addMember(user2);
        boolean b1 = running.addMember(user3);
        assertTrue(b1 == false);

        // Remove a user and see if Daniel can join the gym class.
        running.removeMember(user2);
        boolean b2 = running.addMember(user3);
        assertTrue(b2 == true);

        // Use the getMembers() function and check to see if Daniel's name is there.
        assertEquals(running.getMembers().get(1).getName(), "Daniel");

        // Change the numberOfUsers and verify the function works.
        running.setNumberOfUsers(10);
        assertEquals(running.getNumberOfUsers(), 10);

        // Verify that the empty GymClass class is functional.
        GymClass emptyGC = new GymClass();
        assertNotEquals(emptyGC, null);

    }
}
