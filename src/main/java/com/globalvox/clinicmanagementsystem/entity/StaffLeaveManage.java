package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class StaffLeaveManage {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double paidLeaves;

    private int carryForwardExpiry;

    @Temporal(value=TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date effectiveDate;

    private int year;

    private boolean active;
}
