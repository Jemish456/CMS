package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.validator.*;
import com.globalvox.clinicmanagementsystem.validator.FieldMatch;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.validator.ValidMobileNumber;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Data
@FieldMatch(first = "password1",second ="password",message = "Password and Confirm Password must be match")
@DateCompare(first = "startWorkTime",second = "endWorkTime", message = "End work time must be greater than start work time")
public class DoctorCreationDTO {

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

    private String password1;

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
    @NotNull(message = "Please provide a date.")
    private Date dateOfBirth;

    private int slotTime = 30;

    @DecimalMin(value = "0.00")
    @NotNull(message = "is  required")
    private Double fee;

    @DateTimeFormat(pattern = "h:mm a")
    @Temporal(value=TemporalType.TIME)
    private Date startWorkTime;

    @DateTimeFormat(pattern = "h:mm a")
    @Temporal(value=TemporalType.TIME)
    private Date endWorkTime;

}
