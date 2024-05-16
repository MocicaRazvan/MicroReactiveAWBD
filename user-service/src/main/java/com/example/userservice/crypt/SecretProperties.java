package com.example.userservice.crypt;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "encoding")
@PropertySource("classpath:secret.properties")
@Data
public class SecretProperties {
    private String secret;
    private String springMailPassword;
}
