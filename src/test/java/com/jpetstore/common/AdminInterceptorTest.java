package com.jpetstore.common;

import com.jpetstore.domain.Account;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.PrintWriter;
import java.io.StringWriter;
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
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setContentType("application/json;charset=utf-8");
    }

    @Test
    void testUserNotAdmin() throws Exception {
        Account user = new Account();
        user.setRole("USER");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));
        assertFalse(interceptor.preHandle(request, response, new Object()));
        verify(response).setContentType("application/json;charset=utf-8");
    }

    @Test
    void testAdminUser() throws Exception {
        Account user = new Account();
        user.setRole("ADMIN");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("user")).thenReturn(user);
        assertTrue(interceptor.preHandle(request, response, new Object()));
    }
}