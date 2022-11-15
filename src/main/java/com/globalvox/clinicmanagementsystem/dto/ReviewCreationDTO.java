package com.globalvox.clinicmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ReviewCreationDTO {

    private Long id;

    @Min(value = 1,message = "Ratings must be greater than or equal to 1 star")
    private Double rating;

    @Length(max = 500,message = "Size should not be more than 500")
    private String description;
}
