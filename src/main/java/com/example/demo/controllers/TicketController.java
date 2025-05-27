package com.example.demo.controllers;

import com.example.demo.controllers.dto.ContentResponsDto;
import com.example.demo.models.TicketModel;
import com.example.demo.services.TicketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> subirTicketPdf(@RequestParam("file") MultipartFile file) {
        try {
            // Subir el PDF y obtener sourceId
            String respuestaJson = ticketService.uploadPdf(file);

            JsonNode root = objectMapper.readTree(respuestaJson);
            String sourceId = root.get("sourceId").asText();

            // Consultar con prompt para obtener el JSON limpio en string
            ContentResponsDto response = ticketService.consultarPdfConPrompt(sourceId);

            // Limpiar el contenido de comillas invertidas
            String content = response.getContent().trim();
            content = content.replaceAll("```(json)?", "").trim();

            // Mapear el JSON limpio a objeto TicketModel usando el ObjectMapper configurado
            TicketModel ticket = objectMapper.readValue(content, TicketModel.class);

            // Guardar el ticket en BD (opcional)
            ticketService.crearTicket(ticket);

            // Retornar el objeto TicketModel directamente
            return ResponseEntity.ok(ticket);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al procesar el PDF: " + e.getMessage());
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
