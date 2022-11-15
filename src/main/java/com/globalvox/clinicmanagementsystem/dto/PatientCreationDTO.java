package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.validator.FieldMatch;
import com.globalvox.clinicmanagementsystem.validator.Password;
import com.globalvox.clinicmanagementsystem.validator.ValidEmail;
import com.globalvox.clinicmanagementsystem.validator.ValidMobileNumber;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@FieldMatch(first = "password",second ="password1",message = "Password and Confirm Password must be match.")
public class PatientCreationDTO {

    private Long id;

    @Size(max = 50)
    @NotBlank(message = "is required")
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @Size(max = 50)
    @NotBlank(message = "is required")
    private String lastName;

    @NotBlank(message = "is required")
    @ValidEmail
    private String email;

    private String profilePhoto;

    @Password
    private String password;

    private String  password1;

    @Size(max = 12)
    @NotBlank(message = "is required")
    @ValidMobileNumber
    private String mobileNumber;

    @NotNull(message = "Please Select Gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(value=TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date dateOfBirth;
}
