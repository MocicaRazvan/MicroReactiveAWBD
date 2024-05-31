package com.example.websocketservice.exceptions;

public class EntityNotFound extends RuntimeException {

    public EntityNotFound(String name, Long id) {
        super(name + " with id " + id + " not found");
    }
}
