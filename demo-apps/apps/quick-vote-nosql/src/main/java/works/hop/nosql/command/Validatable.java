package works.hop.nosql.command;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.function.Supplier;

public interface Validatable {

    Map<String, List<String>> validate();

    default Map<String, List<String>> validate(Object... entities) {
        Map<String, List<String>> errors = new HashMap<>();
        for (Object entity : entities) {
            Set<ConstraintViolation<Object>> constraintViolations = validator().get().validate(entity);
            for (ConstraintViolation violation : constraintViolations) {
                String key = violation.getPropertyPath().toString();
                errors.putIfAbsent(key, new ArrayList<>());
                errors.get(key).add(violation.getMessage());
            }
        }
        return errors;
    }

    default Supplier<Validator> validator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        return () -> validator;
    }
}
