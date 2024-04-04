package com.example.demo.ey.controller;

import com.example.demo.ey.dto.request.LoginRequest;
import com.example.demo.ey.dto.request.RegisterRequest;
import com.example.demo.ey.dto.request.UpdatedRequest;
import com.example.demo.ey.dto.response.PhoneDTO;
import com.example.demo.ey.dto.response.UserDTO;
import com.example.demo.ey.service.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserControllerTest {
    private static final String TEST_NAME = "userTest";
    private static final String TEST_NAME_UPDATED = "userTestUpdated";
    private static final String TEST_EMAIL = "email@test.org";
    private static final String TEST_NUMBER = "123467";
    private static final String TEST_CITY_CODE = "1";
    private static final String TEST_CONTRY_CODE = "57";
    private static final String TEST_PASSWORD = "Password12";
    private static final String TOKEN = "token";
    private static final String TIMESTAMP = "26-01-2024 12:00:00";
    private static final String TEST_UUID = "0e0007b9-b96a-4c8e-b8af-74715e6ff3f2";
    private static final String TEST_UUID_2 = "49414c78-f032-4226-bb7c-b95240fc8355";

    @InjectMocks
    UserController userController;
    @Mock
    UserService userService;

    @Test
    void loginControllerTest() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();
        List<PhoneDTO> phoneDTOList = new ArrayList<>();
        phoneDTOList.add(phoneDTO);

        LoginRequest request = LoginRequest.builder()
                .username(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        UserDTO expectedResponse = UserDTO.builder()
                .id(TEST_UUID)
                .name(TEST_NAME)
                .password(TEST_PASSWORD)
                .lastLogin(TIMESTAMP)
                .created(TIMESTAMP)
                .modified("")
                .isActive(Boolean.TRUE)
                .phones(phoneDTOList)
                .token(TOKEN)
                .build();

        when(userService.login(request)).thenReturn(expectedResponse);

        ResponseEntity<UserDTO> responseEntity = userController.login(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void registerControllerTest() {
        PhoneDTO phoneDTO = PhoneDTO.builder()
                .number(TEST_NUMBER)
                .citycode(TEST_CITY_CODE)
                .contrycode(TEST_CONTRY_CODE)
                .build();
        List<PhoneDTO> phoneDTOList = new ArrayList<>();
        phoneDTOList.add(phoneDTO);

        RegisterRequest request = RegisterRequest.builder()
                .name(TEST_EMAIL)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .phones(phoneDTOList)
                .build();

        UserDTO expectedResponse = UserDTO.builder()
                .id(TEST_UUID_2)
                .name(TEST_NAME)
                .password(TEST_PASSWORD)
                .lastLogin(TIMESTAMP)
                .created(TIMESTAMP)
                .modified("")
                .isActive(Boolean.TRUE)
                .phones(phoneDTOList)
                .token(TOKEN)
                .build();

        when(userService.register(request)).thenReturn(expectedResponse);

        ResponseEntity<UserDTO> responseEntity = userController.register(request);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void getAllUserTest() {
        UserDTO userDTOExpected = UserDTO.builder()
                .id(TEST_UUID)
                .name(TEST_NAME)
                .password(TEST_PASSWORD)
                .lastLogin(TIMESTAMP)
                .created(TIMESTAMP)
                .modified("")
                .isActive(Boolean.TRUE)
                .phones(new ArrayList<>())
                .token(TOKEN)
                .build();

        UserDTO userDTOExpected2 = UserDTO.builder()
                .id(TEST_UUID)
                .name(TEST_NAME)
                .password(TEST_PASSWORD)
                .lastLogin(TIMESTAMP)
                .created(TIMESTAMP)
                .modified("")
                .isActive(Boolean.TRUE)
                .phones(new ArrayList<>())
                .token(TOKEN)
                .build();

        List<UserDTO> expectedResponse = List.of(userDTOExpected, userDTOExpected2);

        when(userService.getAllUser()).thenReturn(expectedResponse);

        ResponseEntity<List<UserDTO>> responseEntity = userController.getAllUsers();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void getUserByUUIDTest() {
        UserDTO expectedResponse = UserDTO.builder()
                .id(TEST_UUID)
                .name(TEST_NAME)
                .password(TEST_PASSWORD)
                .lastLogin(TIMESTAMP)
                .created(TIMESTAMP)
                .modified("")
                .isActive(Boolean.TRUE)
                .phones(new ArrayList<>())
                .token(TOKEN)
                .build();

        when(userService.getUserByUUID(TEST_UUID)).thenReturn(expectedResponse);

        ResponseEntity<UserDTO> responseEntity = userController.getUserByUUID(TEST_UUID);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void updatedUserTest() {
        UserDTO expectedResponse = UserDTO.builder()
                .id(TEST_UUID)
                .name(TEST_NAME_UPDATED)
                .password(TEST_PASSWORD)
                .lastLogin(TIMESTAMP)
                .created(TIMESTAMP)
                .modified(TIMESTAMP)
                .isActive(Boolean.TRUE)
                .phones(new ArrayList<>())
                .token(TOKEN)
                .build();

        UpdatedRequest request = UpdatedRequest.builder()
                .name(TEST_NAME_UPDATED)
                .build();

        when(userService.updateUser(TEST_EMAIL, request)).thenReturn(expectedResponse);

        ResponseEntity<UserDTO> responseEntity = userController.updatedUser(request,TEST_EMAIL);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void deletedUserTest(){
        String expectedResponse = "Username: " + TEST_EMAIL + " has been deleted";

        ResponseEntity<String> responseEntity = userController.deletedUser(TEST_EMAIL);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }
}
