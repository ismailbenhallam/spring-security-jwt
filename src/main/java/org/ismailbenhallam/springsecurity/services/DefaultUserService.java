package org.ismailbenhallam.springsecurity.services;

import org.ismailbenhallam.springsecurity.dao.UserRepository;
import org.ismailbenhallam.springsecurity.models.User;
import org.ismailbenhallam.springsecurity.security.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultUserService implements UserService, UserDetailsService {
    public static final String USERNAME_NOT_FOUND_MESSAGE = "Cannot find username %s";
    private final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User u) {
        return userRepository.save(u);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> allUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final var optionalUser = findByUsername(username);
        optionalUser.orElseThrow(() -> new UsernameNotFoundException(String.format(USERNAME_NOT_FOUND_MESSAGE, username)));
        return new MyUserDetails(optionalUser.get());
    }
}
