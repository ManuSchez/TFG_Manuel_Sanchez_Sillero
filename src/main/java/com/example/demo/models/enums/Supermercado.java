package com.example.demo.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Supermercado {
    MERCADONA,
    CARREFOUR,
    LIDL,
    ALDI,
    DIA,
    EROSKI,
    HIPERCOR,
    CONSUM,
    OTRO;  

    @JsonCreator
    public static Supermercado fromString(String value) {
        if (value == null) {
            return OTRO;
        }
        return Arrays.stream(Supermercado.values())
                .filter(s -> s.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(OTRO);
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
