package com.whiskybored.controllers;

import com.whiskybored.models.AppUser;
import com.whiskybored.models.Whisky;
import com.whiskybored.services.AppUserService;
import com.whiskybored.services.WhiskyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/whiskies")
public class WhiskyController {

    private final AppUserService appUserService;
    private final WhiskyService whiskyService;

    @Autowired
    public WhiskyController(AppUserService appUserService,WhiskyService whiskyService) {
        this.appUserService = appUserService;
        this.whiskyService = whiskyService;
    }

    @GetMapping
    public List<Whisky> getAllWhiskies() {
        return whiskyService.getAllWhiskies();
    }

    @GetMapping("/{whiskyId}")
    public Whisky getSingleWhisky(@PathVariable String whiskyId) throws Exception {
        return whiskyService.getSingleWhisky(whiskyId);
    }

    @PostMapping
    public ResponseEntity<Whisky> createNewWhisky(Principal principal, @RequestBody Whisky newWhisky) {
        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/whiskies")
                .toUriString()
        );
        AppUser appUser = appUserService.getUserByUsername(principal.getName());
        Whisky createdWhisky = whiskyService.createNewWhisky(newWhisky, appUser);
        return ResponseEntity.created(uri).body(createdWhisky);
    }

    @PutMapping("/{whiskyId}")
    public Whisky updateWhisky(
            Principal principal,
            @PathVariable String whiskyId,
            @RequestBody Whisky updatedWhisky
    ) throws Exception {
        updatedWhisky.setId(whiskyId);
        AppUser appUser = appUserService.getUserByUsername(principal.getName());
        return whiskyService.updateWhisky(updatedWhisky, appUser);
    }

    @DeleteMapping("/{whiskyId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteWhisky(Principal principal, @PathVariable String whiskyId) {
        AppUser appUser = appUserService.getUserByUsername(principal.getName());
        whiskyService.deleteWhisky(whiskyId, appUser);
    }
}
