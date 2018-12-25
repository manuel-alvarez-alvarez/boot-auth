package es.manuel.nginx.bootauth.exception;

public class UserAlreadyExistsException extends AuthException {

    public UserAlreadyExistsException(final String username, final Throwable e) {
        super(String.format("User with name \"%s\" already exists", username), e);
    }

    public UserAlreadyExistsException(final String username) {
        this(username, null);
    }
}
