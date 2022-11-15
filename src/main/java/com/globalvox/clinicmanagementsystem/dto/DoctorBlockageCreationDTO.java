package com.globalvox.clinicmanagementsystem.dto;

import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.validator.DateCompare;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import java.util.Date;

@Data
@DateCompare(first = "startDateTime",second = "endDateTime")
public class DoctorBlockageCreationDTO {

    private Long id;

    private Doctor doctor;

    @Temporal(value= TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy, h:mm a")
    @Future(message = "must be future date")
    private Date startDateTime;

    @Temporal(value= TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "dd/MM/yyyy, h:mm a")
//    @Future(message = "must be future date")
    private Date endDateTime;

}
