package com.example.userservice.pkce;

import com.example.userservice.dtos.auth.requests.CallbackBody;
import com.example.userservice.dtos.auth.response.AuthResponse;
import com.example.userservice.exceptions.StateNotFound;
import com.example.userservice.models.OauthState;
import com.example.userservice.pkce.impl.PKCEHandlerImpl;
import com.example.userservice.repositories.OauthStateRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;


@Service
@Slf4j
public class PKCEGoogle extends PKCEHandlerImpl {
    public PKCEGoogle(PKCEService pkceService, OauthStateRepository oauthStateRepository,
                      @Value("${google.client.id}") String clientId,
                      @Value("${google.redirect.uri}") String redirectUri,
                      @Value("${google.client.secret}") String clientSecret) {
        super(pkceService, oauthStateRepository, clientId, redirectUri, clientSecret, "https://accounts.google.com/o/oauth2/v2/auth");
    }

//    @Value("${google.client.id}")
//    private String clientId;
//
//    @Value("${google.client.secret}")
//    private String clientSecret;
//
//    @Value("${google.redirect.uri}")
//    private String redirectUri;
//
//    private final PKCEService pkceService;
//    private final OauthStateRepository oauthStateRepository;
//
//    public Mono<CodeVerifierResponse> handleAuthorizationCode(@RequestParam String state) {
//        log.error("Initiating Google login");
//
//        String codeVerifier = PKCEUtil.generateCodeVerifier();
//        String codeChallenge = PKCEUtil.generateCodeChallenge(codeVerifier);
//        log.error("State: {}", state);
//
//        OauthState oauthState = new OauthState();
//        oauthState.setState(state);
//        oauthState.setCodeVerifier(codeVerifier);
//        log.error("Generated code_verifier: {}", codeVerifier);
//
//        return oauthStateRepository.save(oauthState)
//                .map(o -> {
//                    log.error("Saved state: {}", o);
//                    return o;
//                })
//                .then(Mono.defer(() -> {
//                    String authorizationUrl = pkceService.generateAuthorizationUrl("https://accounts.google.com/o/oauth2/v2/auth",
//                            clientId, redirectUri, state, codeChallenge);
//                    log.error("Generated authorization URL: {}", authorizationUrl);
//
//                    return Mono.just(CodeVerifierResponse.builder().url(authorizationUrl).build());
//                }));
//    }
//
//    public Mono<AuthResponse> handleAuthResponse(
//            CallbackBody callbackBody, BiFunction<CallbackBody, String, Mono<AuthResponse>> handleCallback
//    ) {
//        log.error("Received callback: {}", callbackBody);
//        return oauthStateRepository.findByState(callbackBody.getState())
//                .switchIfEmpty(Mono.error(new StateNotFound()))
//                .flatMap(oauthState -> {
//                    String codeVerifier = oauthState.getCodeVerifier();
//                    log.error("Retrieved code_verifier from store: {}", codeVerifier);
//                    return handleCallback.apply(callbackBody, codeVerifier)
//                            .flatMap(resp -> oauthStateRepository.deleteByState(callbackBody.getState())
//                                    .thenReturn(resp));
//                });
//    }

}
