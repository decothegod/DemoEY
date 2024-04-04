package com.example.demo.ey.service.jwt;

import com.example.demo.ey.model.Phone;
import com.example.demo.ey.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.List;

import static com.example.demo.ey.utils.UtilsServices.generateUUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtServiceImpl jwtServiceImpl;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternal_ValidToken() throws Exception {
        String token = "validToken";

        Phone phone = Phone.builder()
                .id(1L)
                .number(7654321L)
                .citycode(2)
                .contrycode("+56")
                .build();

        List<Phone> phones = Collections.singletonList(phone);

        User user = User.builder()
                .id(1L)
                .UUID(generateUUID())
                .name("testUser")
                .email("email@domain.org")
                .password("password")
                .phones(phones)
                .created("16-11-2023 12:00:00")
                .modified("")
                .lastLogin("16-11-2023 12:00:00")
                .isActive(Boolean.TRUE)
                .build();

        when(request.getHeader(any())).thenReturn("Bearer " + token);
        when(jwtServiceImpl.getUserNameFromToken(token)).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(user);
        when(jwtServiceImpl.isTokenValid(token, user)).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(securityContext, times(1)).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken() throws Exception {
        String token = "invalidToken";

        Phone phone = Phone.builder()
                .id(1L)
                .number(7654321L)
                .citycode(2)
                .contrycode("+56")
                .build();

        List<Phone> phones = Collections.singletonList(phone);

        User user = User.builder()
                .id(1L)
                .UUID(generateUUID())
                .name("testUser")
                .email("email@domain.org")
                .password("password")
                .phones(phones)
                .created("16-11-2023 12:00:00")
                .modified("")
                .lastLogin("16-11-2023 12:00:00")
                .isActive(Boolean.TRUE)
                .build();

        when(request.getHeader(any())).thenReturn("Bearer " + token);
        when(jwtServiceImpl.getUserNameFromToken(token)).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(user);
        when(jwtServiceImpl.isTokenValid(token, user)).thenReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(securityContext, never()).setAuthentication(any(UsernamePasswordAuthenticationToken.class));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_NoToken() throws Exception {
        when(request.getHeader(any())).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
