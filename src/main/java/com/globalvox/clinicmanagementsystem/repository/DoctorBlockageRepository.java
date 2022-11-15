package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.DoctorBlockage;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorBlockageRepository extends JpaRepository<DoctorBlockage,Long> {
    List<DoctorBlockage> findAllByDoctorId(Long id);
    Optional<DoctorBlockage> findByDoctorIdAndAndStartDateTime(Long id, Date startTime);
    void deleteById(Long id);
    int countDoctorBlockageByStartDateTimeBeforeAndEndDateTimeAfterAndDoctorAndStatusIsTrue(Date endDateTime, Date startDateTime, Doctor doctor);
}
