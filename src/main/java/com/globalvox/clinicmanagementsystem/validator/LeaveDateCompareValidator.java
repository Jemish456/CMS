package com.globalvox.clinicmanagementsystem.validator;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Date;

/* A validator class to check whether one date is equal or grater than the other one. */
public class LeaveDateCompareValidator implements ConstraintValidator<LeaveDateCompare, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(LeaveDateCompare constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean valid = true;
        try {
            final Date firstObj = (Date) new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
            final Date secondObj = (Date) new BeanWrapperImpl(value).getPropertyValue(secondFieldName);

            valid = firstObj != null && secondObj != null && (firstObj.before(secondObj) || firstObj.equals(secondObj)) ;
        } catch (final Exception ignore) {
            // we can ignore
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(firstFieldName)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }

}