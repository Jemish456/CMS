package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.PrescriptionClinicServices;

import java.util.List;
import java.util.Optional;

public interface PrescriptionClinicService {
    List<PrescriptionClinicServices> findByAppointment(Appointment appointment);
    PrescriptionClinicServices findByServiceName(String name);
    void save(PrescriptionClinicServices services);
    void deleteById(Long id);
    Optional<PrescriptionClinicServices> findById(Long id);
    List<String> getPrescriptionServiceNames(Appointment appointment);
    List<PrescriptionClinicServices> findAllByAppointmentId(Long id);
}
