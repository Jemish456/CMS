package com.globalvox.clinicmanagementsystem.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
    String message() default "At least one upper case English letter," +
            "\nAt least one lower case English letter," +
            "\nAt least one digit," +
            "\nAt least one special character," +
            "\nMinimum eight in length";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
