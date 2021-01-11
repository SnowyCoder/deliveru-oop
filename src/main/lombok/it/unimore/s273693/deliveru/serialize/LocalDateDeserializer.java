package it.unimore.s273693.deliveru.serialize;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Deserializer of {@link LocalDate} (required by Jackson).
 */
public class LocalDateDeserializer extends StdDeserializer<LocalDate> {
    private static final long serialVersionUID = 1L;

    protected LocalDateDeserializer() {
        super(LocalDate.class);
    }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return LocalDate.parse(jp.readValueAs(String.class));
    }
}