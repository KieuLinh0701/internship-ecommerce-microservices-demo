package com.teamsolution.notification.service;

public interface EmailService {
  void send(String to, String subject, String body);
}
