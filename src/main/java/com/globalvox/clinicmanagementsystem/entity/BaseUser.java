package com.globalvox.clinicmanagementsystem.entity;

import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.validator.Password;
import lombok.Data;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
public class BaseUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    private String mobileNumber;

    @Column(nullable = true)
    private String profilePhoto;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Temporal(value=TemporalType.DATE)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfBirth;

//    @Column(columnDefinition = "TEXT")
//    @Size(max = 500, message = "Description length should not exceed 500 characters")
    private String addressLine1;

    private String addressLine2;


    @Column(length = 64)
    private String verificationCode;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }


    @Column
    private boolean enable = false;

    @CreationTimestamp
    @Column(updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @Column
    private String resetPasswordToken;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "base_user_role",
            joinColumns = @JoinColumn(name = "base_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<Role> roles;

}