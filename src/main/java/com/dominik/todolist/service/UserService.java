package com.dominik.todolist.service;

import com.dominik.todolist.dto.RegisterRequest;
import com.dominik.todolist.exception.EmailAlreadyExistsException;
import com.dominik.todolist.model.AppUser;
import com.dominik.todolist.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser registerUser(RegisterRequest registerRequest) {
        if (appUserRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Error: Email '" + registerRequest.getEmail() + "' is already taken!");
        }

        AppUser newAppUser = new AppUser();
        newAppUser.setName(registerRequest.getName());
        newAppUser.setEmail(registerRequest.getEmail());
        newAppUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        return appUserRepository.save(newAppUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new User(
                appUser.getEmail(),
                appUser.getPassword(),
                new ArrayList<>()
        );
    }
}
