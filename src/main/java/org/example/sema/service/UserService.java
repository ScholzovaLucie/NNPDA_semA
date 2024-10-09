package org.example.sema.service;

import org.example.sema.entities.ApplicationUser;
import org.example.sema.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ApplicationUser> allUsers() {
        List<ApplicationUser> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
}
