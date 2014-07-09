package spring.travel.api.auth;

public class AuthException extends Exception {

    public AuthException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
