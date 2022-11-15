package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.dto.MessageDTO;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.dto.PrescriptionClinicServiceCreationDTO;
import com.globalvox.clinicmanagementsystem.dto.DoctorBlockageCreationDTO;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.dto.DoctorUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.service.ConversationService;
import com.globalvox.clinicmanagementsystem.repository.ClinicServicesRepository;
import com.globalvox.clinicmanagementsystem.repository.DoctorBlockageRepository;
import com.globalvox.clinicmanagementsystem.service.AppointmentService;
import com.globalvox.clinicmanagementsystem.service.DoctorService;
import com.globalvox.clinicmanagementsystem.service.MessageService;
import com.globalvox.clinicmanagementsystem.service.PatientService;
import com.globalvox.clinicmanagementsystem.service.*;
import com.globalvox.clinicmanagementsystem.service.SymptomsService;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorBlockageRepository doctorBlockageRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SymptomsService symptomsService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionDrugService prescriptionDrugService;

    @Autowired
    private ClinicServicesRepository clinicServicesRepository;

    @Autowired
    private PrescriptionClinicService prescriptionClinicService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    @Value("${profile.photo.path}")
    private String profilePhotoPath;

    @Value("${message.photo.path}")
    private String messagePhotoPath;

    @RequestMapping("")
    public String loadHome(Model model) {
        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        List<Appointment> appointmentList = appointmentService.findAllByStatusAndDoctor(AppointmentStatus.PENDING, doctor.get());
        model.addAttribute("appointments", appointmentList);
        model.addAttribute("status", "PENDING");
        return "doctor/home";
    }

    /*show the doctor list*/
    @RequestMapping("/list")
    public String loadDoctorList(Model model) {
        model.addAttribute("doctors", doctorService.getDoctorAndAvgRating());
        System.out.println(doctorService.getDoctorAndAvgRating());
        return "doctor/list";
    }

    /*show the patient list*/
    @RequestMapping("/patient/list")
    public String loadPatientList(Model model) {
        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        model.addAttribute("doctor", doctor.get());
        model.addAttribute("patients", patientService.findAll());
        return "patient/list";
    }

    /*show the pending list of appointment*/
    @RequestMapping("/appointment/pending/list")
    public String loadAppointmentPendingList(Model model) {

        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        List<Appointment> appointmentList = appointmentService.findAllByStatusAndDoctor(AppointmentStatus.PENDING, doctor.get());
        model.addAttribute("appointments", appointmentList);
        model.addAttribute("status", "PENDING");
        return "appointment/appointment-list";
    }

    @RequestMapping("/appointment/approved/list")
    public String loadAppointmentApprovedList(Model model) {
        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        List<Appointment> appointmentList = appointmentService.findAllByStatusAndDoctor(AppointmentStatus.APPROVED, doctor.get());
        model.addAttribute("appointments", appointmentList);
        return "appointment/appointment-list";
    }

    /*delete the particular appointment*/
    @RequestMapping("/appointment/delete")
    public String deleteAppointment(@RequestParam("id") Long id) {
        appointmentService.deleteById(id);
        return "redirect:/doctor/appointment/pending/list";
    }

    /*show the approved appointment*/
    @RequestMapping("/appointment/approve")
    public String approveAppointment(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Appointment> appointment = appointmentService.findById(id);
        if (doctorService.isDoctorAvailable(appointment.get().getStartDateTime(), appointment.get().getEndDateTime(), appointment.get().getDoctor())) {
            appointment.get().setStatus(AppointmentStatus.APPROVED);
            appointmentService.save(appointment.get());
            return "redirect:/doctor/appointment/pending/list";
        }
        redirectAttributes.addFlashAttribute("appointmentError", "Appointment Already Approved in this Slot");
        return "redirect:/doctor/appointment/pending/list";
    }

    /*show the particular doctor details*/
    @RequestMapping("/view")
    public String loadDoctorDetails(@RequestParam("id") Long doctorID, Model model) {

        Optional<Doctor> doctor = doctorService.findById(doctorID);
        if (!doctor.isPresent()) {
            throw new NotFoundException("User Not Found");
        }
        model.addAttribute("doctor", doctor.get());
        return "doctor/view";
    }

    /*show the particular patient details*/
    @RequestMapping("/patient/view")
    public String loadPatientDetails(@RequestParam("id") Long patientID, Model model) {
        Optional<Patient> patient = patientService.findById(patientID);
        if (!patient.isPresent()) {
            throw new NotFoundException("User Not Found");
        }
        model.addAttribute("patient", patient.get());
        return "patient/view";
    }

    /*edit the particular doctor details*/
    @GetMapping("/edit")
    public String loadEditDoctor(@RequestParam("id") Long doctorID, Model model) {
        Optional<Doctor> doctor = doctorService.findById(doctorID);
        if (!doctor.isPresent()) {
            throw new NotFoundException("User Not Found");
        }
        DoctorUpdateDTO doctorUpdateDTO = modelMapper.map(doctor.get(), DoctorUpdateDTO.class);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("user", doctorUpdateDTO);
        return "doctor/edit";
    }

    @PostMapping("/edit")
    public String editDoctor(@Valid @ModelAttribute("user") DoctorUpdateDTO doctorUpdateDTO, BindingResult bindingResult, Model model, @RequestParam("photo") MultipartFile multipartFile, HttpServletRequest request,RedirectAttributes redirectAttributes) throws MessagingException, IOException {
        Optional<Doctor> doctor = doctorService.findById(doctorUpdateDTO.getId());

        // uploaded image validation
        if (!multipartFile.isEmpty() && (!(multipartFile.getContentType().equals("image/jpeg")) &&
                !(multipartFile.getContentType().equals("image/png")) &&
                !(multipartFile.getContentType().equals("image/jpg")))) {
            bindingResult.addError(new FieldError("patient", "profilePhoto", "Image format is not jpeg,png or jpg"));
        }
        if (doctor.isPresent()) {

            int emailCount = doctorService.countByEmailAndIdNot(doctorUpdateDTO.getEmail(), doctorUpdateDTO.getId()) +
                    patientService.countByEmailAndIdNot(doctorUpdateDTO.getEmail(), doctorUpdateDTO.getId());

            int mobileNumberCount = doctorService.countByMobileNumberAndIdNot(doctorUpdateDTO.getMobileNumber(), doctorUpdateDTO.getId()) +
                    patientService.countByMobileNumberAndIdNot(doctorUpdateDTO.getMobileNumber(), doctorUpdateDTO.getId());

            // email already exists
            if (emailCount > 0) {
                bindingResult.addError(new FieldError("doctor", "email", "Doctor already exist with this email."));
            }

            // phone number already exists
            if (mobileNumberCount > 0) {
                bindingResult.addError(new FieldError("doctor", "mobileNumber", "Doctor already exist with this mobile number."));
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("genders", Gender.values());
                return "doctor/edit";
            }

            modelMapper.map(doctorUpdateDTO, doctor.get());
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            if (fileName == null || fileName.equalsIgnoreCase("")) {
                doctorService.save(doctor.get(), ServiceUtil.getSiteURL(request));
            } else {
                doctor.get().setProfilePhoto(fileName);
                Doctor doctor1 = doctorService.save(doctor.get(), ServiceUtil.getSiteURL(request));

                // upload image
                String uploadDir = profilePhotoPath + doctor1.getId();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (NotFoundException | IOException ex) {
                    throw new NotFoundException("Could Not Save Image ");
                }
            }
            doctorService.save(doctor.get(), ServiceUtil.getSiteURL(request));
            redirectAttributes.addAttribute("id",doctor.get().getId());
            return "redirect:/doctor/view";
        } else {
            throw new NotFoundException("User Not Found");
        }
    }

    /* Blockage */

    @GetMapping("/blockage/add")
    public String loadBlockageForm(Model model) {
        model.addAttribute("doctorBlockage", new DoctorBlockageCreationDTO());
        model.addAttribute("doctorblockage", doctorBlockageRepository.findAllByDoctorId(ServiceUtil.getAuthUserId()));
        return "doctor/add-blockage";
    }

    @PostMapping("/blockage/add")
    public String addBlockage(@Valid @ModelAttribute("doctorBlockage") DoctorBlockageCreationDTO doctorBlockageCreationDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("doctorblockage", doctorBlockageRepository.findAllByDoctorId(ServiceUtil.getAuthUserId()));
            return "doctor/add-blockage";
        }
        Optional<Doctor> doctor = doctorService.findById(ServiceUtil.getAuthUserId());
        DoctorBlockage doctorBlockage = modelMapper.map(doctorBlockageCreationDTO, DoctorBlockage.class);
        doctorBlockage.setDoctor(doctor.get());
        doctorBlockageRepository.save(doctorBlockage);

        redirectAttributes.addFlashAttribute("success", "Blockage Added");
        return "redirect:add";
    }

    @GetMapping("/blockage/delete")
    public String deleteBlockage(@RequestParam("id") Long id) {
        doctorBlockageRepository.deleteById(id);
        return "redirect:add";
    }

    /* Prescription */

    /* Add Description for Particular Prescription */
    @GetMapping("/prescription/description/add")
    public String addDescription(Model model, @RequestParam("appointmentId") Long appointmentId) {
        Optional<Appointment> appointment = appointmentService.findById(appointmentId);
        Optional<Prescription> prescription = prescriptionService.findByAppointmentId(appointmentId);
        model.addAttribute("appointment",appointment.get());
        model.addAttribute("patient",appointment.get().getPatient());
        if (prescription.isPresent()) {
            model.addAttribute("prescription", prescription);
        } else {
            Prescription prescription1 = new Prescription();
            prescription1.setAppointment(appointment.get());
            model.addAttribute("prescription", prescription1);
        }
        return "prescription/add-prescription-description";
    }

    @PostMapping("/prescription/description")
    public String savePrescription( Model model, @Valid @ModelAttribute("prescription") Prescription prescription, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            model.addAttribute("prescription", prescription);
            return "prescription/add-prescription-description";
        }
        prescriptionService.savePrescription(prescription);
        redirectAttributes.addAttribute("prescriptionId", prescription.getId());
        return "redirect:/doctor/prescription/drug/list";
    }

    /* Add Drugs for that Particular Prescription */
    @GetMapping("/prescription/drug/add")
    public String prescriptionDrugAdd(Model model, @RequestParam("prescriptionId") Long prescriptionId) {
        Optional<Prescription> prescription = prescriptionService.findById(prescriptionId);
        PrescriptionDrug prescriptionDrug = new PrescriptionDrug();
        prescriptionDrug.setPrescription(prescription.get());
        List<Drug> drugList = drugService.findAllDrugs();
        model.addAttribute("appointment",prescription.get().getAppointment());
        model.addAttribute("doctor",prescription.get().getAppointment().getDoctor());
        model.addAttribute("patient",prescription.get().getAppointment().getPatient());
        model.addAttribute("drugList", drugList);
        model.addAttribute("prescriptionDrug", prescriptionDrug);
        model.addAttribute("prescriptionDrugList", prescriptionDrugService.findAllByPrescriptionId(prescriptionDrug.getPrescription().getId()));
        model.addAttribute("drug",new Drug());
        return "prescription/prescription-drug-form";
    }

    /* We can update the drug that already added to prescription (Dose of the Drugs ,how many Days etc.)*/
    @GetMapping("/prescription/drug/update")
    public String prescriptionDrugUpdate(@RequestParam("drugId") Long drugId, Model model) {

        Optional<PrescriptionDrug> prescriptionDrug = prescriptionDrugService.findById(drugId);
        model.addAttribute("prescriptionDrug", prescriptionDrug.get());
        return "prescription/prescription-drug-edit";
    }

    @PostMapping("/prescription/drug/save")
    public String savePrescription(@Valid @ModelAttribute("prescriptionDrug")PrescriptionDrug prescriptionDrug,BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes){
        String redirectLink = prescriptionDrugService.save(prescriptionDrug, bindingResult,model,redirectAttributes);
        return redirectLink;
    }

    /* Show the Drug List For Particular Prescription */
    @GetMapping("/prescription/drug/list")
    public String prescriptionDrugList(Model model, @RequestParam("prescriptionId") Long prescriptionId) {
        Optional<Prescription> prescription = prescriptionService.findById(prescriptionId);
        List<PrescriptionDrug> prescriptionDrugList = prescriptionDrugService.findAllByPrescriptionId(prescription.get().getId());
        model.addAttribute("appointment",prescription.get().getAppointment());
        model.addAttribute("patient",prescription.get().getAppointment().getPatient());
        model.addAttribute("prescriptionDrugList", prescriptionDrugList);
        model.addAttribute("appointmentId", prescription.get().getAppointment().getId());
        return "prescription/prescription-drug-list";
    }

    /* We can delete the drug that already added to prescription */
    @GetMapping("/prescription/drug/delete")
    public String deletePrescription(@RequestParam("drugId") Long drugId,RedirectAttributes redirectAttributes){

        Optional<PrescriptionDrug> prescriptionDrug = prescriptionDrugService.findById(drugId);
        prescriptionDrugService.deleteById(drugId);
        redirectAttributes.addAttribute("prescriptionId", prescriptionDrug.get().getPrescription());
        return "redirect:/doctor/prescription/drug/list";

    }

    /* If Doctor want add new drug ,
        which is not in Drug List during the prescription writing */
