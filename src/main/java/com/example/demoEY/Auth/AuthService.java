package com.example.demoEY.Auth;

import com.example.demoEY.Model.Dao.IUserDao;
import com.example.demoEY.Model.Role;
import com.example.demoEY.Model.User;
import com.example.demoEY.jwt.IJwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.demoEY.Utils.UtilsServices.*;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private IUserDao userDao;
    @Autowired
    private IJwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            updateLastLogin(request.getUsername());

            UserDetails user = userDao
                    .findByEmail(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not Found"));
            String token = jwtService.getToken(user);
            AuthResponse response = AuthResponse.builder()
                    .token(token)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            LOG.error("Error Internal to Register the user", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("User not Found.");
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(errorResponse);
        } catch (AuthenticationException e) {
            LOG.error("Authentication has been failed", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Authentication has been failed, review user or password.");
            return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(errorResponse);
        } catch (Exception e) {
            LOG.error("Error Internal to Login the user", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Error Internal to Login the user.");
            return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR)).body(errorResponse);
        }

    }

    private void updateLastLogin(String username) {
        if (userDao.findByEmail(username).isPresent()) {
            userDao.findByEmail(username)
                    .map(user -> {
                        user.setLastLogin(covertDateStr(System.currentTimeMillis()));
                        return userDao.save(user);
                    });
        }
    }

    @Override
    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        try {
            if (!userDao.findByEmail(request.email).isPresent()) {
                if (validateEmail(request.email)) {
                    if (validatePassword(request.password)) {
                        User user = User.builder()
                                .name(request.name)
                                .password(passwordEncoder.encode(request.password))
                                .email(request.email)
                                .created(covertDateStr(System.currentTimeMillis()))
                                .lastLogin(covertDateStr(System.currentTimeMillis()))
                                .phones(request.phones)
                                .role(Role.USER)
                                .build();
                        userDao.save(user);

                        AuthResponse response = AuthResponse.builder()
                                .token(jwtService.getToken(user))
                                .build();
                        return new ResponseEntity<>(response, HttpStatus.OK);
                    } else {
                        LOG.error("The password format invalid, Format correct is One Upper Letter, lower letters and two numbers.");
                        ErrorResponse errorResponse = new ErrorResponse("The password format invalid, Format correct is One Upper Letter, lower letters and two numbers.");
                        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(errorResponse);
                    }
                } else {
                    LOG.error("The email format invalid.");
                    ErrorResponse errorResponse = new ErrorResponse("The email format invalid.");
                    return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(errorResponse);
                }
            } else {
                LOG.error("The email already registered.");
                ErrorResponse errorResponse = new ErrorResponse("The email already registered.");
                return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(errorResponse);
            }
        } catch (Exception e) {
            LOG.error("Error Internal to Register the user", e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("Error Internal to Register the user.");
            return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR)).body(errorResponse);
        }
    }
}
