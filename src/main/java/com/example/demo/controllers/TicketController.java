package com.example.demo.controllers;

import com.example.demo.controllers.dto.ContentResponsDto;
import com.example.demo.models.TicketModel;
import com.example.demo.services.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<TicketModel> crearTicket(@RequestBody TicketModel ticket) {
        TicketModel ticketCreado = ticketService.crearTicket(ticket);
        return ResponseEntity.ok(ticketCreado);
    }

    @PostMapping("/upload-pdf")
    public ResponseEntity<TicketModel> subirTicketPdf(@RequestParam("file") MultipartFile file, @RequestParam("idUsuario") Long idUsuario) {
        try {
            // Subir el PDF y obtener sourceId
            String respuestaJson = ticketService.uploadPdf(file);

            JsonNode root = objectMapper.readTree(respuestaJson);
            String sourceId = root.get("sourceId").asText();

            TicketModel ticket = ticketService.procesarTicket(sourceId,idUsuario);
           
            return ResponseEntity.ok(ticket);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("misTickets/{idUsuario}")
    public ResponseEntity<List<TicketModel>> obtenerMisTickets(@PathVariable Long idUsuario) {

        return ResponseEntity.ok(ticketService.obtenerMisTickets(idUsuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketModel> obtenerTicketPorId(@PathVariable Long id) {
        Optional<TicketModel> ticket = ticketService.obtenerPorId(id);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketModel> actualizarTicket(@PathVariable Long id, @RequestBody TicketModel ticketActualizado) {
        Optional<TicketModel> ticket = ticketService.actualizarTicket(id, ticketActualizado);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTicket(@PathVariable Long id) {
        ticketService.eliminarTicket(id);
        return ResponseEntity.noContent().build();
    }
}
