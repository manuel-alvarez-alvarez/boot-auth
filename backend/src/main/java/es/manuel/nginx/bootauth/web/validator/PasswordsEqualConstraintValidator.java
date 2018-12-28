package es.manuel.nginx.bootauth.web.validator;


import org.springframework.util.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsEqualConstraintValidator implements ConstraintValidator<PasswordsEqualConstraint, HasPassword> {

    @Override
    public void initialize(PasswordsEqualConstraint arg0) {
    }

    @Override
    public boolean isValid(final HasPassword candidate, final ConstraintValidatorContext arg1) {
        return ObjectUtils.nullSafeEquals(candidate.getPassword(), candidate.getPasswordRepeat());
    }
}
