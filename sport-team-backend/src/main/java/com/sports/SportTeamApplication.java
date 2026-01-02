package com.sports;

import com.sports.entity.Spot;
import com.sports.entity.User;
import com.sports.repository.SpotRepository;
import com.sports.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class SportTeamApplication {

    public static void main(String[] args) {
        SpringApplication.run(SportTeamApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepository, SpotRepository spotRepository) {
        return args -> {
            // Initialize Admin User
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String defaultPassword = "password";
            String encodedPassword = encoder.encode(defaultPassword);

            User admin = userRepository.findByUsername("admin").orElse(null);
            if (admin == null) {
                admin = User.builder()
                        .username("admin")
                        .password(encodedPassword)
                        .level(2)
                        .phoneNum("13800000000")
                        .stuId("2024001")
                        .gender(User.Gender.OTHER)
                        .build();
                userRepository.save(admin);
                System.out.println("Admin user created: admin / password");
            } else {
                admin.setPassword(encodedPassword);
                if (admin.getLevel() == null) {
                    admin.setLevel(2);
                }
                userRepository.save(admin);
                System.out.println("Admin password reset to: password");
            }

            // Initialize Spots
            if (spotRepository.count() == 0) {
                List<Spot> spots = Arrays.asList(
                    Spot.builder().id(1).address("学校体育馆").status(1).build(),
                    Spot.builder().id(2).address("足球场").status(1).build(),
                    Spot.builder().id(3).address("羽毛球馆").status(1).build(),
                    Spot.builder().id(4).address("游泳池").status(1).build()
                );
                spotRepository.saveAll(spots);
                System.out.println("Initialized 4 default spots.");
            }
        };
    }
}
