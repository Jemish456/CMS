package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

    @Override
    Appointment save(Appointment appointment);

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findAllByStatusAndDoctor(AppointmentStatus appointmentStatus,Doctor doctor);

    void deleteById(Long id);

    int countAppointmentsByStartDateTimeBeforeAndEndDateTimeAfterAndDoctorAndStatus(Date endDateTime, Date startDateTime, Doctor doctor, AppointmentStatus appointmentStatus);

    Appointment findByPatient(Patient patient);

    List<Appointment> findAllByStartDateTimeLessThan(Date date);

    long countAllByStatus(AppointmentStatus status);

    Optional<Appointment> findById(Long id);

    List<Appointment> findAllByPatientId(Long id);

    @Query("select a FROM Appointment a where status = 'PENDING' AND doctor_id = :doctorId and" +
            " CONCAT(a.patient.firstName,' ',a.patient.lastName,' ',a.startDateTime) LIKE %:keywords%")
    Page<Appointment> search(@Param("doctorId")Long doctorId, @Param("keywords") String keywords, Pageable pageable);

}
