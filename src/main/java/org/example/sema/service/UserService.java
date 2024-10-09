package org.example.sema.service;

import org.example.sema.dtos.RegisterUserDTO;
import org.example.sema.entities.ApplicationUser;
import org.example.sema.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ApplicationUser findUserByUsername(String username) {
        Optional<ApplicationUser> optionalUser = userRepository.findByUsername(username);

        return optionalUser.orElse(null);

    }

    public void updateUser(String username, RegisterUserDTO updatedUserDto) throws Exception {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        if (updatedUserDto.getUsername() != null && !updatedUserDto.getUsername().isEmpty()) {
            user.setUsername(updatedUserDto.getUsername());
        }

        if (updatedUserDto.getEmail() != null && !updatedUserDto.getEmail().isEmpty()) {
            user.setEmail(updatedUserDto.getEmail());
        }

        if (updatedUserDto.getPassword() != null && !updatedUserDto.getPassword().isEmpty()) {
            user.setPassword(updatedUserDto.getPassword());
        }

        userRepository.save(user);
    }

    public void deleteUser(String username) throws Exception {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));
        userRepository.delete(user);
    }

}
