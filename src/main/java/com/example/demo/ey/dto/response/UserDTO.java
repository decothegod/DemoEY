package com.example.demo.ey.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String password;
    private List<PhoneDTO> phones;
    private String created;
    private String modified;
    private String lastLogin;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    @JsonProperty("isActive")
    private boolean isActive;
}
