package com.globalvox.clinicmanagementsystem.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.entity.enums.StaffMembers;
import com.globalvox.clinicmanagementsystem.validator.DateCompare;
import com.globalvox.clinicmanagementsystem.validator.ValidEmail;
import com.globalvox.clinicmanagementsystem.validator.ValidMobileNumber;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class StaffMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String mobileNumber;

    @Column(unique = true)
    private String email;

    private Double salary;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    private StaffMembers memberRole;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "h:mm a")
    private Date startWorkTime;

    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "h:mm a")
    private Date endWorkTime;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date joiningDate;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(mappedBy = "staffMember")
    @JsonManagedReference(value = "staffMember")
    private List<StaffSalary> staffSalaryList;

}
