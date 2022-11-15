package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.dto.DoctorReviewDTO;
import com.globalvox.clinicmanagementsystem.dto.ForgotPasswordDTO;
import com.globalvox.clinicmanagementsystem.dto.PasswordUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.Doctor;
import com.globalvox.clinicmanagementsystem.entity.Message;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import com.globalvox.clinicmanagementsystem.entity.DoctorBlockage;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> findAll();

    Doctor save(Doctor doctor, String siteURL) throws MessagingException, UnsupportedEncodingException;

    Optional<Doctor> findById(Long doctorId);

    void deleteById(Long doctorId);

    int countById(Long id);

    Doctor findByEmail(String email);

    Doctor findByMobileNumber(String mobileNumber);

    boolean verify(String verificationCode);

    int countByEmail(String email);

    int countByEmailAndIdNot(String email,Long id);

    int countByMobileNumberAndIdNot(String mobileNumber,Long id);

    void updateResetPasswordToken(String token,String email);

    Doctor getByResetPasswordToken(String token);

    void updatePassword(Doctor doctor,String newPassword);

    void processForgotPassword(HttpServletRequest request, Model model);

    String processResetPassword(ForgotPasswordDTO forgotPasswordDTO, Model model,RedirectAttributes redirectAttributes);

    String processUpdatePassword(Optional<Doctor> doctor, PasswordUpdateDTO passwordUpdateDTO, Model model, BindingResult bindingResult,RedirectAttributes redirectAttributes);

    List<String> getTimeSlots(Doctor doctor,Date appointmentDate) throws ParseException;

    boolean isDoctorAvailable(Date startDateTime, Date endDateTime, Doctor doctor);

    List<DoctorReviewDTO> getDoctorAndAvgRating();

    long count();

    String saveChatting(Message message, MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException;

}
