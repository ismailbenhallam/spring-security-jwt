package org.ismailbenhallam.springsecurity.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.ismailbenhallam.springsecurity.models.User;
import org.ismailbenhallam.springsecurity.services.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
@CommonsLog
public class SignUpController {
    private final UserService userService;

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveUser(@ModelAttribute UserRequest userRequest) {
        try {
            final var user = userService.save(userRequest.toUser());
            final var uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path(String.format("/users/%s", user.getUsername())).toUriString());
            return ResponseEntity.created(uri).body(user);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getRootCause());
            ObjectMapper objectMapper = new ObjectMapper();
            final var body = objectMapper.createObjectNode().put("error_message", "Username already taken").toString();
            return ResponseEntity.badRequest().body(body);
        }
    }

    @Data
    private static class UserRequest {
        private String firstName;
        private String lastName;
        private String username;
        private String password;

        User toUser() {
            final var user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setUsername(username);
            user.setPassword(password);
            return user;
        }
    }
}
