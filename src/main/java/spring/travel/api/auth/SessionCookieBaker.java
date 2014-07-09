package spring.travel.api.auth;

import java.util.Map;

public interface SessionCookieBaker {

    String encode(Map<String, String> values) throws AuthException;

    Map<String, String> decode(String cookie) throws AuthException;
}
