package com.example.commonmodule.dtos.errors;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class ValidationResponse extends BaseErrorResponse {
    public Map<String, String> reasons;

    public ValidationResponse withBase(BaseErrorResponse baseErrorResponse, Map<String, String> reasons) {
        return ValidationResponse.builder()
                .error(baseErrorResponse.getError())
                .message(baseErrorResponse.getMessage())
                .path(baseErrorResponse.getPath())
                .status(baseErrorResponse.getStatus())
                .timestamp(baseErrorResponse.getTimestamp())
                .reasons(reasons)
                .build();
    }
}