//    @GetMapping("/prescription/drugadd")
//    public String addDrugPrescription(Model theModel) {
//        Drug drug = new Drug();
//        theModel.addAttribute("drug", drug);
//        return "drug/prescription-drug-form";
//    }
//
//    @PostMapping("/prescription/drugadd")
//    public String saveDrugPrescription(@RequestParam("prescriptionId") Long prescriptionId, @ModelAttribute("drug") Drug drug, RedirectAttributes redirectAttributes,BindingResult bindingResult,Model model) {
//        drugService.save(drug,bindingResult,model);
//        redirectAttributes.addAttribute("prescriptionId", prescriptionId);
//        return "redirect:/doctor/prescription/drug/add";
//    }

    /* Drug */

    /* Showing the drug list*/
    @GetMapping("/drug/list")
    public String drugList(Model model) {
        List<Drug> drugList = drugService.findAllDrugs();
        model.addAttribute("drugList", drugList);
        return "drug/drug-list";
    }

    /*Add the particular drug*/
    @GetMapping("/drug/add")
    public String showFormForAdd(Model theModel) {
        Drug drug = new Drug();
        theModel.addAttribute("drug", drug);
        return "drug/drug-form";
    }

    /*Update the particular drug*/
    @GetMapping("/drug/update")
    public String showFormForUpdate(@RequestParam("drugId") Long drugId, Model theModel) {
        Drug drug = drugService.findById(drugId);
        theModel.addAttribute("drug", drug);
        return "drug/drug-form";
    }
    /*save the particular drug*/
    @PostMapping("/drug/save/model")
    public String saveDrugModel(@Valid @ModelAttribute("drug") Drug drug,BindingResult bindingResult,@RequestParam("prescriptionId") Long prescriptionId,Model model,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors() ){
            Optional<Prescription> prescription = prescriptionService.findById(prescriptionId);
            PrescriptionDrug prescriptionDrug = new PrescriptionDrug();
            prescriptionDrug.setPrescription(prescription.get());
            List<Drug> drugList = drugService.findAllDrugs();
            model.addAttribute("drugList", drugList);
            model.addAttribute("doctor",prescription.get().getAppointment().getDoctor());
            model.addAttribute("patient",prescription.get().getAppointment().getPatient());
            model.addAttribute("prescriptionDrug", prescriptionDrug);
            model.addAttribute("prescriptionDrugList", prescriptionDrugService.findAllByPrescriptionId(prescriptionDrug.getPrescription().getId()));
            return "prescription/prescription-drug-form";
        }else {
            drugService.save(drug,bindingResult,model);
            if (bindingResult.hasErrors() || model.containsAttribute("error")){
                Optional<Prescription> prescription = prescriptionService.findById(prescriptionId);
                PrescriptionDrug prescriptionDrug = new PrescriptionDrug();
                prescriptionDrug.setPrescription(prescription.get());
                List<Drug> drugList = drugService.findAllDrugs();
                model.addAttribute("drugList", drugList);
                model.addAttribute("doctor",prescription.get().getAppointment().getDoctor());
                model.addAttribute("patient",prescription.get().getAppointment().getPatient());
                model.addAttribute("prescriptionDrug", prescriptionDrug);
                model.addAttribute("prescriptionDrugList", prescriptionDrugService.findAllByPrescriptionId(prescriptionDrug.getPrescription().getId()));
                return "prescription/prescription-drug-form";
            }
        }
        redirectAttributes.addAttribute("prescriptionId",prescriptionId);
        return "redirect:/doctor/prescription/drug/add";
    }

    /*save the particular drug*/
    @PostMapping("/drug/save")
    public String saveDrug(@Valid @ModelAttribute("drug") Drug drug,BindingResult bindingResult,Model model) {
        if(bindingResult.hasErrors()){
            return "drug/drug-form";
        }
        drugService.save(drug,bindingResult,model);
        if(model.containsAttribute("error")){
            return "drug/drug-form";
        }

//
        return "redirect:/doctor/drug/list";
    }

    /*delete the particular drug*/
    @GetMapping("/drug/delete")
    public String deleteDrug(@RequestParam("drugId") Long drugId) {
        drugService.deleteById(drugId);
        return "redirect:/doctor/drug/list";
    }

    /* Clinic Service */

    /* add clinic services for patient */
    @PostMapping("/clinicservice/add")
    public String addClinicService(@ModelAttribute("clinicService") PrescriptionClinicServiceCreationDTO prescriptionClinicServiceCreationDTO, RedirectAttributes redirectAttributes) {
        PrescriptionClinicServices prescriptionClinicServices = modelMapper.map(prescriptionClinicServiceCreationDTO, PrescriptionClinicServices.class);
        ClinicServices service = clinicServicesRepository.findByName(prescriptionClinicServices.getServiceName());
        System.out.println(service.getCost());
        prescriptionClinicServices.setCost(service.getCost());
        prescriptionClinicService.save(prescriptionClinicServices);
        redirectAttributes.addAttribute("appointmentId", prescriptionClinicServices.getAppointment().getId());
        return "redirect:/appointment/view";
    }

    /* Delete Already added clinic services */
    @GetMapping("/clinicservice/delete")
    public String deleteClinicService(@RequestParam("serviceId") Long id, RedirectAttributes redirectAttributes) {
        Optional<PrescriptionClinicServices> prescriptionClinicServices = prescriptionClinicService.findById(id);
        prescriptionClinicService.deleteById(id);
        redirectAttributes.addAttribute("appointmentId", prescriptionClinicServices.get().getAppointment().getId());
        return "redirect:/appointment/view";
    }

    /* Loading old Messages */
    @GetMapping("/chat")
    public String showChattingPage(@RequestParam("patientId")long patientId,Model model){

        List<Message> oldMessages = null;
        Message message = new Message();
        MessageDTO messageDTO = new MessageDTO();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();

        Optional<Patient> patient = patientService.findById(patientId);
        Optional<Doctor> doctor = doctorService.findById(customUser.getId());

        Optional<Conversation> conversation = conversationService.findConversation(doctor.get(),patient.get());
        /* Checking Conversation is present or not */
        if (conversation.isPresent()) {
            /* Conversation is present  */
            messageDTO.setSenderId(conversation.get().getDoctor().getId());
            messageDTO.setConversationId(conversation.get().getId());
            oldMessages = messageService.findByConversation(conversation.get());
        }
        else if (!conversation.isPresent()){
            /* Conversation is not present
             so we Creating new Conversation bw doctor and patient */
            Conversation conversation1 = new Conversation();
            conversation1.setPatient(patient.get());
            conversation1.setDoctor(doctor.get());
            messageDTO.setSenderId(conversation1.getDoctor().getId());
            conversationService.saveConversation(conversation1);
            messageDTO.setConversationId(conversation1.getId());

        }

        model.addAttribute("oldMessages",oldMessages);
        model.addAttribute("message",messageDTO);
        model.addAttribute("patientName",patient.get().getFirstName());
        model.addAttribute("patientId",patientId);

        return "doctor/chatting";
    }

    @PostMapping("/chat")
    private String chatSaving(@ModelAttribute("message")Message message,@RequestParam("photo")MultipartFile multipartFile,RedirectAttributes redirectAttributes) throws IOException {
        //Saving Chat
        String redirectLink = doctorService.saveChatting(message,multipartFile,redirectAttributes);
        return redirectLink;
    }
}