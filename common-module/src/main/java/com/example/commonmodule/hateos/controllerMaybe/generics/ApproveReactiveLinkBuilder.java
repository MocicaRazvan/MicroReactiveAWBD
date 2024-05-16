package com.example.commonmodule.hateos.controllerMaybe.generics;


import com.example.commonmodule.controllers.ApproveController;
import com.example.commonmodule.dtos.PageableBody;
import com.example.commonmodule.dtos.generic.TitleBody;
import com.example.commonmodule.dtos.generic.TitleBodyUser;
import com.example.commonmodule.hateos.controllerMaybe.ReactiveLinkBuilder;
import com.example.commonmodule.mappers.DtoMapper;
import com.example.commonmodule.models.Approve;
import com.example.commonmodule.repositories.ApprovedRepository;
import com.example.commonmodule.services.ApprovedService;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;

import java.util.List;

public abstract class ApproveReactiveLinkBuilder<MODEL extends Approve, BODY extends TitleBody, RESPONSE extends TitleBodyUser,
        S extends ApprovedRepository<MODEL>, M extends DtoMapper<MODEL, BODY, RESPONSE>,
        G extends ApprovedService<MODEL, BODY, RESPONSE, S, M>,
        C extends ApproveController<MODEL, BODY, RESPONSE, S, M, G>
        >
        extends TitleBodyReactiveLinkBuilder<MODEL, BODY, RESPONSE, S, M, G, C>
        implements ReactiveLinkBuilder<RESPONSE, C> {

    @Override
    public List<WebFluxLinkBuilder.WebFluxLink> createModelLinks(RESPONSE response, Class<C> c) {
        List<WebFluxLinkBuilder.WebFluxLink> links = super.createModelLinks(response, c);
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).approveModel(response.getId(), null)).withRel("approve"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelsTrainer(response.getTitle(),
                PageableBody.builder().page(0).size(10).build(), response.getUserId(), null)).withRel("models by trainer"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).createModel(null, null)).withRel("create"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getModelsApproved(response.getTitle(),
                PageableBody.builder().page(0).size(10).build(), null)).withRel("approved models"));
        links.add(WebFluxLinkBuilder.linkTo(WebFluxLinkBuilder.methodOn(c).getAllModelsAdmin(response.getTitle(),
                PageableBody.builder().page(0).size(10).build(), null)).withRel("all models admin"));
        return links;
    }
}
