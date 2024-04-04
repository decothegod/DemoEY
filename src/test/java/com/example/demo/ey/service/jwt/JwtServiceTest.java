package com.example.demo.ey.service.jwt;

import com.example.demo.ey.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class JwtServiceTest {
    @InjectMocks
    private JwtServiceImpl jwtServiceImpl;
    @Mock
    private User userDetails;
    @Mock
    private Claims claims;
    @Value("${jwt.secretKey}")
    public String secretKey;
    @Value("${jwt.validityTime}")
    public long validityTime;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtServiceImpl, "SECRET_KEY", secretKey);
        ReflectionTestUtils.setField(jwtServiceImpl, "JWT_TOKEN_VALIDITY", validityTime);
    }

    @Test
    void getUserNameFromToken_Successful() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtServiceImpl.getToken(userDetails);
        String username = jwtServiceImpl.getUserNameFromToken(token);

        assertNotNull(token);
        assertNotNull(username);
        assertEquals("testUser", username);
    }

    @Test
    void isTokenValid_ValidToken() {
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtServiceImpl.getToken(userDetails);

        boolean isValid = jwtServiceImpl.isTokenValid(token, userDetails);

        assertNotNull(token);
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJBaWx5biBGZXJyYWRhIiwiaWF0IjoxNzAwMTk5MzE3LCJleHAiOjE3MDAyMDA3NTd9.q_kjnBnIm5Mdx55ImEnR7l05RsQGrtgm1Ihr_O_PXRI";
        Assertions.assertThrows(ExpiredJwtException.class, () -> {
            jwtServiceImpl.isTokenValid(invalidToken, userDetails);
        });
    }

    @Test
    void getExpiration_Successful() {
        String token = jwtServiceImpl.getToken(userDetails);

        Date expiration = jwtServiceImpl.getExpiration(token);

        assertNotNull(expiration);
    }

}
