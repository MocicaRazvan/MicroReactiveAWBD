package com.example.userservice.advice;


import com.example.commonmodule.advices.BaseExceptionMapping;
import com.example.commonmodule.dtos.errors.BaseErrorResponse;
import com.example.commonmodule.exceptions.common.UsernameNotFoundException;
import com.example.userservice.exceptions.UserWithEmailExists;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestControllerAdvice
public class AuthAdvice extends BaseExceptionMapping {


    @ExceptionHandler(UsernameNotFoundException.class)
    public Mono<ResponseEntity<BaseErrorResponse>> notFound(UsernameNotFoundException error, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.NOT_FOUND, error, exchange);

    }

    @ExceptionHandler(UserWithEmailExists.class)
    public Mono<ResponseEntity<BaseErrorResponse>> handleUserWithEmailExists(UserWithEmailExists e, ServerWebExchange exchange) {
        return handleWithMessage(HttpStatus.CONFLICT, e, exchange);
    }

}
