package org.ismailbenhallam.springsecurity.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtUtils {
    public static final String ROLES_CLAIM = "roles";
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";

    private JwtUtils() {
        throw new RuntimeException("Should not instantiate this class");
    }

    public static JWTCreator.Builder prepareTokenBuilder(HttpServletRequest request, String username, int expireMinutes) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(request.getRequestURL().toString())
                .withExpiresAt(getExpirationDate(LocalDateTime.now().plusMinutes(expireMinutes)));
    }

    public static Date getExpirationDate(LocalDateTime localDateTime) {
        return java.util.Date
                .from(localDateTime.atZone(ZoneId.systemDefault())
                        .toInstant());
    }
}
