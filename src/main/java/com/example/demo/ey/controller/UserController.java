package com.example.demo.ey.controller;

import com.example.demo.ey.dto.request.LoginRequest;
import com.example.demo.ey.dto.request.RegisterRequest;
import com.example.demo.ey.dto.request.UpdatedRequest;
import com.example.demo.ey.dto.response.UserDTO;
import com.example.demo.ey.service.user.UserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.security.config.Elements.JWT;

@RestController
@RequestMapping("/v1/user")
@OpenAPIDefinition(info = @Info(title = "User API", version = "${app.version}"))
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "Bearer Authentication", scheme = "bearer", bearerFormat = JWT, description = "Need a token generated upon login or registration endpoints")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user", description = "Register a new user with the information provided.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, returns the registered user and the token of the logged in user."),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @Operation(summary = "Log in", description = "Sign in with the provided credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, returns the token of the logged in user."),
            @ApiResponse(responseCode = "401", description = "Unauthorized, incorrect credentials."),
            @ApiResponse(responseCode = "404", description = "Not found, user not found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @Operation(summary = "Get all users", description = "Retrieves the complete list of users. Need a token generated upon login or registration.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, returns the list of users. If there are no users, it returns an empty list"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @Operation(summary = "Get User by id", description = "Retrieves a user by its UUID. Need a token generated upon login or registration.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, return the task."),
            @ApiResponse(responseCode = "404", description = "Not found, task not found."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @GetMapping("/{userUUID}")
    public ResponseEntity<UserDTO> getUserByUUID(
            @Parameter(description = "The identifier code", required = true, example = "1f324716-6a52-4d51-8b58-63060582d1cb")
            @PathVariable String userUUID) {
        return ResponseEntity.ok(userService.getUserByUUID(userUUID));
    }

    @Operation(summary = "update a user", description = "Update a user with the information provided.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, returns the updated user."),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @PutMapping("/{username}")
    public ResponseEntity<UserDTO> updatedUser(
            @RequestBody UpdatedRequest request,
            @Parameter(description = "The username", required = true, example = "Jon Doe")
            @PathVariable String username) {
        return ResponseEntity.ok(userService.updateUser(username, request));

    }

    @Operation(summary = "Delete a user", description = "Delete a user with the information provided.")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success, returns the msg."),
            @ApiResponse(responseCode = "400", description = "Bad Request."),
            @ApiResponse(responseCode = "500", description = "Internal Server Error.")
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deletedUser(
            @Parameter(description = "The username", required = true, example = "Jon Doe")
            @PathVariable String username) {
        userService.deleteUser(username);

        return ResponseEntity.ok("Username: " + username + " has been deleted");

    }
}
