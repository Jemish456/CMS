package com.globalvox.clinicmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globalvox.clinicmanagementsystem.entity.enums.BloodGroup;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Patient extends BaseUser {

    private Float height;

    private Float weight;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    @Column
    private String allergy;

    @OneToMany(mappedBy = "patient",cascade = CascadeType.ALL)
    @JsonManagedReference(value = "patient")
    private List<Review> reviewList;

}
