package com.globalvox.clinicmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globalvox.clinicmanagementsystem.entity.enums.Specialization;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CascadeType;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;


@Entity
@Getter
@Setter
public class Doctor extends BaseUser {

    private int yearOfExperience;

    private String specialization;

    private String licence;

    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "h:mm a")
    private Date startWorkTime;

    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "h:mm a")
    private Date endWorkTime;

    private int slotTime;

    private Double fee;

    @OneToMany(mappedBy = "doctor",cascade = javax.persistence.CascadeType.ALL)
    @JsonManagedReference(value = "doctor")
    private List<Review> reviewList;

}
