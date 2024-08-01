package net.projects.stockservice.kafka;

import net.projects.basedomains.dto.OrderEvent;
import net.projects.stockservice.entity.Order;
import net.projects.stockservice.repository.OrderEventRepository;
import net.projects.stockservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderEventRepository orderEventRepository;

    @KafkaListener(groupId = "${spring.kafka.consumer.groupId}",
    topics = "${spring.kafka.topic}")
    @Transactional
    public void consume(OrderEvent orderEvent) {
        try {
            LOGGER.info(String.format("Order Event Received => %s", orderEvent.toString()));
            // save into the db
            Order order = new Order(
                    orderEvent.getOrder().getOrderId(),
                    orderEvent.getOrder().getOrderName(),
                    orderEvent.getOrder().getQuantity(),
                    orderEvent.getOrder().getPrice()
            );

            orderRepository.save(order);

            net.projects.stockservice.entity.OrderEvent orderEventEntity = new net.projects.stockservice.entity.OrderEvent();
            orderEventEntity.setMessage(orderEvent.getMessage());
            orderEventEntity.setStatus(orderEvent.getStatus());
            orderEventEntity.setOrder(orderRepository.findById(order.getOrderId()).get());

            orderEventRepository.save(orderEventEntity);
        } catch (Exception e) {
            LOGGER.error("Error processing message", e);
            throw new RuntimeException(e);
        }
    }
}
