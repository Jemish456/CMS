package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.repository.PrescriptionDrugRepository;
import com.globalvox.clinicmanagementsystem.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Override
    public Optional<Prescription> findById(Long prescriptionId) {
        return prescriptionRepository.findById(prescriptionId);
    }

    @Override
    public Optional<Prescription> findByAppointmentId(long appointmentId) {
        return prescriptionRepository.findByAppointmentId(appointmentId);
    }

    @Override
    public Prescription savePrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }
}
