package com.example.demo.models.enums;

import java.util.Arrays;
import java.util.Optional;

public enum FormaPago {
    EFECTIVO, TARJETA, BIZUM, PAYPAL, TRANSFERENCIA, NO_ENCONTRADO;
    public static Optional<FormaPago> fromString(String value) {
        if (value == null) return Optional.empty();
        return Arrays.stream(values())
                     .filter(p -> p.name().equalsIgnoreCase(value.replace(" ", "_")))
                     .findFirst();
    }
}