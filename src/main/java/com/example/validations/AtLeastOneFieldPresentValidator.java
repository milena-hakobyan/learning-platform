package com.example.validations;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class AtLeastOneFieldPresentValidator implements ConstraintValidator<AtLeastOneFieldPresent, Object> {

    @Override
    public boolean isValid(Object dto, ConstraintValidatorContext context) {
        if (dto == null) return false;

        try {
            Field[] fields = dto.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(dto);
                if (value != null) {
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            return false;
        }

        return false;
    }
}
