package com.example.userservice.email;


import com.example.userservice.crypt.Crypt;
import com.example.userservice.crypt.SecretProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
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
        log.info("Mail properties: " + customMailProps);
        log.error("Mail properties: " + secretProperties.getSpringMailPassword());
        log.error("Crypt pass" + secretProperties.getSecret());
//        log.error("Crypt pass" + Crypt.decryptPassword(secretProperties.getSpringMailPassword(), secretProperties.getSecret()));
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(customMailProps.getHost());
        mailSender.setPort(customMailProps.getPort());
        mailSender.setUsername(customMailProps.getUsername());
        mailSender.setPassword(Crypt.decryptPassword(secretProperties.getSpringMailPassword(), secretProperties.getSecret()));
//        mailSender.setPassword(customMailProps.getPassword());
        mailSender.getJavaMailProperties().putAll(customMailProps.getProperties());
        return mailSender;
    }
}
