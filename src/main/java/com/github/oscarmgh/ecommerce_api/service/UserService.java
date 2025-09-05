package com.github.oscarmgh.ecommerce_api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.github.oscarmgh.ecommerce_api.entity.User;
import com.github.oscarmgh.ecommerce_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean promoteToAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setRoles(new HashSet<>(List.of("ROLE_ADMIN")));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public boolean demoteToUser(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setRoles(new HashSet<>(List.of("ROLE_USER")));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
