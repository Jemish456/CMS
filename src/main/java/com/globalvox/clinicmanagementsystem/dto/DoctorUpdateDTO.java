package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.validator.ValidEmail;
import com.globalvox.clinicmanagementsystem.validator.ValidMobileNumber;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Getter
@Setter
public class DoctorUpdateDTO {

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

    @Size(max = 12)
    @NotBlank(message = "is required")
    @ValidMobileNumber
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(value= TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @Min(1)
    private int yearOfExperience;

    private String specialization;

    private String licence;

    @DecimalMin(value = "0.00")
    @NotNull(message = "is  required")
    private Double fee;
}
