package es.manuel.nginx.bootauth.exception;

public class UserNotFoundException extends AuthException {

    public UserNotFoundException(final String username, final Throwable e) {
        super(String.format("User with name \"%s\" not found", username), e);
    }

    public UserNotFoundException(final String username) {
        this(username, null);
    }
}
