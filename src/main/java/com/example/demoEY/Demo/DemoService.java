package com.example.demoEY.Demo;

import com.example.demoEY.Model.Dao.IUserDao;
import com.example.demoEY.Model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demoEY.Utils.UtilsServices.covertDateStr;
import static com.example.demoEY.Utils.UtilsServices.validatePassword;

@Service
@RequiredArgsConstructor
public class DemoService implements IDemoService {
    private static final Logger LOG = LoggerFactory.getLogger(DemoService.class);
    @Autowired
    private IUserDao userDao;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<DemoResponse> getAllUser() {

        try {
            List<User> users = userDao.findAll();
            DemoResponse response = DemoResponse.builder()
                    .users(users)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error to get the Users", e.getMessage());
            DemoResponseMessage responseMessage = new DemoResponseMessage("Error to get the Users");
            return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<DemoResponse> updateUser(User request, Long id) {
        try {
            List<User> users = new ArrayList<>();
            if (request.getPassword() != null && validatePassword(request.getPassword())) {
                userDao.findById(id)
                        .map(user -> {
                            if (request.getName() != null) {
                                user.setName(request.getName());
                            }
                            if (request.getPassword() != null) {
                                user.setPassword(passwordEncoder.encode(request.getPassword()));
                            }
                            user.setModified(covertDateStr(System.currentTimeMillis()));
                            users.add(user);
                            return userDao.save(user);

                        });
                DemoResponseMessage responseMessage = new DemoResponseMessage("User has been updated successfully");
                return new ResponseEntity<>(responseMessage, HttpStatus.OK);
            } else {
                DemoResponseMessage responseMessage = new DemoResponseMessage("The password format invalid, Format correct is One Upper Letter, lower letters and two numbers.");
                return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            LOG.error("Error to update the User", e.getMessage());
            DemoResponseMessage responseMessage = new DemoResponseMessage("Error to update the User");
            return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<DemoResponse> deleteUser(Long id) {
        try {
            userDao.deleteById(id);
            DemoResponseMessage responseMessage = new DemoResponseMessage("User has been deleted successfully");
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error to delete the User", e.getMessage());
            DemoResponseMessage responseError = new DemoResponseMessage("Error to delete the User");
            return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
