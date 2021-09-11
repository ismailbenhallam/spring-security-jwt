package org.ismailbenhallam.springsecurity.services;

import org.ismailbenhallam.springsecurity.dao.UserRepository;
import org.ismailbenhallam.springsecurity.models.Role;
import org.ismailbenhallam.springsecurity.models.User;
import org.ismailbenhallam.springsecurity.security.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class DefaultUserService implements UserService, UserDetailsService {
    public static final String USERNAME_NOT_FOUND_MESSAGE = "Cannot find username %s";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DefaultUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User u) {
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        u.setId(0L);
        u.setActive(true);
        u.setAuthorities(Set.of(Role.USER));
        return userRepository.save(u);
    }

    @Override
    public User update(User u) {
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
