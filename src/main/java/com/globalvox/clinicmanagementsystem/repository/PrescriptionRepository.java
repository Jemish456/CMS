package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {
//    List<Prescription> findByPatient(Patient patient);
//    List<Prescription> findByDoctorAndPatient(Doctor doctor,Patient patient);
    List<Prescription> findByAppointment(Appointment appointment);
    Optional<Prescription> findById(Long prescriptionId);

    @Override
    Prescription  save(Prescription prescription);

    Optional<Prescription> findByAppointmentId(Long appointmentId);
    //    List<Prescription> findByDoctorAndPatientOrderBOrderByUpdatedAt(Doctor doctor,Patient patient);
}
