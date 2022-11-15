package com.globalvox.clinicmanagementsystem.entity;

import com.globalvox.clinicmanagementsystem.entity.enums.TypeOfHoliday;
import com.globalvox.clinicmanagementsystem.validator.LeaveDateCompare;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Getter
@Setter
//@LeaveDateCompare(first = "fromDate", second = "toDate", message = "Enter Valid Date")
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(value = TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date date;

//    @Temporal(value = TemporalType.DATE)
//    @DateTimeFormat(pattern = "dd/MM/yyyy")
//    private Date toDate;

    @NotBlank(message = "Please Enter holiday name!")
    private String name;

    @Enumerated(EnumType.STRING)
    private TypeOfHoliday type;

//    private int days;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}
