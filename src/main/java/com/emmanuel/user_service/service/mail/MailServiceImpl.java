package com.emmanuel.user_service.service.mail;

import com.emmanuel.user_service.dto.request.MailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
  private final JavaMailSender mailSender;

  @Override
  @Async
  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
  }

  @Override
  @Async
  public void sendHtmlEmailFromFile(MailRequest mailRequest)
      throws MessagingException, IOException {
    // Load HTML file from resources folder
    ClassPathResource resource = new ClassPathResource(mailRequest.htmlFilePath());
    String htmlBody = Files.readString(Path.of(resource.getURI()));
    htmlBody = htmlBody.replace("${userName}", mailRequest.user().getFirstName());
    htmlBody = htmlBody.replace("${activationLink}", mailRequest.activationUrl());

    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(mailRequest.to());
    helper.setSubject(mailRequest.subject());
    helper.setText(htmlBody, true);
    mailSender.send(message);
  }
}
