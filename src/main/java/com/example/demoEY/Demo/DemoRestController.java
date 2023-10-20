package com.example.demoEY.Demo;

import com.example.demoEY.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
public class DemoRestController {
    @Autowired
    IDemoService service;

    @GetMapping("/users")
    public ResponseEntity<DemoResponse> getAllUsers() {
        ResponseEntity<DemoResponse> response = service.getAllUser();
        return response;
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<DemoResponse> updateUser(@RequestBody User request, @PathVariable Long id){
        ResponseEntity<DemoResponse> response = service.updateUser(request, id);
        return response;
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<DemoResponse> deleteUser(@PathVariable Long id){
        ResponseEntity<DemoResponse> response = service.deleteUser(id);
        return response;
    }
}
