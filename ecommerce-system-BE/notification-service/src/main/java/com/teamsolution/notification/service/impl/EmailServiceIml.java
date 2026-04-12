package com.teamsolution.notification.service.impl;

import com.teamsolution.common.core.exception.AppException;
import com.teamsolution.notification.exception.ErrorCode;
import com.teamsolution.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceIml implements EmailService {

  private final JavaMailSender mailSender;

  @Override
  public void send(String to, String subject, String body) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(body, false);

      mailSender.send(message);

    } catch (MessagingException e) {
      log.error("Failed to send email to: {}", to, e);
      throw new AppException(ErrorCode.EMAIL_SEND_FAILED);
    }
  }
}
