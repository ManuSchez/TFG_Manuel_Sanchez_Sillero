package com.example.demo.services;

import com.example.demo.controllers.dto.ContentResponsDto;
import com.example.demo.models.TicketModel;
import com.example.demo.repositories.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    // Inyectamos el ObjectMapper que configuraste en JacksonConfig
    @Autowired
    private ObjectMapper objectMapper;

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

    public List<TicketModel> obtenerTodos() {
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

    public ContentResponsDto consultarPdfConPrompt(String sourceId) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("sourceId", sourceId);
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", "Por favor, extrae del texto del ticket la siguiente información y devuélvela en formato JSON con estas claves:\n\n"
                + "{\n"
                + "  \"fecha\": \"YYYY-MM-DD\" o \"No encontrado\",\n"
                + "  \"supermercado\": \"Nombre del supermercado en mayúsculas o 'No encontrado'\",\n"
                + "  \"precioTotal\": número decimal o \"No encontrado\",\n"
                + "  \"formaPago\": \"Nombre de la forma de pago en mayúsculas y guiones bajos o 'No encontrado'\"\n"
                + "}\n\n"
                + "Si algún dato no está presente o no puede ser extraído, pon el valor \"No encontrado\" para ese campo.\n\n"
                + "Ejemplo de respuesta:\n\n"
                + "{\n"
                + "  \"fecha\": \"2023-05-20\",\n"
                + "  \"supermercado\": \"MERCADONA\",\n"
                + "  \"precioTotal\": 45.70,\n"
                + "  \"formaPago\": \"TARJETA_CREDITO\"\n"
                + "}");

        messages.add(message);
        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.chatpdf.com/v1/chats/message";

        ResponseEntity<ContentResponsDto> response = restTemplate.postForEntity(url, requestEntity, ContentResponsDto.class);
        ContentResponsDto responseBody = response.getBody();

        String content = responseBody.getContent().trim();

        // Limpiar comillas invertidas si existen
        if (content.startsWith("```") || content.startsWith("`")) {
            content = content.replaceAll("```(json)?", "").trim();
        }

        try {
            // Usamos ObjectMapper inyectado, que ya tiene JavaTimeModule registrado
            TicketModel ticket = objectMapper.readValue(content, TicketModel.class);
            System.out.println("Ticket mapeado correctamente: " + ticket);
        } catch (Exception e) {
            System.err.println("Contenido recibido NO es JSON válido:");
            System.err.println(content);
            throw e;
        }

        System.out.println("Contenido del prompt: " + content);

        return responseBody;
    }

    public TicketModel parsearRespuestaYCrearTicket(String jsonRespuesta) throws IOException {
        JsonNode root = objectMapper.readTree(jsonRespuesta);

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
                BigDecimal precio = new BigDecimal(precioStr);
                ticket.setPrecioTotal(precio);
            } catch (NumberFormatException e) {
                ticket.setPrecioTotal(BigDecimal.ZERO);
            }
        }
        if (textoRespuesta.contains("formaPago:")) {
            String formaPago = textoRespuesta.split("formaPago:")[1].split(",")[0].trim();
            ticket.setFormaPagoFromString(formaPago);
        }

        return ticket;
    }
}
