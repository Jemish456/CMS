package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.*;

import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
//    List<Prescription> findAllPrescription();
//
//    void save(Prescription prescription);
//
//    Prescription findById(Long drugId);
//
//    void deleteById(Long drugId);
////
////    List<Prescription> patientWisePrescription(Patient patient,Doctor doctor);
////
////    List<Prescription> doctorWisePrescription(Doctor doctor,Patient patient);
//
//    List<Prescription> findByAppointment(Appointment appointment);

    Optional<Prescription> findById(Long prescriptionId);

    Prescription savePrescription(Prescription prescription);

    Optional<Prescription> findByAppointmentId(long appointmentId);


}
