package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.validator.FieldMatch;
import com.globalvox.clinicmanagementsystem.validator.Password;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldMatch(first = "newPassword",second ="confirmPassword",message = "Password and Confirm Password Must be Match")
public class PasswordUpdateDTO {

    private Long id;

    private String oldPassword;

    @Password
    private String newPassword;

    private  String confirmPassword;

}
