package org.ismailbenhallam.springsecurity;

import org.ismailbenhallam.springsecurity.dao.UserRepository;
import org.ismailbenhallam.springsecurity.models.Role;
import org.ismailbenhallam.springsecurity.models.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.ismailbenhallam.springsecurity.dao")
public class SpringSecurityApplication {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SpringSecurityApplication(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityApplication.class, args);
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            var user = new User();
            user.setUsername("u");
            user.setPassword(passwordEncoder.encode("u"));
            user.setActive(true);
            user.setAuthorities(List.of(Role.USER));
            userRepository.save(user);

            user = new User();
            user.setUsername("i");
            user.setPassword(passwordEncoder.encode("i"));
            user.setActive(true);
            user.setAuthorities(List.of(Role.USER, Role.ADMIN));
            userRepository.save(user);
        };
    }

}
