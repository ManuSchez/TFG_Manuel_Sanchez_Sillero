package com.example.demo.services;

import com.example.demo.models.TicketModel;
import com.example.demo.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    private final String apiKey = "sec_ufpye2BYKXbY0FjFhnHBtNlhanqV4zNB";

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

    public String uploadPdf(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("x-api-key", apiKey);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.chatpdf.com/v1/sources/add-file",
                requestEntity,
                String.class
        );

        return response.getBody();
    }

    public String consultarPdfConPrompt(String sourceId, String prompt) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("sourceId", sourceId);
        body.put("query", prompt);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.chatpdf.com/v1/sources/query";

        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        return response.getBody();
    }

    public TicketModel parsearRespuestaYCrearTicket(String jsonRespuesta) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonRespuesta);

        String textoRespuesta = root.path("answer").asText();

        TicketModel ticket = new TicketModel();

        if (textoRespuesta.contains("fecha:")) {
            String fecha = textoRespuesta.split("fecha:")[1].split(",")[0].trim();
            ticket.setFechaFromString(fecha);
        }
        if (textoRespuesta.contains("supermercado:")) {
            String supermercado = textoRespuesta.split("supermercado:")[1].split(",")[0].trim();
            ticket.setSupermercadoFromString(supermercado);
        }
        if (textoRespuesta.contains("precio:")) {
            String precioStr = textoRespuesta.split("precio:")[1].split(",")[0].trim();
            try {
                ticket.setPrecioTotal(Double.parseDouble(precioStr));
            } catch (NumberFormatException e) {
                ticket.setPrecioTotal(0.0);
            }
        }
        if (textoRespuesta.contains("formaPago:")) {
            String formaPago = textoRespuesta.split("formaPago:")[1].split(",")[0].trim();
            ticket.setFormaPagoFromString(formaPago);
        }

        return ticket;
    }
}
