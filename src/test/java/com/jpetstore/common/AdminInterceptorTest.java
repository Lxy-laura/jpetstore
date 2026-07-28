package com.jpetstore.common;

import com.jpetstore.domain.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInterceptorTest {
    private AdminInterceptor interceptor = new AdminInterceptor();
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private HttpSession session;

    @Test
    void testNoUserInSession() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).sendRedirect("/login");
    }

    @Test
    void testUserNotAdmin() throws Exception {
        Account user = new Account();
        user.setRole("USER");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).sendRedirect("/");
    }

    @Test
    void testAdminUser() throws Exception {
        Account user = new Account();
        user.setRole("ADMIN");
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        assertTrue(interceptor.preHandle(request, response, new Object()));
    }
}