package spring.travel.api.model;

import java.util.Optional;

public class RemoteUser {

    private Optional<User> user;

    private String ipAddress;

    public RemoteUser() {
    }

    public RemoteUser(Optional<User> user, String ipAddress) {
        this.user = user;
        this.ipAddress = ipAddress;
    }

    public static RemoteUser from(String ipAddress) {
        return new RemoteUser(Optional.<User>empty(), ipAddress);
    }

    public static RemoteUser from(Optional<User> user, String ipAddress) {
        return new RemoteUser(user, ipAddress);
    }

    public Optional<User> getUser() {
        return user;
    }

    public void setUser(Optional<User> user) {
        this.user = user;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
