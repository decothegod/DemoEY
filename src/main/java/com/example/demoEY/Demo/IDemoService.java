package com.example.demoEY.Demo;

import com.example.demoEY.Model.User;
import org.springframework.http.ResponseEntity;

public interface IDemoService {
    ResponseEntity<DemoResponse> getAllUser();

    ResponseEntity<DemoResponse> updateUser(User user, Long id);

    ResponseEntity<DemoResponse> deleteUser(Long id);
}
