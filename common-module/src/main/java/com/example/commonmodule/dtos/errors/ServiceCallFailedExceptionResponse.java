package com.example.commonmodule.dtos.errors;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ServiceCallFailedExceptionResponse extends BaseErrorResponse {
    public String serviceName;
    public String servicePath;

    public ServiceCallFailedExceptionResponse withBase(BaseErrorResponse baseErrorResponse, String serviceName, String servicePath) {
        return ServiceCallFailedExceptionResponse.builder()
                .error(baseErrorResponse.getError())
                .message(baseErrorResponse.getMessage())
                .path(baseErrorResponse.getPath())
                .status(baseErrorResponse.getStatus())
                .timestamp(baseErrorResponse.getTimestamp())
                .serviceName(serviceName)
                .servicePath(servicePath)
                .build();
    }

}
