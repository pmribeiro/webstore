package pt.pmribeiro.webstore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * {@code NotFoundException} is the exception that can be thrown
 * in case of a entity not found in database
 *
 * Created by pribeiro on 26/11/2016.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new validation exception with the specified detail message
     *
     * @param message detailed message of the exception
     */
    public NotFoundException(String message) {
        super(message);
    }

}
