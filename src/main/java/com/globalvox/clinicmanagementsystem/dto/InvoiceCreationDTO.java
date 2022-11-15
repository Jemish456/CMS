package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import lombok.Data;

@Data
public class InvoiceCreationDTO {

    private Long id;

    private Appointment appointment;

    private Doctor doctor;

    private Patient patient;
}
