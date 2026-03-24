package com.tanmeyah.practice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = NoSpecialCharactersValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER}) //On class fields ex DTO fields ,, On method parameters
@Retention(RetentionPolicy.RUNTIME) //available at runtime
public @interface NoSpecialCharacters {
    String message() default "must contain only letters and spaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {}; //Bean validation spec
}


/// // This is the Annotation
