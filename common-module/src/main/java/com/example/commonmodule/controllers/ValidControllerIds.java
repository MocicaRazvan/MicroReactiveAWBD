package com.example.commonmodule.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ValidControllerIds {
    Mono<ResponseEntity<Void>> validIds(@RequestParam List<Long> ids);
}
