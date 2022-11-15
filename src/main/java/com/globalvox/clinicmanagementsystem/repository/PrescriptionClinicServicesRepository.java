package com.globalvox.clinicmanagementsystem.repository;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.PrescriptionClinicServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionClinicServicesRepository extends JpaRepository<PrescriptionClinicServices,Long> {
    List<PrescriptionClinicServices> findByAppointment(Appointment appointment);
    PrescriptionClinicServices findByServiceName(String name);
    void deleteById(Long id);
    Optional<PrescriptionClinicServices> findById(Long id);

    @Query("select serviceName from PrescriptionClinicServices where appointment=:appointment")
    List<String> getPrescriptionServiceNames(Appointment appointment);

    List<PrescriptionClinicServices> findAllByAppointmentId(Long id);
}
