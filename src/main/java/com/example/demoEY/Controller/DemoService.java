package com.example.demoEY.Controller;

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

import java.util.List;

import static com.example.demoEY.Utils.UtilsServices.covertDateStr;

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
            DemoResponseError responseError = new DemoResponseError("Error to get the Users");
            return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<DemoResponse> updateUser(User request, Long id) {
        try {
            userDao.findById(id)
                    .map(user -> {
                        if (request.getName() != null) {
                            user.setName(request.getName());
                        }
                        if (request.getPassword() != null) {
                            user.setPassword(passwordEncoder.encode(request.getPassword()));
                        }
                        user.setModified(covertDateStr(System.currentTimeMillis()));
                        return userDao.save(user);
                    });
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error to update the User", e.getMessage());
            DemoResponseError responseError = new DemoResponseError("Error to update the User");
            return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<DemoResponse> deleteUser(Long id) {
        try {
            userDao.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOG.error("Error to delete the User", e.getMessage());
            DemoResponseError responseError = new DemoResponseError("Error to delete the User");
            return new ResponseEntity<>(responseError, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
