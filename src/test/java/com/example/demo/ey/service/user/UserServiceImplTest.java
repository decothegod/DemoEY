package com.example.demo.ey.service.user;

import com.example.demo.ey.dto.request.LoginRequest;
import com.example.demo.ey.dto.request.RegisterRequest;
import com.example.demo.ey.dto.response.PhoneDTO;
import com.example.demo.ey.dto.response.UserDTO;
import com.example.demo.ey.exception.ServiceExceptionBadRequest;
import com.example.demo.ey.exception.ServiceExceptionNotFound;
import com.example.demo.ey.exception.ServiceExceptionUnauthorized;
import com.example.demo.ey.model.Phone;
import com.example.demo.ey.model.User;
import com.example.demo.ey.repository.UserRepository;
import com.example.demo.ey.service.jwt.JwtService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {
    private static final String TEST_NAME = "userTest";
    private static final String TEST_EMAIL = "email@test.org";
    private static final String TEST_EMAIL_WRONG = "email@test";
    private static final String TEST_NUMBER = "123467";
    private static final String TEST_NUMBER_WRONG = "123-467";
    private static final String TEST_CITY_CODE = "1";
    private static final String TEST_CITY_CODE_WRONG = "+1";
    private static final String TEST_CONTRY_CODE = "57";
    private static final String TEST_PASSWORD = "Password12";
    private static final String TEST_PASSWORD_WRONG = "invalidPassword";
    private static final String ENCODED_PASSWORD = "passwordEncoder";
    private static final String TOKEN = "token";
    private static final String TIMESTAMP = "26-01-2024 12:00:00";
    private static final String TEST_UUID = "0e0007b9-b96a-4c8e-b8af-74715e6ff3f2";

    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private ModelMapper mapper;
    @Value("${passwordRegexPattern.regexp}")
    private String passwordRegex;
    @Value("${emailRegexPattern.regexp}")
    private String emailRegex;

    private User createUserTest() {
        Phone phone = Phone.builder()
                .id(1L)
                .number(Long.parseLong(TEST_NUMBER))
                .citycode(Integer.parseInt(TEST_CITY_CODE))
                .contrycode(TEST_CONTRY_CODE)
                .build();

        return User.builder()
                .id(1L)
                .UUID(TEST_UUID)
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .phones(Collections.singletonList(phone))
                .created(TIMESTAMP)
                .modified("")
                .lastLogin(TIMESTAMP)
                .isActive(Boolean.TRUE)
                .build();
    }

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(userServiceImpl, "passwordRegex", passwordRegex);
        ReflectionTestUtils.setField(userServiceImpl, "emailRegex", emailRegex);
    }

    @Test
    void register_Successful() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();

        RegisterRequest request = RegisterRequest.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .phones(Collections.singletonList(phoneDTO))
                .build();

        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(mapper.map(any(UserDTO.class), eq(User.class))).thenReturn(createUserTest());

        UserDTO response = userServiceImpl.register(request);
        assertNotNull(response);
        assertEquals(TEST_NAME, response.getName());
        assertEquals(TEST_EMAIL, response.getEmail());
        assertEquals(ENCODED_PASSWORD, response.getPassword());
    }

    @Test
    void register_UserExists() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();

        RegisterRequest request = RegisterRequest.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .phones(Collections.singletonList(phoneDTO))
                .build();

        User user = createUserTest();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));
        Assertions.assertThrows(ServiceExceptionBadRequest.class, () -> {
            userServiceImpl.register(request);
        });
    }

    @Test
    public void register_InvalidEmail() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();

        RegisterRequest request = RegisterRequest.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL_WRONG)
                .password(TEST_PASSWORD)
                .phones(Collections.singletonList(phoneDTO))
                .build();

        Assertions.assertThrows(ServiceExceptionBadRequest.class, () -> {
            userServiceImpl.register(request);
        });
    }

    @Test
    void register_register_InvalidPassword() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();

        RegisterRequest request = RegisterRequest.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD_WRONG)
                .phones(Collections.singletonList(phoneDTO))
                .build();

        Assertions.assertThrows(ServiceExceptionBadRequest.class, () -> {
            userServiceImpl.register(request);
        });
    }

    @Test
    void register_register_InvalidPhoneNumber() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER_WRONG)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();

        RegisterRequest request = RegisterRequest.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .phones(Collections.singletonList(phoneDTO))
                .build();

        Assertions.assertThrows(ServiceExceptionBadRequest.class, () -> {
            userServiceImpl.register(request);
        });
    }

    @Test
    void register_register_InvalidCityCode() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE_WRONG)
                .contrycode(TEST_CONTRY_CODE)
                .build();

        RegisterRequest request = RegisterRequest.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .phones(Collections.singletonList(phoneDTO))
                .build();

        Assertions.assertThrows(ServiceExceptionBadRequest.class, () -> {
            userServiceImpl.register(request);
        });
    }

    @Test
    void login_Successful() {
        LoginRequest request = LoginRequest.builder()
                .username(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        User user = createUserTest();

        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();

        UserDTO userDTO = UserDTO.builder()
                .id(TEST_UUID)
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(ENCODED_PASSWORD)
                .phones(Collections.singletonList(phoneDTO))
                .created(TIMESTAMP)
                .modified("")
                .lastLogin(TIMESTAMP)
                .token(TOKEN)
                .isActive(Boolean.TRUE)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.ofNullable(user));
        when(jwtService.getToken(user)).thenReturn(TOKEN);
        when(mapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO response = userServiceImpl.login(request);
        assertNotNull(response);
        assertEquals(TEST_UUID, response.getId());
        assertEquals(TEST_NAME, response.getName());
        assertEquals(TEST_EMAIL, response.getEmail());
        assertEquals(ENCODED_PASSWORD, response.getPassword());
        assertEquals(TOKEN, response.getToken());

    }

    @Test
    void login_UserNotFound() {
        LoginRequest request = LoginRequest.builder()
                .username(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        Assertions.assertThrows(ServiceExceptionNotFound.class, () -> {
            userServiceImpl.login(request);
        });
    }

    @Test
    void login_PasswordIncorrect() {
        LoginRequest request = LoginRequest.builder()
                .username(TEST_EMAIL)
                .password(TEST_PASSWORD_WRONG)
                .build();

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(createUserTest()));
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .thenThrow(BadCredentialsException.class);

        Assertions.assertThrows(ServiceExceptionUnauthorized.class, () -> {
            userServiceImpl.login(request);
        });
    }
}

