package com.tanmeyah.practice.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// validator for :  NoSpecialCharacter ,,, Type: String

public class NoSpecialCharactersValidator implements ConstraintValidator<NoSpecialCharacters, String> {

    private static final String NAME_REGEX = "^[A-Za-z ]+$"; // only letters

    @Override
    //called automatically when validation runs
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return value.matches(NAME_REGEX);
    }
}

