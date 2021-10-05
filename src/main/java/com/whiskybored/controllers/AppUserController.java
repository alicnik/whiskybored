package com.whiskybored.controllers;

import com.whiskybored.models.AppUser;
import com.whiskybored.services.AppUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
@Slf4j
public class AppUserController {

    private final AppUserService appUserService;

    @Autowired
    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/users/{username}")
    public AppUser getSingleUser(Principal principal, @PathVariable String username) {
        AppUser appUser = appUserService.getUserByUsername(principal.getName());
        if (!username.equals(appUser.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't get another user's details");
        }
        return appUserService.getSingleUser(username);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AppUser appUser) throws ResponseStatusException {
        if (!appUser.getPassword().equals(appUser.getPasswordConfirmation())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password does not match password confirmation");
        }
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/register")
                .toUriString()
        );
        appUserService.createUser(appUser);
        return ResponseEntity.created(uri).body("{\"message\": \"Registration successful\"}");
    }
}
