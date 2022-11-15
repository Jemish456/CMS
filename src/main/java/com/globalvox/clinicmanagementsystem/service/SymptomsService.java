package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Symptoms;

import java.util.List;
import java.util.Optional;

public interface SymptomsService {

    Symptoms findById(long symptomsId);

    List<Symptoms> findAll();

    void save(Symptoms symptoms);

    Optional<Symptoms> findById(Long symptomsId);

    void deleteById(Long symptomsId);

    List<Symptoms> findByAppointment(Appointment appointment);
}
