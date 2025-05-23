package com.example.demo.controllers;

import com.example.demo.models.TicketModel;
import com.example.demo.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketModel> crearTicket(@RequestBody TicketModel ticket) {
        TicketModel ticketCreado = ticketService.crearTicket(ticket);
        return ResponseEntity.ok(ticketCreado);
    }

    @PostMapping("/upload-pdf")
    public ResponseEntity<?> subirTicketPdf(@RequestParam("file") MultipartFile file) {
        try {
            String respuestaJson = ticketService.uploadPdf(file);
            return ResponseEntity.ok(respuestaJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al procesar el PDF: " + e.getMessage());
        }
    }

    @PostMapping("/query")
    public ResponseEntity<?> consultarPdf(@RequestParam String sourceId, @RequestParam String prompt) {
        try {
            String respuestaJson = ticketService.consultarPdfConPrompt(sourceId, prompt);

            TicketModel ticket = ticketService.parsearRespuestaYCrearTicket(respuestaJson);
            TicketModel ticketGuardado = ticketService.crearTicket(ticket);

            return ResponseEntity.ok(ticketGuardado);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al consultar el PDF: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<TicketModel>> obtenerTodos() {
        return ResponseEntity.ok(ticketService.obtenerTodos());
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
