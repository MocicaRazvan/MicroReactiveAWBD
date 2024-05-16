package com.example.commonmodule.hateos.controllerMaybe;

import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

@FunctionalInterface
public interface ReactiveLinkBuilder<RESPONSE, C> {

    List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(RESPONSE response, Class<C> c);


}
