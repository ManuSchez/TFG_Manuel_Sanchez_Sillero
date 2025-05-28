package com.example.demo.repositories;

import com.example.demo.models.TicketModel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<TicketModel, Long> {
    List<TicketModel> findByUsuarioId(Long usuarioId);
}
