package com.whiskybored;

import com.whiskybored.models.AppUser;
import com.whiskybored.models.Whisky;
import com.whiskybored.repositories.AppUserRepository;
import com.whiskybored.repositories.WhiskyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class WhiskyboredApplication {

    @Autowired
    private Environment env;

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

            appUserRepository.deleteAll();
            whiskyRepository.deleteAll();

            AppUser admin = AppUser.builder()
                    .username("admin")
                    .emailAddress("admin@email.com")
                    .password(bCryptPasswordEncoder().encode("admin"))
                    .build();

            Whisky dalmore = Whisky.builder()
                    .name("Dalmore 15 y.o.")
                    .origin("Scotland")
                    .image(env.getProperty("BASE_URL") + "static/images/dalmore.jpg")
                    .tastingNotes("Soft and very approachable, with dried fruit, hazelnuts and dry spiciness. Rich, classic Christmas-cake notes of raisins, currants, cherries, cinnamon and nutmeg. Sweet and raisiny, this rich-tasting dram lingers for a long time.")
                    .user(admin)
                    .build();


            Whisky bunnahabhain = Whisky.builder()
                    .name("Bunnahabhain 12 y.o.")
                    .origin("Scotland")
                    .image(env.getProperty("BASE_URL") + "/images/bunnahabhain.jpg")
                    .tastingNotes("A rich and fruity Islay single malt from Bunnahabhain, matured for 12 years in ex-sherry casks, creating aromas of dried fruit, sweet flowers, oak spice and rich smoke on the nose. The palate offers notes of roast hazelnuts, vanilla, caramel, raisins and dates that linger in the finish.")
                    .user(admin)
                    .build();

            Whisky yamazaki = Whisky.builder()
                    .name("Suntory Yamazaki Single Malt")
                    .origin("Japan")
                    .image(env.getProperty("BASE_URL") + "/images/yamazaki.jpg")
                    .tastingNotes("The full-bodied style is packed with plenty of nut oils and zest on the nose that's lightened with a little floral character and tropical fruit. The sweetness only builds on the palate, with a kick of winter spice and more tropical fruits in the background.")
                    .user(admin)
                    .build();

            appUserRepository.save(admin);
            whiskyRepository.saveAll(List.of(dalmore, bunnahabhain, yamazaki));

        };
    }

}
