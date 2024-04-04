package com.example.demo.ey.dto.request;

import com.example.demo.ey.dto.response.PhoneDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedRequest {
    String name;
    String password;
    String email;
    List<PhoneDTO> phones;
    private boolean isActive;
}
