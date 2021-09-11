package org.ismailbenhallam.springsecurity.filters;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ismailbenhallam.springsecurity.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.ismailbenhallam.springsecurity.utils.JwtUtils.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class MyAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    @Autowired
    private Algorithm algorithm;

    @Value("${jwt.access.token.expire.after.minutes}")
    private int accessTokenExpireMinutes;

    @Value("${jwt.refresh.token.expire.after.minutes}")
    private int refreshTokenExpireMinutes;

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        MyUserDetails user = (MyUserDetails) authResult.getPrincipal();

        // Create access & refresh tokens
        String accessToken = prepareTokenBuilder(request, user.getUsername(), accessTokenExpireMinutes)
                .withClaim(ROLES_CLAIM, user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
        String refreshToken = prepareTokenBuilder(request, user.getUsername(), refreshTokenExpireMinutes)
                .sign(algorithm);

        // Send the response
        final var map = new HashMap<String, String>();
        map.put(ACCESS_TOKEN_KEY, accessToken);
        map.put(REFRESH_TOKEN_KEY, refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), map);
    }

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

}
