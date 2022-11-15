package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.validator.FieldMatch;
import com.globalvox.clinicmanagementsystem.validator.Password;
import lombok.Data;

@Data
@FieldMatch(first = "newPassword",second ="confirmPassword",message = "Password and Confirm Password Must be Match")
public class ForgotPasswordDTO {

    String resetPasswordToken;

    @Password
    String newPassword;

    String confirmPassword;
}
