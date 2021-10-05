package com.whiskybored.services;

import com.whiskybored.models.AppUser;
import com.whiskybored.models.Whisky;
import com.whiskybored.repositories.WhiskyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class WhiskyService {

    private final WhiskyRepository whiskyRepository;

    @Autowired
    public WhiskyService(WhiskyRepository whiskyRepository) {
        this.whiskyRepository = whiskyRepository;
    }

    public List<Whisky> getAllWhiskies() {
        return whiskyRepository.findAll();
    }

    public Whisky getSingleWhisky(String cardId) {
        Optional<Whisky> foundCard = whiskyRepository.findById(cardId);
        if (foundCard.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found");
        }
        return foundCard.get();
    }

    public Whisky createNewWhisky(Whisky newWhisky, AppUser user) {
        Whisky whisky = Whisky.builder()
                .name(newWhisky.getName())
                .origin(newWhisky.getOrigin())
                .image(newWhisky.getImage())
                .tastingNotes(newWhisky.getTastingNotes())
                .user(user)
                .build();
        return whiskyRepository.save(whisky);
    }

    public Whisky updateWhisky(Whisky updatedWhisky, AppUser appUser) {
        Optional<Whisky> existingWhisky = whiskyRepository.findById(updatedWhisky.getId());
        if (existingWhisky.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Whisky not found");
        }
        if (existingWhisky.get().getUser() != appUser) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't drink someone else's whisky");
        }
        updatedWhisky.setUser(appUser);
        return whiskyRepository.save(updatedWhisky);
    }

    public void deleteWhisky(String whiskyId, AppUser appUser) {
        Optional<Whisky> existingWhisky = whiskyRepository.findById(whiskyId);
        if (existingWhisky.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Whisky not found");
        }
        if (existingWhisky.get().getUser() != appUser) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't drink someone else's whisky");
        }
        whiskyRepository.deleteById(whiskyId);
    }
}
