package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.dto.ForgotPasswordDTO;
import com.globalvox.clinicmanagementsystem.dto.PasswordUpdateDTO;
import com.globalvox.clinicmanagementsystem.entity.Message;
import com.globalvox.clinicmanagementsystem.entity.Patient;
import org.springframework.stereotype.Component;
import com.globalvox.clinicmanagementsystem.entity.Prescription;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;


public interface PatientService {

    List<Patient> findAll();

    Patient save(Patient patient, String siteURL) throws MessagingException, UnsupportedEncodingException;

    Optional<Patient> findById(Long patientId);

    void deleteById(Long patientId);

    Patient findByEmail(String email);

    int countByEmail(String email);

    int countById(Long id);

    Patient findByMobileNumber(String mobileNumber);

    boolean verify(String verificationCode);

    int countByEmailAndIdNot(String email,Long id);

    int countByMobileNumberAndIdNot(String mobileNumber,Long id);

    void processForgotPassword(HttpServletRequest request, Model model);

    Patient getByResetPasswordToken(String token);

    String processResetPassword(ForgotPasswordDTO forgotPasswordDTO, Model model,RedirectAttributes redirectAttributes);

    String processUpdatePassword(Optional<Patient> patient, PasswordUpdateDTO passwordUpdateDTO, Model model, BindingResult bindingResult,RedirectAttributes redirectAttributes);

    String saveChatting(Message message, MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException;

    long count();
}
