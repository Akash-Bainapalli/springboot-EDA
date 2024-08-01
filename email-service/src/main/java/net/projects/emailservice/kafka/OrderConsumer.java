package net.projects.emailservice.kafka;

import net.projects.basedomains.dto.OrderEvent;
import net.projects.emailservice.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private EmailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailID;

    @KafkaListener(groupId = "${spring.kafka.consumer.groupId}",
    topics = "${spring.kafka.topic}")
    @Transactional
    public void consume(OrderEvent orderEvent) {
        try {
            LOGGER.info(String.format("Order Event Received => %s", orderEvent.toString()));

            // Send email
            String to = emailID; // Assuming the Order contains an email field
            String subject = "New Order Event Notification";
            String body = String.format(
                    "Order ID: %s\n" +
                            "Order Name: %s\n" +
                            "Quantity: %d\n" +
                            "Price: %.2f\n" +
                            "Message: %s\n" +
                            "Status: %s",
                    orderEvent.getOrder().getOrderId(),
                    orderEvent.getOrder().getOrderName(),
                    orderEvent.getOrder().getQuantity(),
                    orderEvent.getOrder().getPrice(),
                    orderEvent.getMessage(),
                    orderEvent.getStatus()
            );
            try {
                emailSender.send(to, subject, body);
                LOGGER.info("Email sent successfully to {}", to);
            } catch (Exception e) {
                LOGGER.error("Failed to send email to {}: {}", to, e.getMessage());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing message", e);
            throw new RuntimeException(e);
        }
    }
}
