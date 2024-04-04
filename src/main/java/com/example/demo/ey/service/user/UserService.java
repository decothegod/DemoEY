package com.example.demo.ey.service.user;

import com.example.demo.ey.dto.request.LoginRequest;
import com.example.demo.ey.dto.request.RegisterRequest;
import com.example.demo.ey.dto.request.UpdatedRequest;
import com.example.demo.ey.dto.response.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO register(RegisterRequest request);

    UserDTO login(LoginRequest request);

    List<UserDTO> getAllUser();

    UserDTO getUserByUUID(String uuid);

    UserDTO updateUser(String username, UpdatedRequest request);

    void deleteUser(String username);
}
