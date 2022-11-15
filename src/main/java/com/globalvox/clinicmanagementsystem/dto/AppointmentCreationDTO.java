package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.Symptoms;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AppointmentCreationDTO {

    private Long id;

    private Patient patient;

    private Doctor doctor;

    private String description;

    @Temporal(value= TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @Temporal(value = TemporalType.TIME)
    @DateTimeFormat(pattern = "h:mm a")
    @NotNull(message = "Please select slot")
    private Date startTime;

    private List<Symptoms> symptomsList = new ArrayList<>();

}
