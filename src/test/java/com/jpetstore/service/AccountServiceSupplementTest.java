package com.jpetstore.service;

import com.jpetstore.domain.Account;
import com.jpetstore.mapper.AccountMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceSupplementTest {
    @Mock private AccountMapper accountMapper;
    @InjectMocks private AccountService accountService;

    @Test
    void testGetAllUsers() {
        Account a = new Account();
        a.setUserid("j2ee");
        when(accountMapper.getAllUsers()).thenReturn(Arrays.asList(a));
        List<Account> result = accountService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("j2ee", result.get(0).getUserid());
        verify(accountMapper, times(1)).getAllUsers();
    }
}