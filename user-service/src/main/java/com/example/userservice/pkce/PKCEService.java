package com.example.userservice.pkce;

public interface PKCEService {
    String generateAuthorizationUrl(String authUri, String clientId, String redirectUri, String state, String codeChallenge);
}
