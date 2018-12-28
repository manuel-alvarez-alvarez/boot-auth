package es.manuel.nginx.bootauth.exception;

import org.springframework.context.MessageSourceResolvable;

public class AuthException extends RuntimeException implements MessageSourceResolvable {

    public AuthException(final String message) {
        super(message);
    }

    public AuthException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AuthException(final Throwable cause) {
        super(cause.getMessage(), cause);
    }

    @Override
    public String[] getCodes() {
        return new String[0];
    }

    @Override
    public Object[] getArguments() {
        return new Object[0];
    }

    @Override
    public String getDefaultMessage() {
        return getMessage();
    }
}
