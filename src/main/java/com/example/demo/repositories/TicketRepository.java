package com.example.demo.repositories;

import com.example.demo.models.TicketModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketModel, Long> {
}
