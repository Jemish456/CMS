package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import lombok.Data;

@Data
public class PrescriptionClinicServiceCreationDTO {

    private Long id;

    private String serviceName;

    private Appointment appointment;
}
