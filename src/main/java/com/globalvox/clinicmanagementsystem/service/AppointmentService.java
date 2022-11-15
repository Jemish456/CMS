package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AppointmentService  {

    List<Appointment> findAll();

    Appointment save(Appointment appointment);

    Optional<Appointment> findById(Long appointmentId);

    List<Appointment> findAllByPatientId(Long id);

    void deleteById(Long appointmentId);

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findAllByStatusAndDoctor(AppointmentStatus appointmentStatus,Doctor doctor);

    Appointment findByPatient(Patient patient);

    void sendAppointmentApprovedMail(Appointment appointment) throws MessagingException, UnsupportedEncodingException;

    void sendAppointmentCreatedMail(Appointment appointment) throws MessagingException, UnsupportedEncodingException;

    long countAllByApprovedStatus();

    long countAllByPendingStatus();

    Page<Appointment> findByKeyword(String keyword, Doctor doctor, Pageable pageable);

    List<Appointment> findAllByStartDateTimeLessThan(Date date);

    long countAllByClosedStatus();
}
