package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.dto.AppointmentCreationDTO;
import com.globalvox.clinicmanagementsystem.dto.PrescriptionClinicServiceCreationDTO;
import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.CustomUser;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.entity.enums.Severity;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.repository.ClinicServicesRepository;
import com.globalvox.clinicmanagementsystem.repository.InvoiceRepository;
import com.globalvox.clinicmanagementsystem.service.*;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import com.lowagie.text.Row;
import lombok.var;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SymptomsService symptomsService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionDrugService prescriptionDrugService;

    @Autowired
    private ClinicServicesRepository clinicServicesRepository;

    @Autowired
    private PrescriptionClinicService prescriptionClinicService;

    @Autowired
    private PdfGenerateService pdfGenerateService;

    @Autowired
    private ServiceUtil serviceUtil;

    @Value("${pdf.directory}")
    private String pdfDirectory;

    /* Book Appointment */
    @GetMapping("/book")
    public String loadBookAppointment(@RequestParam(name = "id") Long id,
                                      Model model) {
        AppointmentCreationDTO appointmentCreationDTO = new AppointmentCreationDTO();

        // request user is doctor or patient
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_DOCTOR"))) {
            Optional<Patient> patient = patientService.findById(id);
            if (patient.isPresent()) {
                appointmentCreationDTO.setPatient(patient.get());
                appointmentCreationDTO.setDoctor(doctorService.findById(ServiceUtil.getAuthUserId()).get());
            }
        } else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_PATIENT"))) {
            Optional<Doctor> doctor = doctorService.findById(id);
            if (doctor.isPresent()) {
                appointmentCreationDTO.setPatient(patientService.findById(ServiceUtil.getAuthUserId()).get());
                appointmentCreationDTO.setDoctor(doctor.get());
            }
        }

        model.addAttribute("severities",Severity.values());
        model.addAttribute("appointment", appointmentCreationDTO);
        return "appointment/book";
    }

    /* At the time of booking appointment if the user is doctor, show him patient list and vice versa */
    @PostMapping("/book")
    public String bookAppointment(@Valid @ModelAttribute("appointment") AppointmentCreationDTO appointmentCreationDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) throws ParseException, MessagingException, UnsupportedEncodingException {

        if (bindingResult.hasErrors()) {
            model.addAttribute("severities",Severity.values());
            return "appointment/book";
        }

        Date startDateTime = ServiceUtil.getDate(appointmentCreationDTO.getDate(), appointmentCreationDTO.getStartTime());

        Date endDateTime = ServiceUtil.addMinutesToDate(appointmentCreationDTO.getDoctor().getSlotTime(), startDateTime);

        Appointment appointment = modelMapper.map(appointmentCreationDTO, Appointment.class);
        appointment.setStartDateTime(startDateTime);
        appointment.setEndDateTime(endDateTime);

        // check if doctor is available or not
        if (doctorService.isDoctorAvailable(appointment.getStartDateTime(),
                appointment.getEndDateTime(),
                appointment.getDoctor())) {
            Appointment newAppointment = appointmentService.save(appointment);

            // send appointment created mail
            appointmentService.sendAppointmentCreatedMail(newAppointment);
            redirectAttributes.addAttribute("appointmentId", newAppointment.getId());
            return "redirect:/appointment/view";
        }

        redirectAttributes.addFlashAttribute("appointmentError", "Appointment Already Exist !!");
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("DOCTOR"))) {
            redirectAttributes.addAttribute("id", appointmentCreationDTO.getDoctor().getId());
        } else if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("PATIENT"))) {
            redirectAttributes.addAttribute("id", appointmentCreationDTO.getPatient().getId());
        }
        return "redirect:/appointment/book";
    }

    @PostMapping(value="/book", params={"addSymptoms"})
    public String addSymptoms(@ModelAttribute("appointment") AppointmentCreationDTO appointmentCreationDTO, BindingResult bindingResult,Model model) {
        appointmentCreationDTO.getSymptomsList().add(new Symptoms());
        model.addAttribute("appointment",appointmentCreationDTO);
        model.addAttribute("severities",Severity.values());
        return "appointment/book";
    }

    @PostMapping(value="/book", params={"removeSymptoms"})
    public String removeSymptoms(@ModelAttribute("appointment") AppointmentCreationDTO appointmentCreationDTO, BindingResult bindingResult,
                                 final HttpServletRequest req,Model model) {
        final Integer symptomId = Integer.valueOf(req.getParameter("removeSymptoms"));
        appointmentCreationDTO.getSymptomsList().remove(symptomId.intValue());
        model.addAttribute("severities",Severity.values());
        return "appointment/book";
    }

    /* Show particular patient appointment list*/
    @RequestMapping("/details")
    public String loadAppointmentDetails(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        Long patientId = customUser.getId();

        Optional<Patient> patient = patientService.findById(patientId);
        if (patient.isPresent()) {
            model.addAttribute("appointments", appointmentService.findAllByPatientId(patientId));
        }
        return "appointment/my-appointments";
    }

    /* Show pending appointment list for doctor */
    @RequestMapping("/pending/list")
    public String loadAppointmentPendingList(Model model) {

        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        List<Appointment> appointmentList = appointmentService.findAllByStatusAndDoctor(AppointmentStatus.PENDING, doctor.get());
        model.addAttribute("appointments", appointmentList);
        model.addAttribute("status", "PENDING");
        return "appointment/appointment-list";
    }

    /* Show approved appointment list for doctor */
    @RequestMapping("/approved/list")
    public String loadAppointmentApprovedList(Model model) {
        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        List<Appointment> appointmentList = appointmentService.findAllByStatusAndDoctor(AppointmentStatus.APPROVED, doctor.get());
        model.addAttribute("appointments", appointmentList);
        return "appointment/appointment-list";
    }

    /* Show closed appointment list for doctor */
    @RequestMapping("/closed/list")
    public String loadAppointmentClosedList(Model model) {
        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        List<Appointment> appointmentList = appointmentService.findAllByStatusAndDoctor(AppointmentStatus.CLOSED, doctor.get());
        model.addAttribute("appointments", appointmentList);
        return "appointment/appointment-list";
    }

    @RequestMapping("/delete")
    public String deleteAppointment(@RequestParam("id") Long id,@RequestParam("status")AppointmentStatus appointmentStatus) {
        Optional<Appointment> appointment=appointmentService.findById(id);
        appointment.get().setStatus(AppointmentStatus.CLOSED);
        appointmentService.save(appointment.get());
        if(appointmentStatus.equals(AppointmentStatus.PENDING)){
            return "redirect:/appointment/pending/list";
        }else {
            return "redirect:/appointment/approved/list";
        }
    }

    @RequestMapping("/approve")
    public String approveAppointment(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) throws MessagingException, UnsupportedEncodingException {
        Optional<Appointment> appointment = appointmentService.findById(id);
        if (doctorService.isDoctorAvailable(appointment.get().getStartDateTime(), appointment.get().getEndDateTime(), appointment.get().getDoctor())) {
            appointment.get().setStatus(AppointmentStatus.APPROVED);
            appointmentService.save(appointment.get());
//            send mail from here
            appointmentService.sendAppointmentApprovedMail(appointment.get());

            return "redirect:/appointment/pending/list";
        }
        redirectAttributes.addFlashAttribute("appointmentError", "Appointment Already Approved in this Slot");
        return "redirect:/appointment/pending/list";
    }


    /*
     * Viewing appointment Details:
     * Doctor, Patient name,
     * timing and date,
     * Symptoms listing and CRUD operation
     * Prescription CRUD
     * Clinic Service
     */
    @RequestMapping("/view")
    public String loadAppointment(@RequestParam("appointmentId") Long appointmentId, Model model    ) {
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
        if (appointment.isPresent()) {
            Optional<Prescription> prescription = prescriptionService.findByAppointmentId(appointmentId);
            model.addAttribute("appointment", appointment.get());
            model.addAttribute("doctor", appointment.get().getDoctor());
            model.addAttribute("patient", appointment.get().getPatient());
            model.addAttribute("symptomList", symptomsService.findByAppointment(appointment.get()));
            Symptoms symptoms = new Symptoms();
            symptoms.setAppointment(appointment.get());
            model.addAttribute("symptoms", symptoms);
            model.addAttribute("severity", Severity.values());
            if (prescription.isPresent()) {
                model.addAttribute("prescription", prescription.get());
                model.addAttribute("prescriptionDrugList", prescriptionDrugService.findAllByPrescriptionId(prescription.get().getId()));
            }
            List<String> prescriptionServiceList = prescriptionClinicService.getPrescriptionServiceNames(appointment.get());
            List<String> clinicServiceList = clinicServicesRepository.getServiceNames();
            clinicServiceList.removeAll(prescriptionServiceList);
            model.addAttribute("prescriptionClinicService", new PrescriptionClinicServiceCreationDTO());
            model.addAttribute("prescriptionServiceList", prescriptionClinicService.findByAppointment(appointment.get()));
            model.addAttribute("clinicServiceList", clinicServiceList);
        }
        return "appointment/view-appointment";
    }

