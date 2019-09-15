package com.app.customvalidator;

import com.app.model.FuelType;
import com.app.repository.FuelTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
public class InValidPrice implements ConstraintValidator<ValidPrice,Double> {

    @Autowired
    private FuelTypeRepository fuelTypeRepository;



    @Override
    public void initialize(ValidPrice constraintAnnotation) {

    }

    @Override
    public boolean isValid(Double aDouble, ConstraintValidatorContext constraintValidatorContext) {
        List<FuelType> all = fuelTypeRepository.findAll();
        System.out.println(all);

        return false;
    }
}
