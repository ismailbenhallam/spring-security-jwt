package org.ismailbenhallam.springsecurity.services;

import org.ismailbenhallam.springsecurity.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User u);

    User update(User u);

    Optional<User> findByUsername(String usename);

    List<User> allUsers();
}
