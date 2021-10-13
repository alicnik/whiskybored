package com.whiskybored.services;

import com.whiskybored.models.AppUser;
import com.whiskybored.repositories.AppUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class AppUserService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AppUserService(AppUserRepository appUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.appUserRepository = appUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public AppUser getUserByUsername(String username) throws ResponseStatusException {
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        if (appUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user with that username exists.");
        }
        return appUser.get();
    }

    public void createUser(AppUser appUser) throws ResponseStatusException {
        Optional<AppUser> existingUser = appUserRepository.findByUsername(appUser.getUsername());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user with that username already exists");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);
    }

    public AppUser getSingleUser(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        if (appUser.isEmpty()) {
            throw new UsernameNotFoundException("No user with that username exists");
        }
        return appUser.get();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("PRINTING THE USERNAME: " + username);
        Optional<AppUser> appUser = appUserRepository.findByUsername(username);
        if (appUser.isEmpty()) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        } else {
            log.info("User found: {}", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        return new User(appUser.get().getUsername(), appUser.get().getPassword(), authorities);
    }

}
