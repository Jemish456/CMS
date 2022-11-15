package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.dto.ForgotPasswordDTO;
import com.globalvox.clinicmanagementsystem.dto.PasswordUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.entity.EMail;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.Role;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.repository.PatientRepository;
import com.globalvox.clinicmanagementsystem.repository.RoleRepository;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class  PatientServiceImpl implements PatientService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private MessageService messageService;

    @Value("${message.photo.path}")
    private String messagePhotoPath;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public Patient save(Patient patient, String siteURL) throws MessagingException, UnsupportedEncodingException {
        if (patient.getId() == 0) {
            patient.setPassword(new BCryptPasswordEncoder().encode(patient.getPassword()));

            Role patientRole = roleRepository.findByName("ROLE_PATIENT");
            patient.setRoles(new ArrayList<>(Arrays.asList(patientRole)));

            String randomCode = RandomString.make(64);
            patient.setVerificationCode(randomCode);
            sendVerificationEmail(patient, siteURL);
        }
        return patientRepository.save(patient);
    }

    public boolean verify(String verificationCode) {
        Patient patient = patientRepository.findByVerificationCode(verificationCode);

        if (patient == null || patient.isEnable()) {
            return false;
        } else {
            patient.setVerificationCode(null);
            patient.setEnable(true);
            patientRepository.save(patient);
            return true;
        }
    }

    @Override
    public int countById(Long id) {
        return patientRepository.countById(id);
    }

    @Override
    public Optional<Patient> findById(Long patientId) {
        return patientRepository.findById(patientId);
    }

    @Override
    public void deleteById(Long patientId) {
        patientRepository.deleteById(patientId);
    }

    @Override
    public Patient findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    @Override
    public Patient findByMobileNumber(String mobileNumber) {
        return patientRepository.findByMobileNumber(mobileNumber);
    }

    @Override
    public int countByEmailAndIdNot(String email, Long id) {
        return patientRepository.countByEmailAndIdNot(email,id);
    }

    @Override
    public int countByEmail(String email) {
        return patientRepository.countByEmail(email);
    }

    @Override
    public int countByMobileNumberAndIdNot(String mobileNumber, Long id) {
        return patientRepository.countByMobileNumberAndIdNot(mobileNumber,id);
    }

    //forgot password
    @Override
    public void processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);
        try{
            updateResetPasswordToken(token,email);
            String resetPasswordLink = ServiceUtil.getSiteURL(request) + "/reset-password?token=" + token;
            EMail eMail = new EMail();

            eMail.setFrom(mailUsername);
            eMail.setTo(email);
            eMail.setSubject("Reset Password Link");
            eMail.setText("Click to Reset your Password");
            eMail.setTemplate("email/forgot-password.html");
            Map<String, Object> properties = new HashMap<>();
            properties.put("link", resetPasswordLink);
            eMail.setProperties(properties);

            emailSenderService.sendHtmlMessage(eMail);

            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        }catch (NotFoundException ex){
            model.addAttribute("error",ex.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Patient getByResetPasswordToken(String token) {
        return patientRepository.findByResetPasswordToken(token);
    }

    @Override
    public String processResetPassword(ForgotPasswordDTO forgotPasswordDTO, Model model,RedirectAttributes redirectAttributes) {
        String token = forgotPasswordDTO.getResetPasswordToken();
        String password = forgotPasswordDTO.getNewPassword();

        Patient patient = patientRepository.findByResetPasswordToken(token);

        if (patient != null){
            updatePassword(patient,password);
            redirectAttributes.addFlashAttribute("message","Success");
            return "redirect:/login";
        }
        else{
            model.addAttribute("message", "Invalid Token");
            return "error";
        }
    }

    @Override
    public String processUpdatePassword(Optional<Patient> patient,PasswordUpdateDTO passwordUpdateDTO, Model model, BindingResult bindingResult,RedirectAttributes redirectAttributes) {
        String userPassword = patient.get().getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (bCryptPasswordEncoder.matches(passwordUpdateDTO.getOldPassword(),userPassword)){
            if (!bCryptPasswordEncoder.matches(passwordUpdateDTO.getNewPassword(),userPassword)){
                patient.get().setPassword(bCryptPasswordEncoder.encode(passwordUpdateDTO.getNewPassword()));
                patientRepository.save(patient.get());
                redirectAttributes.addFlashAttribute("message","success");
                redirectAttributes.addAttribute("id",passwordUpdateDTO.getId());
                return "redirect:/patient/view";  //Updated Page success Link
            }else {
                bindingResult.addError(new FieldError("user", "newPassword", " Old Password and New Password must be Different"));
                return "update-password";
            }
        }else {
            bindingResult.addError(new FieldError("user", "oldPassword", "Old Password is Incorrect"));
            return "update-password";
        }
    }

    @Override
    public String saveChatting(Message message, MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
        if (!multipartFile.isEmpty()){
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            if (!(fileName == null || fileName.equalsIgnoreCase(""))) {

                message.setMessagePhoto(fileName);
                String uploadDir = messagePhotoPath+message.getConversation().getId();
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = multipartFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    System.out.println(filePath.toString());
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (NotFoundException | IOException ex) {
                    throw new NotFoundException("Could Not Save Image ");
                }
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        message.setSenderId(customUser.getId());
        message.setReceiverId(message.getConversation().getDoctor().getId());
        messageService.saveMessage(message);
        redirectAttributes.addAttribute("doctorId",message.getReceiverId());
        return  "redirect:/patient/chat";
    }

    //update password
    private void updatePassword(Patient patient, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        patient.setPassword(encodedPassword);
        patient.setResetPasswordToken(null);
        if (patient.isEnable() == false){
            patient.setVerificationCode(null);
            patient.setEnable(true);
        }
        patientRepository.save(patient);
    }

    private void updateResetPasswordToken(String token, String email) throws NotFoundException {
        Patient patient = patientRepository.findByEmail(email);
        if (patient != null){
            patient.setResetPasswordToken(token);
            patientRepository.save(patient);
        }else {
            throw new NotFoundException("Could not found Patient");
        }
    }
    private void sendVerificationEmail(Patient patient, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String email = patient.getEmail();
        EMail eMail = new EMail();
        String verifyURL = siteURL + "/verify?code=" + patient.getVerificationCode();
        eMail.setFrom("vibhutitemp@gmail.com");
        eMail.setTo(email);
        eMail.setSubject("Verify Link");
        eMail.setText("Click Here to Verify");
        eMail.setTemplate("email/verify-user.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name",patient.getFirstName());
        properties.put("link", verifyURL);
        eMail.setProperties(properties);
        emailSenderService.sendHtmlMessage(eMail);
    }

    //give the number of patient register int he system
    @Override
    public long count() {
        long totalPatient = patientRepository.count();
        return totalPatient;
    }
}