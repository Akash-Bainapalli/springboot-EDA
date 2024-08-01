package net.projects.emailservice.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void send(String toSend, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toSend);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
