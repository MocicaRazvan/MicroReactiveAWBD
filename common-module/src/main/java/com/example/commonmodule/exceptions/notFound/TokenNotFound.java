package com.example.commonmodule.exceptions.notFound;

public class TokenNotFound extends NotFoundBase {
    public TokenNotFound() {
        super("A valid token wasn't provided!");
    }
}
