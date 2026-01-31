package com.emmanuel.user_service.service.mail;

import com.emmanuel.user_service.dto.request.MailRequest;
import jakarta.mail.MessagingException;
import java.io.IOException;

public interface MailService {
  void sendEmail(String to, String subject, String body);

  void sendHtmlEmailFromFile(MailRequest mailRequest) throws MessagingException, IOException;
}
