package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.persistence.*;

@Data
@Entity
public class StandardDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  long id;

    double standardDeduction;

    @OneToOne
    private TaxSection taxSection;
}
