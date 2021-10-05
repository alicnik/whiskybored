package com.whiskybored;

import com.whiskybored.models.AppUser;
import com.whiskybored.models.Whisky;
import com.whiskybored.repositories.AppUserRepository;
import com.whiskybored.repositories.WhiskyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@SpringBootApplication
public class WhiskyboredApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhiskyboredApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner commandLineRunner(
            AppUserRepository appUserRepository,
            WhiskyRepository whiskyRepository
    ) {
        return args -> {

            AppUser admin = AppUser.builder()
                    .username("admin")
                    .emailAddress("admin@email.com")
                    .password(bCryptPasswordEncoder().encode("admin"))
                    .build();

            Whisky dalmore = Whisky.builder()
                    .name("Dalmore 15 y.o.")
                    .origin("Scotland")
                    .image("https://m.media-amazon.com/images/I/41y9+wmK2bL._AC_.jpg")
                    .tastingNotes("Soft and very approachable, with dried fruit, hazelnuts and dry spiciness. Rich, classic Christmas-cake notes of raisins, currants, cherries, cinnamon and nutmeg. Sweet and raisiny, this rich-tasting dram lingers for a long time.")
                    .user(admin)
                    .build();

            Whisky bunnahabhain = Whisky.builder()
                    .name("Bunnahabhain 12 y.o.")
                    .origin("Scotland")
                    .image("https://img.thewhiskyexchange.com/900/bunob.12yov17.jpg")
                    .tastingNotes("A rich and fruity Islay single malt from Bunnahabhain, matured for 12 years in ex-sherry casks, creating aromas of dried fruit, sweet flowers, oak spice and rich smoke on the nose. The palate offers notes of roast hazelnuts, vanilla, caramel, raisins and dates that linger in the finish.")
                    .user(admin)
                    .build();

            appUserRepository.save(admin);
            whiskyRepository.saveAll(List.of(dalmore, bunnahabhain));

        };
    }

}
