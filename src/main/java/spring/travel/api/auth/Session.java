package spring.travel.api.auth;

public class Session {

    private final String value;

    public Session(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return value;
    }
}
