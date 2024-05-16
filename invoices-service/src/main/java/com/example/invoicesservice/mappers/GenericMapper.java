package com.example.invoicesservice.mappers;

public interface GenericMapper<MODEL, RESPONSE> {

    MODEL fromResponseToModel(RESPONSE response);

    RESPONSE fromModelToResponse(MODEL model);

}
