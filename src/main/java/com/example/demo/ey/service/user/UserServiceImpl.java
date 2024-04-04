package com.example.demo.ey.service.user;

import com.example.demo.ey.dto.request.LoginRequest;
import com.example.demo.ey.dto.request.RegisterRequest;
import com.example.demo.ey.dto.request.UpdatedRequest;
import com.example.demo.ey.dto.response.PhoneDTO;
import com.example.demo.ey.dto.response.UserDTO;
import com.example.demo.ey.exception.ServiceExceptionBadRequest;
import com.example.demo.ey.exception.ServiceExceptionNotFound;
import com.example.demo.ey.exception.ServiceExceptionUnauthorized;
import com.example.demo.ey.model.Phone;
import com.example.demo.ey.model.User;
import com.example.demo.ey.repository.UserRepository;
import com.example.demo.ey.service.jwt.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.ey.utils.Constants.*;
import static com.example.demo.ey.utils.UtilsServices.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Value("${passwordRegexPattern.regexp}")
    private String passwordRegex;
    @Value("${emailRegexPattern.regexp}")
    private String emailRegex;

    @Override
    @Transactional
    public UserDTO register(RegisterRequest request) {
        checkEmailFormat(request.getEmail());
        checkPasswordFormat(request.getPassword());
        List<PhoneDTO> phoneDTOList = (request.getPhones() != null) ? request.getPhones() : new ArrayList<>();
        checkPhonesFormat(phoneDTOList);
        checkExistEmail(request.getEmail());

        UserDTO userDTO = UserDTO.builder()
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .created(covertDateStr(System.currentTimeMillis()))
                .lastLogin(covertDateStr(System.currentTimeMillis()))
                .isActive(Boolean.TRUE)
                .phones(phoneDTOList)
                .build();

        userRepository.save(createUser(userDTO));

        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO login(LoginRequest request) {
        try {
            checkEmailFormat(request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            User user = getUser(request.getUsername());
            updateLastLogin(user);

            UserDTO userDTO = convertToDTO(user);
            userDTO.setToken(jwtService.getToken(user));
            userDTO.setId(user.getUUID());

            return userDTO;

        } catch (ExpiredJwtException e) {
            log.error(e.getMessage());
            throw new ServiceExceptionUnauthorized(e.getMessage());
        } catch (AuthenticationException e) {
            log.error(BAD_CREDENTIALS_MSG);
            throw new ServiceExceptionUnauthorized(BAD_CREDENTIALS_MSG);
        }
    }

    @Override
    public List<UserDTO> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDTO).toList();
    }

    @Override
    public UserDTO getUserByUUID(String uuid) {
        User user = userRepository.findByUUID(uuid)
                .orElseThrow(() -> new ServiceExceptionNotFound(USER_NOT_FOUND_MSG));
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(String username, UpdatedRequest request) {
        User user = getUser(username);
        String currentTime = covertDateStr(System.currentTimeMillis());

        User updateUser = User.builder()
                .id(user.getId())
                .UUID(user.getUUID())
                .name(user.getName())
                .password(user.getPassword())
                .email(user.getEmail())
                .created(user.getCreated())
                .lastLogin(user.getLastLogin())
                .modified(user.getModified())
                .phones(user.getPhones())
                .isActive(user.isActive())
                .build();

        if (!StringUtils.isEmpty(request.getEmail())) {
            checkExistEmail(request.getEmail());
            updateUser.setEmail(request.getEmail());
            updateUser.setModified(currentTime);
        }
        if (!StringUtils.isEmpty(request.getName())) {
            updateUser.setName(request.getName());
            updateUser.setModified(currentTime);
        }
        if (!StringUtils.isEmpty(request.getPassword())) {
            checkPasswordFormat(request.getPassword());
            updateUser.setPassword(passwordEncoder.encode(request.getPassword()));
            updateUser.setModified(currentTime);
        }
        if (request.getPhones() != null) {
            checkPhonesFormat(request.getPhones());

            List<Phone> phones = user.getPhones();
            List<PhoneDTO> phoneDTOs = request.getPhones();

            phones.removeIf(phone -> !phoneDTOs.stream()
                    .anyMatch(phoneDTO -> phoneDTO.getNumber().equals(phone.getNumber())));

            phoneDTOs.stream()
                    .filter(phoneDTO -> !phones.stream()
                            .anyMatch(phone -> phoneDTO.getNumber().equals(phone.getNumber())))
                    .map(phoneDTO -> {
                        Phone phone = new Phone();
                        phone.setNumber(Long.valueOf(phoneDTO.getNumber()));
                        phone.setCitycode(Integer.valueOf(phoneDTO.getCitycode()));
                        phone.setContrycode(phoneDTO.getContrycode());
                        return phone;
                    })
                    .forEach(phones::add);
            updateUser.setModified(currentTime);
        }
        if (!StringUtils.isEmpty(Boolean.toString(request.isActive()))) {
            updateUser.setActive(request.isActive());
            updateUser.setModified(currentTime);
        }

        userRepository.save(updateUser);

        return convertToDTO(updateUser);
    }

    @Override
    @Transactional
    public void deleteUser(String username) {
        checkExistEmail(username);
        User user = getUser(username);
        userRepository.delete(user);
    }

    private void checkEmailFormat(String email) {
        if (Boolean.FALSE.equals(validateValueWithRegex(email, emailRegex))) {
            log.error(INVALID_FORMAT_EMAIL_MSG);
            throw new ServiceExceptionBadRequest(INVALID_FORMAT_EMAIL_MSG);
        }
    }

    private void checkPasswordFormat(String password) {
        if (Boolean.FALSE.equals(validateValueWithRegex(password, passwordRegex))) {
            log.error(INVALID_FORMAT_PASSWORD_MSG);
            throw new ServiceExceptionBadRequest(INVALID_FORMAT_PASSWORD_MSG);
        }
    }

    private void checkPhonesFormat(List<PhoneDTO> phones) {
        if (phones != null) {
            phones.stream()
                    .filter(phoneDTO -> !StringUtils.isNumeric(phoneDTO.getNumber()))
                    .findFirst()
                    .ifPresent(phoneDTO -> {
                        log.error(INVALID_PHONE_NUMBER_FORMAT_MSG + phoneDTO.getNumber());
                        throw new ServiceExceptionBadRequest(INVALID_PHONE_NUMBER_FORMAT_MSG + phoneDTO.getNumber());
                    });

            phones.stream()
                    .filter(phoneDTO -> !StringUtils.isNumeric(phoneDTO.getCitycode()))
                    .findFirst()
                    .ifPresent(phoneDTO -> {
                        log.error(INVALID_CITY_CODE_FORMAT_MSG + phoneDTO.getCitycode());
                        throw new ServiceExceptionBadRequest(INVALID_CITY_CODE_FORMAT_MSG + phoneDTO.getCitycode());
                    });
        }
    }

    private void checkExistEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.error(USER_EXIST_MSG);
            throw new ServiceExceptionBadRequest(USER_EXIST_MSG);
        }
    }

    private User createUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setUUID(generateUUID());
        userDTO.setToken(jwtService.getToken(user));
        userDTO.setId(user.getUUID());
        user.setPhones(userDTO.getPhones().stream().map(phoneDTO -> modelMapper.map(phoneDTO, Phone.class)).toList());
        return user;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ServiceExceptionNotFound(USER_NOT_FOUND_MSG));
    }

    private void updateLastLogin(User user) {
        user.setLastLogin(covertDateStr(System.currentTimeMillis()));
        userRepository.save(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setId(user.getUUID());
        return userDTO;
    }
}
