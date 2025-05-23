package com.example.demo.services;

import com.example.demo.models.TicketModel;

import com.example.demo.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Optional;


@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public TicketModel crearTicket(TicketModel ticket) {
        return ticketRepository.save(ticket);
    }

    public Optional<TicketModel> actualizarTicket(Long id, TicketModel actualizado) {
        return ticketRepository.findById(id)
            .map(ticket -> {
                ticket.setFecha(actualizado.getFecha());
                ticket.setFormaPago(actualizado.getFormaPago());
                ticket.setSupermercado(actualizado.getSupermercado());
                ticket.setPrecioTotal(actualizado.getPrecioTotal());
                return ticketRepository.save(ticket);
            });
    }

    public void eliminarTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    public java.util.List<TicketModel> obtenerTodos() {
        return ticketRepository.findAll();
    }

    public Optional<TicketModel> obtenerPorId(Long id) {
        return ticketRepository.findById(id);
    }

    public String uploadPdf (MultipartFile file) throws IOException {
        String apiKey = "sec_ufpye2BYKXbY0FjFhnHBtNlhanqV4zNB";

        // Construir el cuerpo de la solicitud
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("x-api-key", apiKey);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Enviar solicitud
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.chatpdf.com/v1/sources/add-file",
                requestEntity,
                String.class
        );

        return response.getBody();
    }
}