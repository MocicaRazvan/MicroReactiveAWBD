package com.example.userservice.services.impl;


import com.example.commonmodule.enums.AuthProvider;
import com.example.userservice.services.AbstractOauthProvider;
import com.example.userservice.services.HandleUserProvider;
import com.example.userservice.services.OauthUserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GithubUserService extends AbstractOauthProvider {

//    @Value("${git.client.id}")
//    private String clientId;
//    @Value("${git.client.secret}")
//    private String clientSecret;
//    @Value("${git.redirect.uri}")
//    private String redirectUri;


    public GithubUserService(OauthUserInfoFactory oauthUserInfoFactory,
                             @Value("${git.client.id}") String clientId,
                             @Value("${git.client.secret}") String clientSecret,
                             @Value("${git.redirect.uri}") String redirectUri) {
        
        super(oauthUserInfoFactory, clientId, clientSecret, redirectUri, "https://github.com/login/oauth/access_token",
                "https://api.github.com/user", AuthProvider.GITHUB,
                body -> UriComponentsBuilder.fromUriString("/?" + body).build().getQueryParams().toSingleValueMap());
    }
}
