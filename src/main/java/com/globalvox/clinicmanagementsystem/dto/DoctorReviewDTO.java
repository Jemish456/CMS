package com.globalvox.clinicmanagementsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//creating dto to merge doctor data and avg rating
public class DoctorReviewDTO {

    Long id;
    String firstName;
    String lastName;
    String email;
    String mobileNumber;
    Double avgRating;

    public DoctorReviewDTO(Long id, String firstName, String lastName, String email, String mobileNumber, Double avgRating) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.avgRating = avgRating;
    }
}
