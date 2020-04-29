package pt.pmribeiro.webstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * {@code ValidationException} is the exception that can be thrown
 * in case of a field in DTO doesn't match the required criteria
 *
 * Created by pribeiro on 26/11/2016.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private Errors errors;

    /**
     * Constructs a new validation exception with the specified detail message
     * and the errors found
     *
     * @param message detailed message of the exception
     * @param errors errors found
     */
    public ValidationException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() { return errors; }

}
