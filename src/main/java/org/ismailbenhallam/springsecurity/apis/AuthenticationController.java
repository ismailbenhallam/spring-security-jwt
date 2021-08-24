package org.ismailbenhallam.springsecurity.apis;

import org.ismailbenhallam.springsecurity.models.Role;
import org.ismailbenhallam.springsecurity.models.User;
import org.ismailbenhallam.springsecurity.security.MyUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
public class AuthenticationController {

    @GetMapping("/authenticated")
    public ResponseEntity<String> authenticatedUser(@AuthenticationPrincipal MyUserDetails userDetails) {
        User u = new User();
        u.setUsername(userDetails.getUsername());
        u.setAuthorities(userDetails.getAuthorities().stream().map(grantedAuthority -> Role.valueOf(grantedAuthority.getAuthority())).collect(Collectors.toList()));
        return ResponseEntity.ok(userDetails.getUsername());
    }
}
