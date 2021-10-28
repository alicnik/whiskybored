package com.whiskybored.integration;

import com.whiskybored.models.AppUser;
import com.whiskybored.models.Whisky;
import com.whiskybored.repositories.AppUserRepository;
import com.whiskybored.repositories.WhiskyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-it.properties")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class WhiskyIT {

    @Autowired
    private Environment env;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    WhiskyRepository whiskyRepository;

    @BeforeEach
    public void setup() {
        AppUser admin = AppUser.builder()
                .username("admin")
                .emailAddress("admin@email.com")
                .password("admin")
                .build();

        Whisky dalmore = Whisky.builder()
                .name("Dalmore 15 y.o.")
                .origin("Scotland")
                .image(env.getProperty("BASE_URL") + "/images/dalmore.jpg")
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

        Whisky caolIla = Whisky.builder()
                .name("Caol Ila 12 Year Old")
                .origin("Scotland")
                .image(env.getProperty("BASE_URL") + "/images/caol-ila.jpg")
                .tastingNotes("Caol Ila 12 Year Old is of medium weight, but still packing plenty of potent phenols, this is a refined, powerful dram with a compensating oiliness. A balanced, peaty beauty.")
                .user(admin)
                .build();

        appUserRepository.save(admin);
        whiskyRepository.saveAll(List.of(dalmore, bunnahabhain, yamazaki));
    }

    @Test
    public void getAllStation() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/whiskies");

        mockMvc.perform(request).andExpect(status().isOk());
    }
}
