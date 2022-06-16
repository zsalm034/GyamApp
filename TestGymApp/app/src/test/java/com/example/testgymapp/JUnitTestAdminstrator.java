package com.example.testgymapp;

import org.junit.Test;

import static org.junit.Assert.*;

public class JUnitTestAdminstrator {
    @Test
    public void testAdministrator(){
        // Verify the information of the administrator
        Administrator admin = new Administrator("Anderson", "adminboss@hotmail.com");
        assertEquals(admin.getName(), "Anderson");
        assertEquals(admin.getEmail(), "adminboss@hotmail.com");
        assertEquals(admin.getRole(), "Administrator");

        // Verify that the empty Administrator class is functional.
        Administrator emptyA = new Administrator();
        assertNotEquals(emptyA, null);
    }
}
