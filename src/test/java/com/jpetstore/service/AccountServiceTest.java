package com.jpetstore.service;

import com.jpetstore.domain.Account;
import com.jpetstore.domain.Profile;
import com.jpetstore.domain.SignOn;
import com.jpetstore.mapper.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private SignOn signOn;
    private Profile profile;

    @BeforeEach
    void setUp() {
        signOn = new SignOn("j2ee", "j2ee");
        profile = new Profile("j2ee", "english", "DOGS", true, true);

        account = new Account();
        account.setUserid("j2ee");
        account.setEmail("j2ee@jpetstore.com");
        account.setFirstname("John");
        account.setLastname("Doe");
        account.setStatus("OK");
        account.setAddr1("901 San Antonio Road");
        account.setCity("Palo Alto");
        account.setState("CA");
        account.setZip("94303");
        account.setCountry("US");
        account.setPhone("555-555-5555");
        account.setSignOn(signOn);
        account.setProfile(profile);
    }

    @Test
    void testGetAccountByUsername() {
        when(accountMapper.getAccountByUsername("j2ee")).thenReturn(account);

        Account result = accountService.getAccountByUsername("j2ee");

        assertNotNull(result);
        assertEquals("j2ee", result.getUserid());
        assertEquals("John", result.getFirstname());
        verify(accountMapper, times(1)).getAccountByUsername("j2ee");
    }

    @Test
    void testGetAccountByUsernameNotFound() {
        when(accountMapper.getAccountByUsername("NOT_EXIST")).thenReturn(null);

        Account result = accountService.getAccountByUsername("NOT_EXIST");

        assertNull(result);
    }

    @Test
    void testLoginSuccess() {
        when(accountMapper.getAccountByUsernameAndPassword("j2ee", "j2ee")).thenReturn(account);

        Account result = accountService.login("j2ee", "j2ee");

        assertNotNull(result);
        assertEquals("j2ee", result.getUserid());
    }

    @Test
    void testLoginFailure() {
        when(accountMapper.getAccountByUsernameAndPassword("j2ee", "wrong")).thenReturn(null);

        Account result = accountService.login("j2ee", "wrong");

        assertNull(result);
    }

    @Test
    void testRegisterSuccess() {
        when(accountMapper.insertAccount(account)).thenReturn(1);
        when(accountMapper.insertSignOn(signOn)).thenReturn(1);
        when(accountMapper.insertProfile(profile)).thenReturn(1);

        boolean result = accountService.register(account);

        assertTrue(result);
        verify(accountMapper, times(1)).insertAccount(account);
        verify(accountMapper, times(1)).insertSignOn(signOn);
        verify(accountMapper, times(1)).insertProfile(profile);
    }

    @Test
    void testRegisterFailure() {
        when(accountMapper.insertAccount(account)).thenThrow(new RuntimeException("DB error"));

        boolean result = accountService.register(account);

        assertFalse(result);
    }

    @Test
    void testUpdateAccountSuccess() {
        when(accountMapper.updateAccount(account)).thenReturn(1);
        when(accountMapper.updateSignOn(signOn)).thenReturn(1);
        when(accountMapper.updateProfile(profile)).thenReturn(1);

        boolean result = accountService.updateAccount(account);

        assertTrue(result);
    }

    @Test
    void testDeleteAccountSuccess() {
        when(accountMapper.deleteSignOn("j2ee")).thenReturn(1);
        when(accountMapper.deleteProfile("j2ee")).thenReturn(1);
        when(accountMapper.deleteAccount("j2ee")).thenReturn(1);

        boolean result = accountService.deleteAccount("j2ee");

        assertTrue(result);
        verify(accountMapper, times(1)).deleteSignOn("j2ee");
        verify(accountMapper, times(1)).deleteProfile("j2ee");
        verify(accountMapper, times(1)).deleteAccount("j2ee");
    }

    @Test
    void testDeleteAccountFailure() {
        when(accountMapper.deleteSignOn("j2ee")).thenThrow(new RuntimeException("DB error"));

        boolean result = accountService.deleteAccount("j2ee");

        assertFalse(result);
    }
}