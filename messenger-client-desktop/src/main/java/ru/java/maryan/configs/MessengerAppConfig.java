package ru.java.maryan.configs;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "ru.java.maryan.login_register",
        "ru.java.maryan.file_storage",
        "ru.java.maryan.services",
        "ru.java.maryan.configs",
        "ru.java.maryan.messenger"
})
public class MessengerAppConfig {
}
