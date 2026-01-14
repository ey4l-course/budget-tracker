package com.budget.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
    private String id;
    private String username;
    private String password;
    private String givenName;
    private String surname;
    private String mobile;
    private String email;
    private Address address;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String state;
        private String city;
        private String street;
        private int house;
        private int apartment;
        private String zipcode;
    }
}
