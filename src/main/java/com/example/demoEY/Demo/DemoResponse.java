package com.example.demoEY.Demo;

import com.example.demoEY.Model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemoResponse {
    public List<User> users;
}
