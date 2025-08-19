package com.example.annotation;

import com.example.validations.AtLeastOneFieldPresentValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Constraint(validatedBy = AtLeastOneFieldPresentValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AtLeastOneFieldPresent {
    String message() default "At least one field must be provided for update";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
