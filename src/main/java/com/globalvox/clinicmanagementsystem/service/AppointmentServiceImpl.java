package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.EMail;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public Optional<Appointment> findById(Long appointmentId) {
        Optional<Appointment> result = appointmentRepository.findById(appointmentId);
        return result;
    }

    @Override
    public List<Appointment> findAllByPatientId(Long id) {
        return appointmentRepository.findAllByPatientId(id);
    }

    @Override
    public void deleteById(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    @Override
    public Appointment findByPatient(Patient patient) {
        return appointmentRepository.findByPatient(patient);
    }

    @Override
    public List<Appointment> findByDoctor(Doctor doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }

    @Override
    public List<Appointment> findAllByStatusAndDoctor(AppointmentStatus appointmentStatus, Doctor doctor) {
        return appointmentRepository.findAllByStatusAndDoctor(appointmentStatus, doctor);
    }

    /*
     * When appointment is Approved by the Doctor,
     * send approval mail to the patient
     * */
    @Override
    public void sendAppointmentApprovedMail(Appointment appointment) throws MessagingException, UnsupportedEncodingException {

        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();
        String patientEmail = patient.getEmail();
        EMail eMail = new EMail();

        eMail.setFrom("vibhutitemp@gmail.com");
        eMail.setTo(patientEmail);
        eMail.setSubject("Appointment Approved");
        eMail.setTemplate("email/appointment-approved.html");

        Map<String, Object> properties = new HashMap<>();
        properties.put("doctorName", doctor.getFirstName() + ' ' + doctor.getMiddleName() + ' ' + doctor.getLastName());
        properties.put("startAt", appointment.getStartDateTime());
        properties.put("endAt", appointment.getEndDateTime());
        properties.put("description", appointment.getDescription());
        eMail.setProperties(properties);

        emailSenderService.sendHtmlMessage(eMail);
    }

    /*
     * When appointment is created successfully,
     * send notifying mail to the patient
     * */
    @Override
    public void sendAppointmentCreatedMail(Appointment appointment) throws MessagingException, UnsupportedEncodingException {
        Doctor doctor = appointment.getDoctor();
        Patient patient = appointment.getPatient();
        String patientEmail = patient.getEmail();
        EMail eMail = new EMail();

        eMail.setFrom("vibhutitemp@gmail.com");
        eMail.setTo(patientEmail);
        eMail.setSubject("Appointment Created");
        eMail.setTemplate("email/appointment-created.html");

        Map<String, Object> properties = new HashMap<>();
        properties.put("doctorName", doctor.getFirstName() + ' ' + doctor.getMiddleName() + ' ' + doctor.getLastName());
        properties.put("startAt", appointment.getStartDateTime());
        properties.put("endAt", appointment.getEndDateTime());
        properties.put("description", appointment.getDescription());
        eMail.setProperties(properties);

        emailSenderService.sendHtmlMessage(eMail);
    }

    //give the numbers of approved appointment
    @Override
    public long countAllByApprovedStatus() {
        long totalApprovedAppointment = appointmentRepository.countAllByStatus(AppointmentStatus.APPROVED);
        return totalApprovedAppointment;
    }

    //give the numbers of pending appointment
    @Override
    public long countAllByPendingStatus() {
        long totalPendingAppointment = appointmentRepository.countAllByStatus(AppointmentStatus.PENDING);
        return totalPendingAppointment;
    }

    @Override
    public Page<Appointment> findByKeyword(String keyword, Doctor doctor, Pageable pageable) {
        Long doctorId = doctor.getId();
        return appointmentRepository.search(doctorId, keyword, pageable);
    }

    @Override
    public List<Appointment> findAllByStartDateTimeLessThan(Date date) {
        return appointmentRepository.findAllByStartDateTimeLessThan(date);
    }

    @Override
    public long countAllByClosedStatus() {
        long totalClosedAppointment = appointmentRepository.countAllByStatus(AppointmentStatus.CLOSED);
        return totalClosedAppointment;
    }
}

