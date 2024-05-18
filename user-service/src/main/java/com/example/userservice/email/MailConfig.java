package com.example.userservice.email;


import com.example.userservice.crypt.Crypt;
import com.example.userservice.crypt.SecretProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MailConfig {


    private final CustomMailProps customMailProps;
    private final SecretProperties secretProperties;

    @Bean
    public JavaMailSender javaMailSender() throws Exception {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(customMailProps.getHost());
        mailSender.setPort(customMailProps.getPort());
        mailSender.setUsername(customMailProps.getUsername());
        mailSender.setPassword(Crypt.decryptPassword(secretProperties.getSpringMailPassword(), secretProperties.getSecret()));
        mailSender.getJavaMailProperties().putAll(customMailProps.getProperties());
        return mailSender;
    }
}
