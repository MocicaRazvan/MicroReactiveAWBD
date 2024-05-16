package com.example.userservice.email.impl;

import com.example.userservice.email.EmailUtils;
//import com.sendgrid.Method;
//import com.sendgrid.Request;
//import com.sendgrid.Response;
//import com.sendgrid.SendGrid;
//import com.sendgrid.helpers.mail.Mail;
//import com.sendgrid.helpers.mail.objects.Content;
//import com.sendgrid.helpers.mail.objects.Email;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Component
@Slf4j
@RequiredArgsConstructor
public class EmailUtilsImpl implements EmailUtils {

//    @Value("${sendgrid.api.key}")
//    private String sendGridApiKey;
//
//    @Value("${sendgrid.sender}")
//    private String sendGridSender;

    private final JavaMailSender mailSender;

//    @Override
//    public Mono<Void> sendEmail(String to, String subject, String content) {
//        Email from = new Email(sendGridSender);
//        Email toEmail = new Email(to);
//        Content contentEmail = new Content("text/html", content);
//        Mail mail = new Mail(from, subject, toEmail, contentEmail);
//
//        SendGrid sendGrid = new SendGrid(sendGridApiKey);
//        Request request = new Request();
//
//        try {
//            request.setMethod(Method.POST);
//            request.setEndpoint("mail/send");
//            request.setBody(mail.build());
//            Response response = sendGrid.api(request);
//            log.info("Email sent to: " + to + " with status code: " + response.getStatusCode());
//
//            return Mono.empty();
//        } catch (Exception ex) {
//            log.error("Error sending email to: " + to, ex);
//            return Mono.error(ex);
//        }
//
//    }

    @Override
    public Mono<Void> sendEmail(String to, String subject, String content) {
        return Mono.defer(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(content, true);
                mailSender.send(mimeMessage);
                log.info("Email sent to: " + to);
                return Mono.empty();
            } catch (Exception ex) {
                log.error("Error sending email to: " + to, ex);
                return Mono.error(ex);
            }
        });
    }
}
