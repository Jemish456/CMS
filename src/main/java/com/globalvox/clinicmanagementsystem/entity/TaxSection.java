package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import javax.validation.constraints.Max;

@Data
@Entity
public class TaxSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private StaffMember staffMember;

    private String taxSystem;

    private int financialYear;

    private double standardDeduction;

    private double professionalTax;

    private double section80C;

    private double section24B;

    private double section80D;

    private double taxAbleIncome;
}

