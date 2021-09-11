package org.ismailbenhallam.springsecurity.apis;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ismailbenhallam.springsecurity.models.Role;
import org.ismailbenhallam.springsecurity.services.UserService;
import org.ismailbenhallam.springsecurity.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.ismailbenhallam.springsecurity.filters.MyAuthorizationFilter.BEARER_SPACE;
import static org.ismailbenhallam.springsecurity.utils.JwtUtils.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class RefreshTokenController {
    public static final String REFRESH_TOKEN_PATH = "/token/refresh";
    private final UserService userService;

    @Autowired
    private Algorithm algorithm;

    @Value("${jwt.access.token.expire.after.minutes}")
    private int accessTokenExpireMinutes;

    public RefreshTokenController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(REFRESH_TOKEN_PATH)
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var header = request.getHeader(AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_SPACE)) {
            try {
                final var refreshToken = header.substring(BEARER_SPACE.length());
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refreshToken);
                final var username = decodedJWT.getSubject();

                final var user = userService.findByUsername(username).get();
                // Create access & refresh tokens
                String accessToken = JwtUtils.prepareTokenBuilder(request, user.getUsername(), accessTokenExpireMinutes)
                        .withClaim(ROLES_CLAIM, user.getAuthorities().stream().map(Role::name).collect(Collectors.toList()))
                        .sign(algorithm);

                // Send the response
                final var map = new HashMap<String, String>();
                map.put(ACCESS_TOKEN_KEY, accessToken);
                map.put(REFRESH_TOKEN_KEY, refreshToken);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), map);
            } catch (Exception e) {
                e.printStackTrace();
                final var map = new HashMap<String, String>();
                map.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                new ObjectMapper().writeValue(response.getOutputStream(), map);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }

}
