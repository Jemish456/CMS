package com.globalvox.clinicmanagementsystem.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MobileNumberValidator implements ConstraintValidator<ValidMobileNumber, String> {
//    private Pattern pattern;
//    private Matcher matcher;
//    private static final String MOBILE_NUMBER_PATTERN = "^([+]\\d{2})?\\d{10}$";

    @Override
    public void initialize(ValidMobileNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String mobileNumber, ConstraintValidatorContext constraintValidatorContext) {
        return (validateMobileNumber(mobileNumber));
    }

    private boolean validateMobileNumber(String mobileNumber) {

        if (mobileNumber.matches("\\d{10}"))
            return true;
            //validating phone number with -, . or spaces
        else if(mobileNumber.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}"))
            return true;
            //validating phone number with extension length from 3 to 5
        else if(mobileNumber.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}"))
            return true;
            //validating phone number where area code is in braces ()
        else if(mobileNumber.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}"))
            return true;
            //return false if nothing matches the input
        else return false;
//
//        pattern = Pattern.compile(MOBILE_NUMBER_PATTERN);
//        matcher = pattern.matcher(mobileNumber);
//        return matcher.matches();
    }
}
