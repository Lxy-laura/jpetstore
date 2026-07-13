package com.jpetstore.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(account);
    }

    @Test
    void testGetFullName() {
        account.setFirstname("John");
        account.setLastname("Doe");
        assertEquals("John Doe", account.getFullName());
    }

    @Test
    void testIsAdminWithAdminRole() {
        account.setRole("ADMIN");
        assertTrue(account.isAdmin());
    }

    @Test
    void testIsAdminWithUserRole() {
        account.setRole("USER");
        assertFalse(account.isAdmin());
    }

    @Test
    void testIsAdminWithNullRole() {
        account.setRole(null);
        assertFalse(account.isAdmin());
    }

    @Test
    void testAllFields() {
        account.setUserid("testuser");
        account.setEmail("test@test.com");
        account.setFirstname("Test");
        account.setLastname("User");
        account.setStatus("OK");
        account.setAddr1("123 Main St");
        account.setAddr2("Apt 4");
        account.setCity("Springfield");
        account.setState("IL");
        account.setZip("12345");
        account.setCountry("USA");
        account.setPhone("555-1234");
        account.setRole("USER");

        assertEquals("testuser", account.getUserid());
        assertEquals("test@test.com", account.getEmail());
        assertEquals("Test", account.getFirstname());
        assertEquals("User", account.getLastname());
        assertEquals("OK", account.getStatus());
        assertEquals("123 Main St", account.getAddr1());
        assertEquals("Springfield", account.getCity());
        assertEquals("IL", account.getState());
        assertEquals("12345", account.getZip());
        assertEquals("USA", account.getCountry());
        assertEquals("555-1234", account.getPhone());
        assertEquals("USER", account.getRole());
    }

    @Test
    void testSignOn() {
        SignOn signOn = new SignOn();
        signOn.setUsername("testuser");
        signOn.setPassword("password");
        account.setSignOn(signOn);

        assertNotNull(account.getSignOn());
        assertEquals("testuser", account.getSignOn().getUsername());
        assertEquals("password", account.getSignOn().getPassword());
    }

    @Test
    void testProfile() {
        Profile profile = new Profile();
        profile.setUserid("testuser");
        profile.setLangpref("english");
        profile.setFavcategory("DOGS");
        account.setProfile(profile);

        assertNotNull(account.getProfile());
        assertEquals("testuser", account.getProfile().getUserid());
        assertEquals("english", account.getProfile().getLangpref());
        assertEquals("DOGS", account.getProfile().getFavcategory());
    }
}