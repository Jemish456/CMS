package com.globalvox.clinicmanagementsystem.validator;

import com.globalvox.clinicmanagementsystem.repository.DoctorRepository;
import com.globalvox.clinicmanagementsystem.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password,String> {
    private Pattern pattern;
    private Matcher matcher;

//    At least one upper case English letter, (?=.*?[A-Z])
//    At least one lower case English letter, (?=.*?[a-z])
//    At least one digit, (?=.*\d)
//    At least one special character, (?=.*[@$!%*?&])
//    Minimum eight in length {8,}
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return (validatePasswordPattern(password));
    }

    private boolean validatePasswordPattern(String password) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
