package com.globalvox.clinicmanagementsystem.controller;

import com.globalvox.clinicmanagementsystem.entity.EMail;
import com.globalvox.clinicmanagementsystem.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Controller
public class EmailSenderController {

    @Autowired
    private EmailSenderService emailSenderService;

    //send mail to the particular user
    @GetMapping("/email")
    public void sendHtmlMessage(EMail email) throws MessagingException, UnsupportedEncodingException {
        emailSenderService.sendHtmlMessage(email);
    }

}
