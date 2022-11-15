package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.dto.DoctorReviewDTO;
import com.globalvox.clinicmanagementsystem.dto.ForgotPasswordDTO;
import com.globalvox.clinicmanagementsystem.dto.PasswordUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.DoctorBlockage;
import com.globalvox.clinicmanagementsystem.entity.EMail;
import com.globalvox.clinicmanagementsystem.entity.Role;
import com.globalvox.clinicmanagementsystem.entity.enums.AppointmentStatus;
import com.globalvox.clinicmanagementsystem.exception.NotFoundException;
import com.globalvox.clinicmanagementsystem.repository.AppointmentRepository;
import com.globalvox.clinicmanagementsystem.repository.DoctorBlockageRepository;
import com.globalvox.clinicmanagementsystem.repository.DoctorRepository;
import com.globalvox.clinicmanagementsystem.repository.RoleRepository;
import com.globalvox.clinicmanagementsystem.util.ServiceUtil;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import org.yaml.snakeyaml.util.EnumUtils;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

//    @Autowired
//    private JavaMailSender javaMailSender;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private DoctorBlockageRepository doctorBlockageRepository;

    @Value("${message.photo.path}")
    private String messagePhotoPath;

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor save(Doctor doctor, String siteURL) throws MessagingException, UnsupportedEncodingException {
        if (doctor.getId() == 0) {
            doctor.setPassword(new BCryptPasswordEncoder().encode(doctor.getPassword()));
            Role doctorRole = roleRepository.findByName("ROLE_DOCTOR");
            doctor.setRoles(new ArrayList<>(Arrays.asList(doctorRole)));

            String randomCode = RandomString.make(64);
            doctor.setVerificationCode(randomCode);

            sendVerificationEmail(doctor, siteURL);
        }

        return doctorRepository.save(doctor);
    }

    /*
     * Checking if doctor is present and verified let him log-in
     * If doctor is not present or email is not verified, don't allow to log-in
     * */
    public boolean verify(String verificationCode) {
        Doctor doctor = doctorRepository.findByVerificationCode(verificationCode);

        if (doctor == null || doctor.isEnable()) {
            return false;
        } else {
            doctor.setVerificationCode(null);
            doctor.setEnable(true);
            doctorRepository.save(doctor);
            return true;
        }

    }

    @Override
    public Optional<Doctor> findById(Long doctorId) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        return doctor;
    }

    @Override
    public int countById(Long id) {
        return doctorRepository.countById(id);
    }

    @Override
    public void deleteById(Long doctorId) {
        doctorRepository.deleteById(doctorId);
    }

    @Override
    public Doctor findByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }

    @Override
    public Doctor findByMobileNumber(String mobileNumber) {
        return doctorRepository.findByMobileNumber(mobileNumber);
    }

    @Override
    public int countByEmailAndIdNot(String email, Long id) {
        return doctorRepository.countByEmailAndIdNot(email, id);
    }

    @Override
    public int countByEmail(String email) {
        return doctorRepository.countByEmail(email);
    }

    @Override
    public int countByMobileNumberAndIdNot(String mobileNumber, Long id) {
        return doctorRepository.countByMobileNumberAndIdNot(mobileNumber, id);
    }

    /*
     * After successful registration, send verification mail to given email
     * */
    private void sendVerificationEmail(Doctor doctor, String siteURL)
            throws MessagingException, UnsupportedEncodingException {

        String email = doctor.getEmail();
        EMail eMail = new EMail();
        String verifyURL = siteURL + "/verify?code=" + doctor.getVerificationCode();

        eMail.setFrom("vibhutitemp@gmail.com");
        eMail.setTo(email);
        eMail.setSubject("Verify Link");
        eMail.setText("Click Here to Verify");
        eMail.setTemplate("email/verify-user.html");

        Map<String, Object> properties = new HashMap<>();
        properties.put("name", doctor.getFirstName());
        properties.put("link", verifyURL);
        eMail.setProperties(properties);

        emailSenderService.sendHtmlMessage(eMail);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) throws NotFoundException {
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor != null) {
            doctor.setResetPasswordToken(token);
            doctorRepository.save(doctor);
        } else {
            throw new NotFoundException("Could not find any Doctor with the email :- " + email);
        }
    }

    @Override
    public Doctor getByResetPasswordToken(String token) {
        return doctorRepository.findByResetPasswordToken(token);
    }

    @Override
    public void updatePassword(Doctor doctor, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        doctor.setPassword(encodedPassword);

        doctor.setResetPasswordToken(null);
        if (doctor.isEnable() == false) {
            doctor.setVerificationCode(null);
            doctor.setEnable(true);
        }
        doctorRepository.save(doctor);
    }

    @Override
    public void processForgotPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String token = RandomString.make(30);
        try {
            updateResetPasswordToken(token, email);
            String resetPasswordLink = ServiceUtil.getSiteURL(request) + "/reset-password?token=" + token;
            EMail eMail = new EMail();

            eMail.setFrom("vibhutitemp@gmail.com");
            eMail.setTo(email);
            eMail.setSubject("Reset Password Link");
            eMail.setText("Click to Reset your Password");
            eMail.setTemplate("email/forgot-password.html");
            Map<String, Object> properties = new HashMap<>();
            properties.put("link", resetPasswordLink);
            eMail.setProperties(properties);

            emailSenderService.sendHtmlMessage(eMail);

            model.addAttribute("message", "We have sent a reset password link to your email. Please check.");

        } catch (NotFoundException ex) {
            model.addAttribute("error", ex.getMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Doctor get(String resetPassWordToken) {
        return doctorRepository.findByResetPasswordToken(resetPassWordToken);
    }


    @Override
    public String processResetPassword(ForgotPasswordDTO forgotPasswordDTO, Model model,RedirectAttributes redirectAttributes) {
        String token = forgotPasswordDTO.getResetPasswordToken();
        String password = forgotPasswordDTO.getNewPassword();

        Doctor doctor = doctorRepository.findByResetPasswordToken(token);
        if (doctor != null) {
            updatePassword(doctor, password);
            redirectAttributes.addFlashAttribute("message","Success");
            return "redirect:/login";
        }
        else {
            model.addAttribute("message", "Invalid Token");
            return "error";
        }
    }

    @Override
    public String processUpdatePassword(Optional<Doctor> doctor, PasswordUpdateDTO passwordUpdateDTO, Model model, BindingResult bindingResult,RedirectAttributes redirectAttributes) {
        String userPassword = doctor.get().getPassword();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (bCryptPasswordEncoder.matches(passwordUpdateDTO.getOldPassword(), userPassword)) {

            if (!bCryptPasswordEncoder.matches(passwordUpdateDTO.getNewPassword(), userPassword)) {
                doctor.get().setPassword(bCryptPasswordEncoder.encode(passwordUpdateDTO.getNewPassword()));
                doctorRepository.save(doctor.get());
                redirectAttributes.addFlashAttribute("message","success");
                redirectAttributes.addAttribute("id",passwordUpdateDTO.getId());
                return "redirect:/doctor/view";  //Updated Page success Link
            }else {
                bindingResult.addError(new FieldError("user", "newPassword", "Old Password and New Password must be Different"));
                return "update-password";
            }

        }else {
//            model.addAttribute("user", passwordUpdateDTO);
//            model.addAttribute("error","Old Password is Incorrect");
            bindingResult.addError(new FieldError("user", "oldPassword", "Old Password is Incorrect"));
            return "update-password";
        }
    }

    @Override
    public List<String> getTimeSlots(Doctor doctor, Date appointmentDate) throws ParseException {

        // doctor working time on given appointment date
        Date doctorStartWorkDateTime = ServiceUtil.getDate(appointmentDate, doctor.getStartWorkTime());
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");

        // total slots
        long differenceInMilliSeconds = getWorkTime(doctor);
        long slotsCount = differenceInMilliSeconds / (doctor.getSlotTime() * 60 * 1000);

        Long time = doctorStartWorkDateTime.getTime();

        List<String> slotList = new ArrayList<>();

        // find availability of doctor for each slot
        for (int i = 0; i < slotsCount; i++) {
            Date startDateTime = new Date(time);
            time = time + 30 * 60 * 1000;
            Date endDateTime = new Date(time);

            if (isDoctorAvailable(startDateTime, endDateTime, doctor) && startDateTime.compareTo(new Date())>0) {
                slotList.add(formatter.format(startDateTime));
            }
        }
        return slotList;
    }

    @Override
    public String saveChatting(Message message, MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {

        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            if (!(fileName == null || fileName.equalsIgnoreCase(""))) {

                message.setMessagePhoto(fileName);
                String uploadDir = messagePhotoPath + message.getConversation().getId();
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
        message.setReceiverId(message.getConversation().getPatient().getId());
        messageService.saveMessage(message);
        redirectAttributes.addAttribute("patientId", message.getReceiverId());

        return "redirect:/doctor/chat";
    }

    private long getWorkTime(Doctor doctor) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date startDate = formatter.parse(doctor.getStartWorkTime().toString());
        Date endDate = formatter.parse(doctor.getEndWorkTime().toString());
        long differenceInMilliSeconds = Math.abs(startDate.getTime() - endDate.getTime());
        return differenceInMilliSeconds;
    }

    @Override
    public boolean isDoctorAvailable(Date startDateTime, Date endDateTime, Doctor doctor) {

        int appointmentCount = appointmentRepository.countAppointmentsByStartDateTimeBeforeAndEndDateTimeAfterAndDoctorAndStatus(endDateTime, startDateTime, doctor, AppointmentStatus.APPROVED);
        int doctorBlockageCount = doctorBlockageRepository.countDoctorBlockageByStartDateTimeBeforeAndEndDateTimeAfterAndDoctorAndStatusIsTrue(endDateTime, startDateTime, doctor);
        if (appointmentCount > 0 || doctorBlockageCount > 0) {
            return false;
        }
        return true;
    }

    @Override
    public List<DoctorReviewDTO> getDoctorAndAvgRating() {
        return doctorRepository.getDoctorAndAvgRating();
    }

    //give the number of doctor register in the system
    @Override
    public long count() {
        long totalDoctor = doctorRepository.count();
        return totalDoctor;
    }

}
