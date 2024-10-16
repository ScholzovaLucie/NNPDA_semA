package org.example.sema.service;

import org.example.sema.dto.UpdateUserDTO;
import org.example.sema.entity.ApplicationUser;
import org.example.sema.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<ApplicationUser> allUsers(){
        return (List<ApplicationUser>) userRepository.findAll();
    }

    public void updateUser(UpdateUserDTO updatedUserDto) throws Exception {
        ApplicationUser user = userRepository.findById(updatedUserDto.getId())
                .orElseThrow(() -> new Exception("User not found"));

        if (updatedUserDto.getUsername() != null && !updatedUserDto.getUsername().isEmpty()) {
            user.setUsername(updatedUserDto.getUsername());
        }

        if (updatedUserDto.getEmail() != null && !updatedUserDto.getEmail().isEmpty()) {
            user.setEmail(updatedUserDto.getEmail());
        }

        userRepository.save(user);
    }

    public void deleteUser(Long id) throws Exception {
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("User not found"));
        userRepository.delete(user);
    }

}
