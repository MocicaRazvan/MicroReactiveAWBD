package com.example.userservice.email;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class CustomMailProps {
    private String host;
    private int port;
    private String username;
    private String password;
    private final Map<String, String> properties = new HashMap<>();
}
