package com.globalvox.clinicmanagementsystem.service;

import com.globalvox.clinicmanagementsystem.entity.*;
import com.globalvox.clinicmanagementsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Doctor doctor = null;
        Patient patient = null;
        Admin admin = null;
        Store store = null;
        if ((doctor = doctorRepository.findByEmail(email)) != null){
            return new CustomUser(doctor.getEmail(),doctor.getPassword(),
                    doctor.isEnable(),true,true,true,
                    MapRoleToAuthorities(doctor.getRoles()),
                    doctor.getId(),
                    doctor.getFirstName(), doctor.getMiddleName(), doctor.getLastName(),
                    doctor.getMobileNumber(),doctor.getProfilePhoto()
            );
        } else if ((patient = patientRepository.findByEmail(email)) != null){
            return new CustomUser(patient.getEmail(),patient.getPassword(),
                    patient.isEnable(),true,true,true,
                    MapRoleToAuthorities(patient.getRoles()),
                    patient.getId(),
                    patient.getFirstName(), patient.getMiddleName(), patient.getLastName(),
                    patient.getMobileNumber(),patient.getProfilePhoto()
            );
        } else if ((admin = adminRepository.findByEmail(email)) != null){
            return new CustomUser(admin.getEmail(),admin.getPassword(),
                    admin.isEnable(),true,true,true,
                    MapRoleToAuthorities(admin.getRoles()),
                    admin.getId(),
                    admin.getFirstName(), admin.getMiddleName(), admin.getLastName(),
                    admin.getMobileNumber(),admin.getProfilePhoto()
            );
        }
        else if ((store = storeRepository.findByEmail(email)) != null){
            return new CustomUser(store.getEmail(),store.getPassword(),
                    store.isEnable(),true,true,true,
                    MapRoleToAuthorities(store.getRoles()),
                    store.getId(),
                    store.getFirstName(), store.getMiddleName(), store.getLastName(),
                    store.getMobileNumber(),store.getProfilePhoto()
            );
        }
        else {
            throw new UsernameNotFoundException("Invalid Email or Password");
        }
    }

    private Collection<? extends GrantedAuthority> MapRoleToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }


}
