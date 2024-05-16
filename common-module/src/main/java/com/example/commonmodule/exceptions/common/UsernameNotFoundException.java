package com.example.commonmodule.exceptions.common;

import com.example.commonmodule.exceptions.notFound.NotFoundBase;

public class UsernameNotFoundException extends NotFoundBase {
    public UsernameNotFoundException(String message) {
        super(message);
    }
}
