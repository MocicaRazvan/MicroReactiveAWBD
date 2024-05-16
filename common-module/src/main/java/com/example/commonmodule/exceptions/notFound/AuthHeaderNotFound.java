package com.example.commonmodule.exceptions.notFound;

public class AuthHeaderNotFound extends NotFoundBase {
    public AuthHeaderNotFound() {
        super("Authorization header for userId wasn't provided!");
    }
}
