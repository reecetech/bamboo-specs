package au.com.reece.de.bamboospecs.models.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class InvalidSyntaxException extends RuntimeException {
    public InvalidSyntaxException(String s) {
        super(s);
    }

    public InvalidSyntaxException(JsonProcessingException e) {
        super(e);
    }
}
