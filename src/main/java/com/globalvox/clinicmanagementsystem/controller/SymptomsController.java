package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.dto.PrescriptionClinicServiceCreationDTO;
import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.Prescription;
import com.globalvox.clinicmanagementsystem.entity.Symptoms;
import com.globalvox.clinicmanagementsystem.entity.enums.Severity;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.repository.ClinicServicesRepository;
import com.globalvox.clinicmanagementsystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/symptoms")
public class SymptomsController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private SymptomsService symptomsService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionDrugService prescriptionDrugService;

    @Autowired
    private PrescriptionClinicService prescriptionClinicService;

    @Autowired
    private ClinicServicesRepository clinicServicesRepository;


    /* Symptoms CRUD Operation */

    /*
    * Add new Symptoms for particular appointment, used modal in front-end (pop-up)
    * Both doctor and patient has authority
    * */
    @GetMapping("/add")
    public String loadAddSymptoms(@RequestParam(name = "appointmentId") Long appointmentId, Model model){
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
        if(appointment.isPresent()){
            Symptoms symptoms = new Symptoms();
            model.addAttribute("symptoms",symptoms);
            model.addAttribute("severity", Severity.values());
            List<Symptoms> symptomsList = symptomsService.findByAppointment(appointment.get());
            model.addAttribute("symptomList", symptomsList);
            model.addAttribute("appointment",appointment);
        } else {
            throw new NotFoundException("Appointment Not found.");
        }
        return "appointment/view-appointment";
    }

    @PostMapping("/add")
    public String addSymptoms(@Valid @ModelAttribute("symptoms")Symptoms symptoms, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model){
        if(bindingResult.hasErrors()){
            Appointment appointment=symptoms.getAppointment();
            Optional<Prescription> prescription = prescriptionService.findByAppointmentId(appointment.getId());
            model.addAttribute("appointment", appointment);
            model.addAttribute("doctor", appointment.getDoctor());
            model.addAttribute("patient", appointment.getPatient());
            model.addAttribute("symptomList", symptomsService.findByAppointment(appointment));
            model.addAttribute("severity", Severity.values());
            if (prescription.isPresent()) {
                model.addAttribute("prescription", prescription.get());
                model.addAttribute("prescriptionDrugList", prescriptionDrugService.findAllByPrescriptionId(prescription.get().getId()));
            }
            List<String> prescriptionServiceList = prescriptionClinicService.getPrescriptionServiceNames(appointment);
            List<String> clinicServiceList = clinicServicesRepository.getServiceNames();
            clinicServiceList.removeAll(prescriptionServiceList);
            model.addAttribute("prescriptionClinicService", new PrescriptionClinicServiceCreationDTO());
            model.addAttribute("prescriptionServiceList", prescriptionClinicService.findByAppointment(appointment));
            model.addAttribute("clinicServiceList", clinicServiceList);
            return "appointment/view-appointment";
        }
        symptomsService.save(symptoms);
        redirectAttributes.addAttribute("appointmentId",symptoms.getAppointment().getId());
        return "redirect:/appointment/view";
    }

    /* Edit symptoms: Both doctor and patient has authority */
    @GetMapping("/edit")
    public String loadEditSymptoms(@RequestParam("symptomsId") Long symptomsId, Model model) {
        Optional<Symptoms> symptoms = symptomsService.findById(symptomsId);
        if (symptoms.isPresent()) {
            model.addAttribute("symptoms",symptoms.get());
            model.addAttribute("severity", Severity.values());
        } else {
            throw new NotFoundException("Symptom Not found!");
        }
        return "symptoms/edit-symptoms";
    }

    @PostMapping("/edit")
    public String editSymptom(@Valid @ModelAttribute("symptoms")Symptoms symptoms,BindingResult bindingResult, RedirectAttributes redirectAttributes,Model model) {
        if(bindingResult.hasErrors()){
            model.addAttribute("severity", Severity.values());
            return "symptoms/edit-symptoms";
        }
        symptomsService.save(symptoms);
        redirectAttributes.addAttribute("appointmentId", symptoms.getAppointment().getId());
        return "redirect:/appointment/view";
    }

    /* Delete Symptoms, Both doctor and patient has authority */
    @RequestMapping("/delete")
    public String deleteSymptoms(@RequestParam("symptomsId") Long symptomsId,RedirectAttributes redirectAttributes) {
        Optional<Symptoms> symptoms = symptomsService.findById(symptomsId);
        if(symptoms.isPresent()) {
            symptomsService.deleteById(symptomsId);
        } else {
            throw new NotFoundException("Symptom not found!");
        }
        redirectAttributes.addAttribute("appointmentId",symptoms.get().getAppointment().getId());
        return "redirect:/appointment/view";
    }
}
