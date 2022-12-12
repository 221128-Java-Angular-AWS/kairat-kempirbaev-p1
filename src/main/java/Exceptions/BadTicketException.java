package Exceptions;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;

public class BadTicketException extends JsonProcessingException {
    public BadTicketException(String msg, JsonLocation loc, Throwable rootCause) {
        super(msg, loc, rootCause);
    }

    public BadTicketException(String msg) {
        super(msg);
    }

    public BadTicketException(String msg, JsonLocation loc) {
        super(msg, loc);
    }

    public BadTicketException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }

    public BadTicketException(Throwable rootCause) {
        super(rootCause);
    }
}
