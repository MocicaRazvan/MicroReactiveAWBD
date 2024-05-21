package com.example.userservice.convertors;

import com.example.commonmodule.convertors.BaseWritingConverter;
import com.example.commonmodule.enums.AuthProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
//public class AuthProviderWritingConvertor implements Converter<AuthProvider, String> {
//    @Override
//    public String convert(AuthProvider authProvider) {
//        return authProvider == null ? null : authProvider.name();
//    }
//}

public class AuthProviderWritingConvertor extends BaseWritingConverter<AuthProvider> {
    public AuthProviderWritingConvertor() {
        super(AuthProvider.class);
    }
}