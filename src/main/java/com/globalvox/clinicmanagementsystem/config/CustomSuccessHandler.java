package com.globalvox.clinicmanagementsystem.config;
import com.globalvox.clinicmanagementsystem.repository.DoctorRepository;
import com.globalvox.clinicmanagementsystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        String redirectUrl = request.getContextPath();

        Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (authorities.contains("ROLE_DOCTOR")){
            redirectUrl = "/doctor";
        }else if (authorities.contains("ROLE_PATIENT")){
            redirectUrl = "/patient";
        }else if (authorities.contains("ROLE_ADMIN")){
            redirectUrl = "/admin";
        } else if (authorities.contains("ROLE_STORE")){
            redirectUrl = "/store";
        }


        if(response.isCommitted())
        {
            System.out.println("Cant redirect");
        }
        response.sendRedirect(redirectUrl);
    }
}
