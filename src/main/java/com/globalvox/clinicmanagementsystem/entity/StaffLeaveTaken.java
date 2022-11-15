package com.globalvox.clinicmanagementsystem.entity;

import com.globalvox.clinicmanagementsystem.entity.enums.LeaveType;
import com.globalvox.clinicmanagementsystem.validator.LeaveDateCompare;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@LeaveDateCompare(first = "leaveFromDate",second = "leaveToDate",message = "Enter Valid Date")
public class StaffLeaveTaken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StaffMember staffMember;

    @Temporal(value=TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Future
    private Date leaveFromDate;

    @Temporal(value=TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date leaveToDate;

    @NotBlank(message = "Please Enter Description")
    private String description;

    @Min(value = 0, message = "Days can not be negative")
    private int days;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @NotNull(message = "Please Select Leave Type")
    @Enumerated(EnumType.STRING)

    private LeaveType leaveType;
}
