package com.nazarukiv.dentalclinicsystem.config;

import com.nazarukiv.dentalclinicsystem.entity.Role;
import com.nazarukiv.dentalclinicsystem.service.UserService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupUserInitializer implements ApplicationRunner {

    private final UserService userService;

    public StartupUserInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) {
        userService.createUserIfMissing("admin", "admin123", Role.ADMIN);
        userService.createUserIfMissing("receptionist", "reception123", Role.RECEPTIONIST);
    }
}
