package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.entity.enums.StaffMembers;
import com.globalvox.clinicmanagementsystem.validator.DateCompare;
import com.globalvox.clinicmanagementsystem.validator.ValidEmail;
import com.globalvox.clinicmanagementsystem.validator.ValidMobileNumber;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Data
@DateCompare(first = "startWorkTime",second = "endWorkTime", message = "End work time must be greater")
public class StaffMemberCreationDTO {

    private Long id;

    @Size(max = 50)
    @NotBlank(message = "is required")
    private String firstName;

    @Size(max = 50)
    @NotBlank(message = "is required")
    private String lastName;

    @Size(max = 12)
    @NotBlank(message = "is required")
    @ValidMobileNumber
    private String mobileNumber;

    @NotBlank(message = "is required")
    @ValidEmail
    private String email;

    @Min(value = 0,message = "Enter valid value")
    @NotNull(message = "is required")
    private Double salary;

    @Size(max = 300, message = "Address length should not exceed 300 characters")
    private String address;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Member Role cannot be null")
    private StaffMembers memberRole;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please Select Gender")
    private Gender gender;

    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "h:mm a")
    @NotNull(message = "is required")
    private Date startWorkTime;

    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "h:mm a")
    private Date endWorkTime;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "is required")
    private Date joiningDate;

}
