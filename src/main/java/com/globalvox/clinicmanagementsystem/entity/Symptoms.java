package com.globalvox.clinicmanagementsystem.entity;

import com.globalvox.clinicmanagementsystem.entity.enums.Severity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
public class Symptoms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "is required")
    private String name;

    private String bodyPart;

//    @Enumerated(EnumType.STRING)
//    private Severity severity;

    private String severity;

    @ManyToOne
    @JoinColumn(name = "appointment_id",referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Appointment appointment;

}
