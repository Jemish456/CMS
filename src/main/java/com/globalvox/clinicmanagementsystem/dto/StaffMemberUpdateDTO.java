package com.globalvox.clinicmanagementsystem.dto;


import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.entity.enums.StaffMembers;
import com.globalvox.clinicmanagementsystem.validator.ValidEmail;
import com.globalvox.clinicmanagementsystem.validator.ValidMobileNumber;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class StaffMemberUpdateDTO {

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

    @Column(unique = true)
    @NotBlank(message = "is required")
    @ValidEmail
    private String email;

    @Size(max = 300, message = "Address length should not exceed 300 characters")
    private String address;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Member Role cannot be null")
    private StaffMembers memberRole;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please Select Gender")
    private Gender gender;
}
