package com.example.shoppingcart.infraestructure.adapter.out.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFeignResponseDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private boolean active;
    private String country;
    private String city;
    private String role;
}
