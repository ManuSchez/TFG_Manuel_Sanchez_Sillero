package com.example.demo.services;

import com.example.demo.models.TicketModel;
import com.example.demo.models.enums.FormaPago;
import com.example.demo.models.enums.Supermercado;
import com.example.demo.repositories.TicketRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String extraerTextoDirecto(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public TicketModel procesarPdf(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(document);
    
            System.out.println("Texto extraído del PDF:\n" + texto);
    
            Pattern patternFecha = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
            Matcher matcherFecha = patternFecha.matcher(texto);
            LocalDate fecha = null;
            if (matcherFecha.find()) {
                String[] partes = matcherFecha.group().split("/");
                fecha = LocalDate.of(
                    Integer.parseInt(partes[2]),
                    Integer.parseInt(partes[1]),
                    Integer.parseInt(partes[0])
                );
            }
    
            Pattern patternPrecio = Pattern.compile("(\\d+[,.]\\d{2})\\s*€");
            Matcher matcherPrecio = patternPrecio.matcher(texto);
            Double precioTotal = null;
            if (matcherPrecio.find()) {
                precioTotal = Double.parseDouble(matcherPrecio.group(1).replace(",", "."));
            }
    
            TicketModel ticket = new TicketModel();
            ticket.setFecha(fecha);
            ticket.setPrecioTotal(precioTotal);
            ticket.setSupermercado(Supermercado.MERCADONA);
            ticket.setFormaPago(FormaPago.EFECTIVO);
    
            return ticketRepository.save(ticket);
    
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al procesar el PDF digital: " + e.getMessage());
        }
    }
    
    }
