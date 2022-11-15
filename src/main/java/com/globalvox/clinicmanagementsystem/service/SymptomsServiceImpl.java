package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Symptoms;
import com.globalvox.clinicmanagementsystem.repository.SymptomsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SymptomsServiceImpl implements SymptomsService{

    @Autowired
    private SymptomsRepository symptomsRepository;

    @Override
    public Symptoms findById(long symptomsId) {
        return null;
    }

    @Override
    public List<Symptoms> findAll() {
        return symptomsRepository.findAll();
    }

    @Override
    public void save(Symptoms symptoms) {
        symptomsRepository.save(symptoms);
    }

    @Override
    public Optional<Symptoms> findById(Long symptomsId) {
        Optional<Symptoms> result = symptomsRepository.findById(symptomsId);
        return result;
    }

    @Override
    public void deleteById(Long symptomsId) {
        symptomsRepository.deleteById(symptomsId);
    }

    @Override
    public List<Symptoms> findByAppointment(Appointment appointment) {
        return symptomsRepository.findByAppointment(appointment);
    }


}
