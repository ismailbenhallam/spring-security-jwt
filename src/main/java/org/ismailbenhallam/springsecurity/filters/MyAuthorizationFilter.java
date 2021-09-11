package org.ismailbenhallam.springsecurity.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ismailbenhallam.springsecurity.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.ismailbenhallam.springsecurity.apis.RefreshTokenController.REFRESH_TOKEN_PATH;
import static org.ismailbenhallam.springsecurity.utils.JwtUtils.ROLES_CLAIM;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class MyAuthorizationFilter extends OncePerRequestFilter {
    public static final String BEARER_SPACE = "Bearer ";

    @Autowired
    private Algorithm algorithm;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        final var header = request.getHeader(AUTHORIZATION);
        if (!request.getServletPath().equals(REFRESH_TOKEN_PATH) && header != null && header.startsWith(BEARER_SPACE)) {
            try {
                final var token = header.substring(BEARER_SPACE.length());
                final var jwtVerifier = JWT.require(algorithm).build();
                final var decodedJWT = jwtVerifier.verify(token);
                final var username = decodedJWT.getSubject();
                final var roles = decodedJWT.getClaim(ROLES_CLAIM).asList(String.class);
                final var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                final MyUserDetails user = new MyUserDetails();
                user.setUsername(username);
                user.setActive(true);
                user.setAuthorities(authorities);

                final var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(request, response);
            } catch (Exception e) {
                final var map = new HashMap<String, String>();
                map.put("error_message", e.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                new ObjectMapper().writeValue(response.getOutputStream(), map);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
