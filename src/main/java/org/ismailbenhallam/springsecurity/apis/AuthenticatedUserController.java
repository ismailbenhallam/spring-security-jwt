package org.ismailbenhallam.springsecurity.apis;

import org.ismailbenhallam.springsecurity.security.MyUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticatedUserController {

    @GetMapping("/authenticated")
    public ResponseEntity<?> authenticatedUser2(@AuthenticationPrincipal MyUserDetails user) {
        return ResponseEntity.ok(user);
    }

    /*@GetMapping("/authenticated1")
    public ResponseEntity<?> authenticatedUser1() {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @GetMapping("/authenticated2")
    public ResponseEntity<?> authenticatedUser3(Authentication authentication) {
        return ResponseEntity.ok(authentication.getPrincipal());
    }*/
}