//    /* Generate the invoice */
//    @GetMapping("/invoice/generate")
//    public String loadInvoice(@RequestParam("appointmentId") Long appointmentId, Model model) {
//
//        /* find clinic services list,appointment and invoice */
//        List<PrescriptionClinicServices> servicesList = prescriptionClinicService.findAllByAppointmentId(appointmentId);
//        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
//        Optional<Invoice> invoice = invoiceRepository.findByAppointmentId(appointmentId);
//
//        /* count the cost of clinic services */
//        Double totalAmount = 0.0;
//        for (PrescriptionClinicServices clinicServices : servicesList)
//            totalAmount += clinicServices.getCost();
//
//        /* if invoice is already present then update total amount */
//        if (invoice.isPresent()) {
//            invoice.get().setTotalAmount(totalAmount + appointment.get().getDoctor().getFee());
//            model.addAttribute("services", servicesList);
//            model.addAttribute("invoice", invoice.get());
//            return "invoice/invoice";
//        }
//
//        /* otherwise create new invoice */
//        Invoice newInvoice = new Invoice();
//        newInvoice.setTotalAmount(totalAmount + appointment.get().getDoctor().getFee());
//        newInvoice.setAppointment(appointment.get());
//        newInvoice.setDoctor(appointment.get().getDoctor());
//        newInvoice.setPatient(appointment.get().getPatient());
//        invoiceRepository.save(newInvoice);
//        model.addAttribute("services", servicesList);
//        model.addAttribute("invoice", newInvoice);
//        return "invoice/invoice";
//    }

    /* Download Prescription in Pdf format */
    @RequestMapping(value = "/prescription/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadPrescriptionPdfFile(@RequestParam("appointmentId") Long appointmentId) throws IOException {
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
        Optional<Prescription> prescription = prescriptionService.findByAppointmentId(appointmentId);
        List<PrescriptionDrug> drugList = prescriptionDrugService.findAllByPrescriptionId(prescription.get().getId());
        Map<String, Object> data = new HashMap<>();
        data.put("prescription", prescription.get());
        data.put("drugList", drugList);
        data.put("appointment", appointment.get());
        File prescriptionFile = pdfGenerateService.generatePdfFile("prescription/prescription-pdf", data, prescription.get().getId() + ".pdf");
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + prescriptionFile.getName());
        byte[] bytes = Files.readAllBytes(Paths.get(prescriptionFile.getPath()));
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    /* Download Invoice in Pdf format */
    @RequestMapping(value = "/invoice/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> downloadInvoicePdfFile(@RequestParam("appointmentId") Long appointmentId, Model model) throws IOException {
        List<PrescriptionClinicServices> servicesList = prescriptionClinicService.findAllByAppointmentId(appointmentId);
        Optional<Invoice> invoice = invoiceRepository.findByAppointmentId(appointmentId);
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
        boolean flag = true;

        /* count the cost of clinic services */
        Double totalAmount = 0.0;
        for (PrescriptionClinicServices clinicServices : servicesList)
            totalAmount += clinicServices.getCost();

        /* if invoice is already present then update total amount */
        if (invoice.isPresent()) {
            invoice.get().setTotalAmount(totalAmount + appointment.get().getDoctor().getFee());
            model.addAttribute("services", servicesList);
            model.addAttribute("invoice", invoice.get());
            invoiceRepository.save(invoice.get());
            flag = false;
        }

        /* otherwise create new invoice */
        if (flag) {
            invoice = Optional.of(new Invoice());
            invoice.get().setTotalAmount(totalAmount + appointment.get().getDoctor().getFee());
            invoice.get().setAppointment(appointment.get());
            invoice.get().setDoctor(appointment.get().getDoctor());
            invoice.get().setPatient(appointment.get().getPatient());
            invoiceRepository.save(invoice.get());
        }

        Map<String, Object> data = new HashMap<>();
        data.put("services", servicesList);
        data.put("invoice", invoice.get());
        File invoiceFile = pdfGenerateService.generatePdfFile("invoice/invoice-pdf", data, invoice.get().getId() + ".pdf");
        var headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + invoiceFile.getName());
        byte[] bytes = Files.readAllBytes(Paths.get(invoiceFile.getPath()));
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(bytes);
    }

    /* If you want to close approved appointment*/
    @RequestMapping("/close")
    public String setAppointmentStatusClose(@RequestParam("appointmentId") Long appointmentId) {
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);

        /* if appointment not found then throw error */
        if (!appointment.isPresent()) {
            throw new NotFoundException("Appointment Not Found");
        }

        /* otherwise set appointment status close*/
        appointment.get().setStatus(AppointmentStatus.CLOSED);
        appointmentService.save(appointment.get());
        return "redirect:/appointment/approved/list";
    }

    /* Download the invoice */
