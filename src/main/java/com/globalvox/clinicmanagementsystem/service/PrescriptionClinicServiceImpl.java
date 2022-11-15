package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.PrescriptionClinicServices;
import com.globalvox.clinicmanagementsystem.repository.PrescriptionClinicServicesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionClinicServiceImpl implements PrescriptionClinicService {

    @Autowired
    private PrescriptionClinicServicesRepository prescriptionClinicServicesRepository;

    @Override
    public void save(PrescriptionClinicServices services) {
        prescriptionClinicServicesRepository.save(services);
    }

    @Override
    public void deleteById(Long id) {
        prescriptionClinicServicesRepository.deleteById(id);
    }

    @Override
    public Optional<PrescriptionClinicServices> findById(Long id) {
        return prescriptionClinicServicesRepository.findById(id);
    }

    @Override
    public List<String> getPrescriptionServiceNames(Appointment appointment) {
        return prescriptionClinicServicesRepository.getPrescriptionServiceNames(appointment);
    }

    @Override
    public List<PrescriptionClinicServices> findByAppointment(Appointment appointment) {
        return prescriptionClinicServicesRepository.findByAppointment(appointment);
    }

    @Override
    public PrescriptionClinicServices findByServiceName(String name) {
        return prescriptionClinicServicesRepository.findByServiceName(name);
    }

    @Override
    public List<PrescriptionClinicServices> findAllByAppointmentId(Long id) {
        return prescriptionClinicServicesRepository.findAllByAppointmentId(id);
    }
}
