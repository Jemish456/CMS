package com.globalvox.clinicmanagementsystem.entity;

import lombok.Data;

import javax.persistence.*;
import javax.print.Doc;
import java.util.List;

@Entity
@Data
public class Conversation {


    @Id
    @Column(name = "conversation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "patient_id",referencedColumnName = "id")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id",referencedColumnName = "id")
    private Doctor doctor;


}