//    @PostMapping("/invoice/download")
//    public String downloadInvoice(@RequestParam("appointmentId") Long appointmentId) {
//        List<PrescriptionClinicServices> servicesList = prescriptionClinicService.findAllByAppointmentId(appointmentId);
//        Optional<Invoice> invoice = invoiceRepository.findByAppointmentId(appointmentId);
//        Map<String, Object> data = new HashMap<>();
//        data.put("services", servicesList);
//        data.put("invoice", invoice.get());
//        pdfGenerateService.generatePdfFile("invoice/invoice-pdf", data, invoice.get().getId() + ".pdf");
//        return "redirect:/appointment/approved/list";
//    }
//
//    /* Doctor can download the prescription */
//    @GetMapping("/prescription/download")
//    public String loadPrescription(@RequestParam("appointmentId") Long appointmentId, RedirectAttributes redirectAttributes) {
//        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
//        Optional<Prescription> prescription = prescriptionService.findByAppointmentId(appointmentId);
//        List<PrescriptionDrug> drugList = prescriptionDrugService.findAllByPrescriptionId(prescription.get().getId());
//        Map<String, Object> data = new HashMap<>();
//        data.put("prescription", prescription.get());
//        data.put("drugList", drugList);
//        data.put("appointment", appointment.get());
//        redirectAttributes.addAttribute("appointmentId", appointmentId);
//        File prescriptionFile = pdfGenerateService.generatePdfFile("prescription/prescription-pdf", data, prescription.get().getId() + ".pdf");
//        return "redirect:/appointment/view";
//    }

}
