package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;

public interface PatientRepository extends BaseUserRepository<Patient> {
    Patient findByEmail(String email);

    Patient findByMobileNumber(String mobileNumber);

    int countByEmail(String email);

    int countByMobileNumber(String mobileNumber);

    int countById(Long id);

    Patient findByVerificationCode(String code);

    int countByEmailAndIdNot(String email,Long id);

    int countByMobileNumberAndIdNot(String mobileNumber,Long id);

    Patient findByResetPasswordToken(String token);

    long count();
}
