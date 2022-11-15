package com.globalvox.clinicmanagementsystem.dto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Getter
@Setter
public class StaffLeaveManageCreationDTO {

    private Long id;

    @Min(value = 0,message = "Please Enter Positive Value")
    private double paidLeaves;

    @Min(value = 1,message = "Enter value between 1 to 12")
    @Max(value = 12,message = "Enter value between 1 to 12")
    private int carryForwardExpiry;

    @Temporal(value= TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Future(message = "Must be a future date")
    private Date effectiveDate;
}
