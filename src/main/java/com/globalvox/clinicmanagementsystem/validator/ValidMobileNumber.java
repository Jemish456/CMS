package com.globalvox.clinicmanagementsystem.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = MobileNumberValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMobileNumber {

    public String message() default "Invalid mobile number.";

    public Class<?>[] groups() default {};

    public Class<? extends Payload>[] payload() default {};
}
