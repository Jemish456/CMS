package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.validator.ValidEmail;
import com.globalvox.clinicmanagementsystem.validator.ValidMobileNumber;
import com.globalvox.clinicmanagementsystem.entity.enums.BloodGroup;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
public class PatientUpdateDTO {

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

    @Basic
    @Temporal(value=TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

    @DecimalMin("1.00")
    private Float height;

    @DecimalMin("1.00")
    private Float weight;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    private String allergy;
}
