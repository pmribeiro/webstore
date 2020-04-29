package pt.pmribeiro.webstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * {@code DataIntegrityViolationException} is the exception that can be thrown
 * in case of a entity violates the integrity of the data in the database
 *
 * Created by pribeiro on 26/11/2016.
 */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class DataIntegrityViolationException extends RuntimeException {

    /**
     * Constructs a new validation exception with the specified detail message
     *
     * @param message detailed message of the exception
     */
    public DataIntegrityViolationException(String message) {
        super(message);
    }

}
