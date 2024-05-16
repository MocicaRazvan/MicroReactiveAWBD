package com.example.commonmodule.hateos.controllerMaybe.generics;

import com.example.commonmodule.controllers.ManyToOneUserController;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.hateos.controllerMaybe.ReactiveLinkBuilder;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.ManyToOneUser;
import com.example.commonmodule.repositories.ManyToOneUserRepository;
import com.example.commonmodule.services.ManyToOneUserService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class ManyToOneUserReactiveLinkBuilder<
        MODEL extends ManyToOneUser, BODY, RESPONSE extends WithUser,
        S extends ManyToOneUserRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ManyToOneUserService<MODEL, BODY, RESPONSE, S, M>,
        C extends ManyToOneUserController<MODEL, BODY, RESPONSE, S, M, G>>
        implements ReactiveLinkBuilder<RESPONSE, C> {


    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(RESPONSE response, Class<C> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = new ArrayList<>();
        links.add(WebFluxLinkBuilder.linkTo(
                WebFluxLinkBuilder.methodOn(c).deleteModel(response.getId(), null)).withRel("delete"));
        links.add(
                WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelById(response.getId(), null)).withSelfRel());
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelByIdWithUser(response.getId(), null)).withRel("withUser"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).updateModel(null, response.getId(), null)).withRel("update"));

        return links;
    }


}
