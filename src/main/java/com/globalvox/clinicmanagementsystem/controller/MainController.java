package com.globalvox.clinicmanagementsystem.controller;
import com.globalvox.clinicmanagementsystem.dto.DoctorCreationDTO;
import com.globalvox.clinicmanagementsystem.dto.ForgotPasswordDTO;
import com.globalvox.clinicmanagementsystem.dto.PatientCreationDTO;
import com.globalvox.clinicmanagementsystem.dto.PasswordUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.entity.enums.Gender;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.service.*;

import com.globalvox.clinicmanagementsystem.service.AppointmentService;
import com.globalvox.clinicmanagementsystem.service.DoctorService;
import com.globalvox.clinicmanagementsystem.service.MessageService;
import com.globalvox.clinicmanagementsystem.service.PatientService;
import com.globalvox.clinicmanagementsystem.service.PrescriptionService;
import org.apache.commons.io.IOUtils;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

@Controller
public class MainController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private StaffMemberService staffMemberService;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${profile.photo.path}")
    private String profilePhotoPath;

    @Autowired
    private MessageService messageService;

    @Value("${message.photo.path}")
    private String messagePhotoPath;

    private Logger logger = Logger.getLogger(getClass().getName());

    @Autowired
    private DrugService drugService;

    @Autowired
    private PrescriptionService prescriptionService;


    @PostConstruct
    private void postConstruct() { }

    // Delete this one this is for testing purpose
    @GetMapping("/api/chat")
    public String test(){
        return "doctor/chat-new";
    }

    @GetMapping("/")
    public String loadGuestHome() {

        if (isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (authorities.contains("ROLE_DOCTOR")) {
                return "redirect:/doctor";
            } else if (authorities.contains("ROLE_PATIENT")) {
                return "redirect:/patient";
            } else if (authorities.contains("ROLE_ADMIN")) {
                return "redirect:/admin";
            } else if (authorities.contains("ROLE_STORE")) {
                return "redirect:/store";
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String loadLogin() {
        if (isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if(authorities.contains("ROLE_DOCTOR")) {
                return "redirect:/doctor";
            } else if (authorities.contains("ROLE_PATIENT")) {
                return "redirect:/patient";
            } else if(authorities.contains("ROLE_ADMIN")){
                return "redirect:/admin";
            } else if (authorities.contains("ROLE_STORE")) {
                return "redirect:/store";
            }
        }
        return "login";
    }

    @GetMapping("/registerDoctor")
    public String loadRegisterDoctor(Model model) {

        if (isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (authorities.contains("ROLE_DOCTOR")) {
                return "redirect:/doctor";
            } else if (authorities.contains("ROLE_PATIENT")) {
                return "redirect:/patient";
            }
        }

        Doctor doctor = new Doctor();
        DoctorCreationDTO doctorCreationDTO = modelMapper.map(doctor, DoctorCreationDTO.class);
        model.addAttribute("user", doctorCreationDTO);
        model.addAttribute("genders", Gender.values());

        return "doctor/register";
    }

    @GetMapping("/registerPatient")
    public String loadRegisterPatient(Model model) {

        if (isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if (authorities.contains("ROLE_DOCTOR")) {
                return "redirect:/doctor";
            } else if (authorities.contains("ROLE_PATIENT")) {
                return "redirect:/patient";
            }
        }

        Patient patient = new Patient();
        PatientCreationDTO patientCreationDTO = modelMapper.map(patient, PatientCreationDTO.class);
        model.addAttribute("user", patientCreationDTO);
        model.addAttribute("genders", Gender.values());
        return "patient/register";
    }

    @PostMapping("/registerDoctor")
    public String registerDoctor(@Valid @ModelAttribute("user") DoctorCreationDTO doctorCreationDTO, BindingResult bindingResult, @RequestParam("photo") MultipartFile profilePhotoFile, Model model, HttpServletRequest request) throws MessagingException, IOException {
        int emailCount = doctorService.countByEmailAndIdNot(doctorCreationDTO.getEmail(), doctorCreationDTO.getId()) +
                patientService.countByEmailAndIdNot(doctorCreationDTO.getEmail(), doctorCreationDTO.getId());

        int mobileNumberCount = doctorService.countByMobileNumberAndIdNot(doctorCreationDTO.getMobileNumber(), doctorCreationDTO.getId()) +
                patientService.countByMobileNumberAndIdNot(doctorCreationDTO.getMobileNumber(), doctorCreationDTO.getId());

        // uploaded image validation
        if (!profilePhotoFile.isEmpty() && (!(profilePhotoFile.getContentType().equals("image/jpeg")) &&
                !(profilePhotoFile.getContentType().equals("image/png")) &&
                !(profilePhotoFile.getContentType().equals("image/jpg")))) {
            bindingResult.addError(new FieldError("doctor", "profilePhoto", "Image format is not jpeg,png or jpg"));
        }

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
            return "doctor/register";
        } else {
            Doctor doctor = modelMapper.map(doctorCreationDTO, Doctor.class);
            String fileName = StringUtils.cleanPath(profilePhotoFile.getOriginalFilename());
            if (fileName == null || fileName.equalsIgnoreCase("")) {
                doctor.setProfilePhoto(null);
                doctorService.save(doctor, ServiceUtil.getSiteURL(request));
            } else {
                doctor.setProfilePhoto(fileName);
                Doctor doctor1 = doctorService.save(doctor, ServiceUtil.getSiteURL(request));

                //upload image
                String uploadDir = profilePhotoPath + doctor1.getId();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = profilePhotoFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    System.out.println(filePath.toString());
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (NotFoundException ex) {
                    throw new NotFoundException("Could Not Save Image ");
                }
            }
            return "redirect:/register-success";
        }
    }

    @PostMapping("/registerPatient")
    public ModelAndView registerPatient(@Valid @ModelAttribute("user") PatientCreationDTO patientCreationDTO,BindingResult bindingResult,@RequestParam("photo") MultipartFile profilePhotoFile, Model model, HttpServletRequest request) throws MessagingException, IOException {
        int emailCount = doctorService.countByEmailAndIdNot(patientCreationDTO.getEmail(), patientCreationDTO.getId()) +
                patientService.countByEmailAndIdNot(patientCreationDTO.getEmail(), patientCreationDTO.getId());
        int mobileNumberCount = doctorService.countByMobileNumberAndIdNot(patientCreationDTO.getMobileNumber(), patientCreationDTO.getId()) +
                patientService.countByMobileNumberAndIdNot(patientCreationDTO.getMobileNumber(), patientCreationDTO.getId());

        // uploaded image validation
        if (!profilePhotoFile.isEmpty() && (!(profilePhotoFile.getContentType().equals("image/jpeg")) &&
                !(profilePhotoFile.getContentType().equals("image/png")) &&
                !(profilePhotoFile.getContentType().equals("image/jpg")))) {
            bindingResult.addError(new FieldError("patient", "profilePhoto", "Image format is not jpeg,png or jpg"));
        }

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
            return new ModelAndView("patient/register");
        } else {
            Patient patient = modelMapper.map(patientCreationDTO, Patient.class);

            String fileName = StringUtils.cleanPath(profilePhotoFile.getOriginalFilename());
            if (fileName == null || fileName.equalsIgnoreCase("")) {
                patient.setProfilePhoto(null);
                patientService.save(patient, ServiceUtil.getSiteURL(request));
            } else {
                patient.setProfilePhoto(fileName);
                Patient patient1 = patientService.save(patient, ServiceUtil.getSiteURL(request));

                //upload image
                String uploadDir = profilePhotoPath + patient1.getId();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = profilePhotoFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    System.out.println(filePath.toString());
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (NotFoundException ex) {
                    throw new NotFoundException("Could Not Save Image ");
                }
            }
            return new ModelAndView("redirect:/register-success");
        }
    }

    /* Registration mail */
    /* Verify if user is registered successfully or not! */
    @GetMapping("/verify")
    public String verifyUser(@Param("code") String code) {
        boolean check = doctorService.verify(code) || patientService.verify(code);
        if (check) {
            return "verify-success";
        } else {
            return "verify-failed";
        }
    }

    /* On successful register redirect user to this page */
    @GetMapping("/register-success")
    public String registerSuccess() {
        return "register-success";
    }

    /* display profile images */
    @GetMapping("/display/image/")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") Long id) {
        String img = "default.jpg";
        Optional<Doctor> doctor = doctorService.findById(id);
        Optional<Patient> patient = patientService.findById(id);

        /* Check if the user photo is available in DB or not
          otherwise we display default image */
        if (doctor.isPresent() && doctor.get().getProfilePhoto() != null) {
            img = id + "/" + doctor.get().getProfilePhoto();
        } else if (patient.isPresent() && patient.get().getProfilePhoto() != null) {
            img = id + "/" + patient.get().getProfilePhoto();
        }

        try {
            String path = profilePhotoPath + img;
            InputStream inputStream = new FileInputStream(path);
            final HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.IMAGE_JPEG);
//            headers.setCacheControl(CacheControl.noCache().getHeaderValue());
            return new ResponseEntity<>(IOUtils.toByteArray(inputStream), headers, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            throw new NotFoundException("File Not Found ");
        } catch (IOException e) {
            throw new NotFoundException("Could not load image");
        }
    }

    @GetMapping("/access-denied")
    public String loadAccessDenied() {
        return "access-denied";
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    /*forgot password*/
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        Doctor doctor = doctorService.findByEmail(email);
        Patient patient = patientService.findByEmail(email);

        if (doctor != null){
            doctorService.processForgotPassword(request,model);
        }else if(patient != null){
            patientService.processForgotPassword(request,model);
        }
        else if (doctor == null && patient == null){
            model.addAttribute("error","User with this email not found");
        }
        return "forgot-password";
    }

    /*update password*/
    @GetMapping("/updatePassword")
    public String updatePassword(@RequestParam("id") long id, Model model) {
        Optional<Patient> patient = patientService.findById(id);
        Optional<Doctor> doctor = doctorService.findById(id);

        /* Fetching usr By Id */
        if (patient.isPresent()) {
            PasswordUpdateDTO passwordUpdateDTO = modelMapper.map(patient.get(), PasswordUpdateDTO.class);
            model.addAttribute("user", passwordUpdateDTO);
        } else if (doctor.isPresent()) {
            PasswordUpdateDTO passwordUpdateDTO = modelMapper.map(doctor.get(), PasswordUpdateDTO.class);
            model.addAttribute("user", passwordUpdateDTO);
        }
        return "update-password";
    }

    @PostMapping("/updatePassword")
    public String updatingPassword(@Valid @ModelAttribute("user") PasswordUpdateDTO passwordUpdateDTO,BindingResult bindingResult, Model model,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException, MessagingException {

        if (bindingResult.hasErrors()){
            return "update-password";
        }

        Optional<Patient> patient = patientService.findById(passwordUpdateDTO.getId());
        Optional<Doctor> doctor = doctorService.findById(passwordUpdateDTO.getId());
        String userPassword = null;
        /* Check User was Patient or Doctor */
        if (patient.isPresent() || doctor.isPresent()){

                if ( patient.isPresent() ){
                    return  patientService.processUpdatePassword(patient,passwordUpdateDTO,model,bindingResult,redirectAttributes);

            } else if (doctor.isPresent() ){
                    return  doctorService.processUpdatePassword(doctor,passwordUpdateDTO,model,bindingResult,redirectAttributes);
                }
        }
        return "index";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if(token != null){
            Doctor doctor = doctorService.getByResetPasswordToken(token);
            Patient patient = patientService.getByResetPasswordToken(token);

            ForgotPasswordDTO forgotPasswordDTO = new ForgotPasswordDTO();
            forgotPasswordDTO.setResetPasswordToken(token);
            if (doctor != null) {
                model.addAttribute("user", doctor);
                model.addAttribute("forgotPassword",forgotPasswordDTO);
                return "reset-password";
            } else if (patient != null) {
                model.addAttribute("user", patient);
                model.addAttribute("forgotPassword",forgotPasswordDTO);
                return "reset-password";
            }
            model.addAttribute("message", "Invalid Token");
            return "error";//  Password Link Expire page
        }
        else {
            model.addAttribute("message", "Invalid Token");
            return "error";
        }

    }

    @PostMapping("/reset-password")
    public String processResetPassword(@Valid @ModelAttribute("forgotPassword")ForgotPasswordDTO forgotPasswordDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        String token = forgotPasswordDTO.getResetPasswordToken();
        Doctor doctor = doctorService.getByResetPasswordToken(token);
        Patient patient = patientService.getByResetPasswordToken(token);
        String redirectLink;

        if (bindingResult.hasErrors()){
            return "reset-password";
        }
        if (doctor != null) {
            redirectLink =  doctorService.processResetPassword(forgotPasswordDTO, model,redirectAttributes);
            return redirectLink;
        } else if (patient != null) {
            redirectLink = patientService.processResetPassword(forgotPasswordDTO, model,redirectAttributes);
            return redirectLink;
        }

        model.addAttribute("message", "Invalid Token");
        return "error";

    }

    /* Display chat Image */
    @GetMapping("/display/chat-image/")
    public ResponseEntity<byte[]> displayChatImage(@RequestParam("id") Long id) throws IOException {
        String img = "default.jpg";
        Optional<Message> message = Optional.ofNullable(messageService.findById(id));
        /* check there is image or not */
        if (message.isPresent() && message.get().getMessagePhoto() != null) {
            /* Image Exist now taking from server */
            img = message.get().getConversation().getId() + "/" + message.get().getMessagePhoto();
            try {
                String path = messagePhotoPath + img;
                InputStream inputStream = new FileInputStream(path);

                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
//            headers.setCacheControl(CacheControl.noCache().getHeaderValue());

                return new ResponseEntity<>(IOUtils.toByteArray(inputStream), headers, HttpStatus.OK);
            } catch (FileNotFoundException e) {
                throw new NotFoundException("File Not Found ");
            } catch (IOException e) {
                throw new NotFoundException("Could not load image");
            }
        }
        InputStream inputStream = new FileInputStream((File) null);
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>(IOUtils.toByteArray(inputStream), headers, HttpStatus.OK);
    }

}