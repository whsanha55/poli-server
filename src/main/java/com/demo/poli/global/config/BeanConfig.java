package com.demo.poli.global.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.math.BigDecimal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setSerializationInclusion(Include.ALWAYS)
            .registerModule(new SimpleModule().addSerializer(BigDecimal.class, new JsonSerializer<>() {
                @Override
                public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    if (value != null) {
                        var stripped = value.stripTrailingZeros();
                        if (stripped.scale() < 0) {
                            gen.writeNumber(new BigDecimal(stripped.toPlainString()));
                        } else {
                            gen.writeNumber(stripped);
                        }
                    } else {
                        gen.writeNull();
                    }
                }
            }))
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

}
