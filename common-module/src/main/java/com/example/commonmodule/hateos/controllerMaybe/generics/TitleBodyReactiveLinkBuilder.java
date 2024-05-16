package com.example.commonmodule.hateos.controllerMaybe.generics;


import com.example.commonmodule.controllers.TitleBodyController;
import com.example.commonmodule.dtos.generic.WithUser;
import com.example.commonmodule.hateos.controllerMaybe.ReactiveLinkBuilder;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.TitleBody;
import com.example.commonmodule.repositories.TitleBodyRepository;
import com.example.commonmodule.services.TitleBodyService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public abstract class TitleBodyReactiveLinkBuilder<MODEL extends TitleBody, BODY, RESPONSE extends WithUser,
        S extends TitleBodyRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends TitleBodyService<MODEL, BODY, RESPONSE, S, M>,
        C extends TitleBodyController<MODEL, BODY, RESPONSE, S, M, G>>
        extends ManyToOneUserReactiveLinkBuilder<MODEL, BODY, RESPONSE, S, M, G, C>
        implements ReactiveLinkBuilder<RESPONSE, C> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(RESPONSE response, Class<C> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(response, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).likeModel(response.getId(), null)).withRel("like"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).dislikeModel(response.getId(), null)).withRel("dislike"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelsWithUserAndReaction(response.getId(), null)).withRel("withUser/withReactions"));
        return links;
    }
}
