package com.example.commonmodule.jackson;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@RequiredArgsConstructor
public class CustomObjectMapper {

    private final Jackson2ObjectMapperBuilder builder;

    public ObjectMapper customObjectMapper() {
        return builder
                .modules(new SimpleModule().addSerializer(WebFluxLinkBuilder.WebFluxLink.class, new WebFluxLinkSerializer())
                        , new Jackson2HalModule(), new JavaTimeModule()
                ).build();
    }
}
