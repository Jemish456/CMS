package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.dto.*;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.dto.AppointmentCreationDTO;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.dto.DoctorReviewDTO;
import com.globalvox.clinicmanagementsystem.dto.ReviewCreationDTO;
import com.globalvox.clinicmanagementsystem.entity.enums.Rating;
import com.globalvox.clinicmanagementsystem.entity.Appointment;
import com.globalvox.clinicmanagementsystem.entity.enums.Severity;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.dto.PatientUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.enums.BloodGroup;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.service.*;
import com.globalvox.clinicmanagementsystem.service.AppointmentService;
import com.globalvox.clinicmanagementsystem.service.DoctorService;
import com.globalvox.clinicmanagementsystem.service.PatientService;
import com.globalvox.clinicmanagementsystem.service.SymptomsService;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
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
import java.text.ParseException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SymptomsService symptomsService;

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private PrescriptionDrugService prescriptionDrugService;

    @Value("${profile.photo.path}")
    private String profilePhotoPath;

    @Value("${message.photo.path}")
    private String messagePhotoPath;

    @RequestMapping("")
    public String loadHome(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Long patientId = customUser.getId();
        Optional<Patient> patient = patientService.findById(patientId);
        if(patient.isPresent()) {
            model.addAttribute("appointments",appointmentService.findAllByPatientId(patientId));
        }
        return "patient/home";
    }

    /* show the particular doctor details */
    @RequestMapping("/doctor/view")
    public String loadDoctorDetails(@RequestParam("id") Long doctorID, Model model) {
        Optional<Doctor> doctor = doctorService.findById(doctorID);
        if (!doctor.isPresent()) {
            throw new NotFoundException("User Not Found");
        }
        model.addAttribute("doctor", doctor.get());
        return "doctor/view";
    }

    /* show the particular patient details */
    @RequestMapping("/view")
    public String loadPatientDetails(@RequestParam("id") Long patientID, Model model) {
        Optional<Patient> patient = patientService.findById(patientID);
        if (!patient.isPresent()) {
            throw new NotFoundException("User Not Found");
        }
        model.addAttribute("patient", patient.get());
        return "patient/view";
    }

    /* edit particular patient details */
    @GetMapping("/edit")
    public String loadEditPatient(@RequestParam("id") Long patientID, Model model) {
        Optional<Patient> patient = patientService.findById(patientID);
        if (!patient.isPresent()) {
            throw new NotFoundException("User Not Found");
        }
        PatientUpdateDTO patientUpdateDTO = modelMapper.map(patient.get(), PatientUpdateDTO.class);
        model.addAttribute("genders", Gender.values());
        model.addAttribute("bloodgroups", BloodGroup.values());
        model.addAttribute("user", patientUpdateDTO);
        return "patient/edit";
    }

    @PostMapping("/edit")
    public String editPatient(@Valid @ModelAttribute("user") PatientUpdateDTO patientUpdateDTO, BindingResult bindingResult, @RequestParam("photo") MultipartFile multipartFile, Model model,RedirectAttributes redirectAttributes, HttpServletRequest request) throws MessagingException, IOException {
        Optional<Patient> patient = patientService.findById(patientUpdateDTO.getId());

        // uploaded image validation
        if (!multipartFile.isEmpty() && (!(multipartFile.getContentType().equals("image/jpeg")) &&
                !(multipartFile.getContentType().equals("image/png")) &&
                !(multipartFile.getContentType().equals("image/jpg")))) {
            bindingResult.addError(new FieldError("patient", "profilePhoto", "Image format is not jpeg,png or jpg"));
        }
        if (patient.isPresent()) {

            int emailCount = doctorService.countByEmailAndIdNot(patientUpdateDTO.getEmail(), patientUpdateDTO.getId()) +
                    patientService.countByEmailAndIdNot(patientUpdateDTO.getEmail(), patientUpdateDTO.getId());

            int mobileNumberCount = doctorService.countByMobileNumberAndIdNot(patientUpdateDTO.getMobileNumber(), patientUpdateDTO.getId()) +
                    patientService.countByMobileNumberAndIdNot(patientUpdateDTO.getMobileNumber(), patientUpdateDTO.getId());

            // email already exists
            if (emailCount > 0) {
                bindingResult.addError(new FieldError("patient", "email", "Patient already exist with this email."));
            }

            // phone number already exists
            if (mobileNumberCount > 0) {
                bindingResult.addError(new FieldError("patient", "mobileNumber", "Patient already exist with this mobile number."));
            }

            if (bindingResult.hasErrors()) {
                model.addAttribute("genders", Gender.values());
                model.addAttribute("bloodgroups", BloodGroup.values());
                return "patient/edit";
            }

            modelMapper.map(patientUpdateDTO, patient.get());
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

            if (fileName == null || fileName.equalsIgnoreCase("")) {
                patientService.save(patient.get(), ServiceUtil.getSiteURL(request));
            } else {
                patient.get().setProfilePhoto(fileName);

                Patient patient1 = patientService.save(patient.get(), ServiceUtil.getSiteURL(request));

                //upload image
                String uploadDir = profilePhotoPath + patient1.getId();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    System.out.println(filePath.toString());
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (NotFoundException ex) {
                    throw new NotFoundException("Could Not Save Image ");
                }
            }
            patientService.save(patient.get(), ServiceUtil.getSiteURL(request));
            redirectAttributes.addAttribute("id",ServiceUtil.getAuthUserId());
            return "redirect:/patient";
        } else {
            throw new NotFoundException("User Not Found");
        }
    }

    /* Loading old Messages */
    @GetMapping("/chat")
    private String showChatPage(@RequestParam("doctorId")long doctorId,Model model){

        List<Message> oldMessages = null;
        MessageDTO messageDTO = new MessageDTO();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Optional<Doctor> doctor = doctorService.findById(doctorId);
        Optional<Patient> patient =patientService.findById(customUser.getId());
        Optional<Conversation> conversation = conversationService.findConversation(doctor.get(),patient.get());

        /* Checking Conversation is present or not */
        if (conversation.isPresent()){
            /* Conversation is present  */
            oldMessages = messageService.findByConversation(conversation.get());
            messageDTO.setConversationId(conversation.get().getId());
        }
        else if (!conversation.isPresent()){
            /* Conversation is not present
             so we Creating new Conversation bw doctor and patient */
            Conversation conversation1 = new Conversation();
            conversation1.setDoctor(doctor.get());
            conversation1.setPatient(patient.get());
            conversationService.saveConversation(conversation1);
            messageDTO.setConversationId(conversation1.getId());
            messageDTO.setSenderId(patient.get().getId());
        }
        model.addAttribute("oldMessages",oldMessages);
        messageDTO.setSenderId(patient.get().getId());
        model.addAttribute("message",messageDTO);
        model.addAttribute("doctorName",doctor.get().getFirstName());
        model.addAttribute("doctorId",doctorId);

        return "patient/chatting";
    }

    @PostMapping("/chat")
    private String chatting(@ModelAttribute("message")Message message,@RequestParam("photo") MultipartFile multipartFile,RedirectAttributes redirectAttributes) throws IOException {
        //Saving Chat
        String redirectLink = patientService.saveChatting(message,multipartFile,redirectAttributes);
        return  redirectLink;
    }

    @GetMapping("/prescription/list")
    public String prescriptionList(Model model,@RequestParam("appointmentId")Long appointmentId){
        Optional<Prescription> prescription = prescriptionService.findByAppointmentId(appointmentId);
        if(prescription.isPresent()) {
            List<PrescriptionDrug> prescriptionDrugList = prescriptionDrugService.findAllByPrescriptionId(prescription.get().getId());
            model.addAttribute("prescriptionDrugList", prescriptionDrugList);
            return "patient/prescription-list";
        }
       return "patient/prescription-list";
    }

    @RequestMapping("/doctor/clearList")
    public ResponseEntity<?> clearSearchDoctorList() {
        List<DoctorReviewDTO> doctorList = doctorService.getDoctorAndAvgRating();
        return ResponseEntity.ok(doctorList);
    }

    /* review  */

    //get doctor list with avg rating
    @RequestMapping("/doctor/list")
    public String loadDoctorList(Model model) {
        List<DoctorReviewDTO> doctorList = doctorService.getDoctorAndAvgRating();
        model.addAttribute("doctors", doctorList);
        return "doctor/list";
    }

    //show add review page
    @RequestMapping("/doctor/review/reviewPage")
    public String loadReviewPage(@RequestParam("doctorId") Long doctorId,@RequestParam("patientId") Long patientId,Model model){

        Optional<Doctor> doctor = doctorService.findById(doctorId);
        Optional<Patient> patient = patientService.findById(patientId);

        if (!doctor.isPresent()) {
            throw new NotFoundException("User Not Found");
        }

        if (!patient.isPresent()) {
            throw new NotFoundException("User Not Found");
        }

        //passing rating enum values , creationdto object,doctor and patient id
        model.addAttribute("review",new ReviewCreationDTO());
        model.addAttribute("doctor", doctor.get());
        model.addAttribute("patient", patientId);

        return "review/add-review";
    }

    //saving review
    @PostMapping("/review/addReview")
    public String addReview(@Valid @ModelAttribute("review") ReviewCreationDTO reviewCreationDTO,
                            BindingResult bindingResult,
                            @RequestParam("doctorId") Long doctorId,
                            @RequestParam("patientId") Long patientId,
                            Model model,
                            RedirectAttributes redirectAttributes) throws ParseException {



        //mapping dto to entity
        Review review = modelMapper.map(reviewCreationDTO,Review.class);

        Optional<Doctor> doctor = doctorService.findById(doctorId);
        Optional<Patient> patient = patientService.findById(patientId);

        if (!doctor.isPresent()) {
            throw new NotFoundException("User Not Found");
        }

        if (!patient.isPresent()) {
            throw new NotFoundException("User Not Found");
        }

        review.setDoctor(doctor.get());
        review.setPatient(patient.get());

        if(bindingResult.hasErrors()){
            model.addAttribute("doctor", doctor.get());
            model.addAttribute("patient", patientId);
            return "review/add-review";
        }

        reviewService.save(review);

        redirectAttributes.addAttribute("id",doctorId);

        return "redirect:/patient/doctor/view";
    }

    //show reviews
    @RequestMapping("/doctor/review/reviewList")
    public String showReviews(@RequestParam("doctorId") Long doctorId,Model model){

        Optional<Doctor> doctor = doctorService.findById(doctorId);

        if (!doctor.isPresent()) {
            throw new NotFoundException("User Not Found");
        }
        model.addAttribute("doctor",doctor.get());
        return "review/show-review";
    }

}
