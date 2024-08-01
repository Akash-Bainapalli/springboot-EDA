package net.projects.stockservice.repository;

import net.projects.stockservice.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEventRepository extends JpaRepository<OrderEvent,Long> {
}
