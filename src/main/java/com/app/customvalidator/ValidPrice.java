package com.app.customvalidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InValidPrice.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
    String message() default "Invalid price";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
