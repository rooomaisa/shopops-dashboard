package com.shopops.config;

import com.shopops.entity.User;
import com.shopops.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoUserRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoUserRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoUserRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByEmail(SeedDataRunner.DEMO_EMAIL).ifPresentOrElse(
                user -> {
                    if (!passwordEncoder.matches(SeedDataRunner.DEMO_PASSWORD, user.getPassword())) {
                        user.setPassword(passwordEncoder.encode(SeedDataRunner.DEMO_PASSWORD));
                        user.setName(SeedDataRunner.DEMO_NAME);
                        userRepository.save(user);
                        log.info("Updated demo user password");
                    }
                },
                () -> {
                    User demo = new User();
                    demo.setName(SeedDataRunner.DEMO_NAME);
                    demo.setEmail(SeedDataRunner.DEMO_EMAIL);
                    demo.setPassword(passwordEncoder.encode(SeedDataRunner.DEMO_PASSWORD));
                    userRepository.save(demo);
                    log.info("Created demo user {}", SeedDataRunner.DEMO_EMAIL);
                }
        );
    }
}
