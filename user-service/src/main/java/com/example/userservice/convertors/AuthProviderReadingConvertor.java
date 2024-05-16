package com.example.userservice.convertors;


import com.example.commonmodule.enums.AuthProvider;
import com.example.commonmodule.enums.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class AuthProviderReadingConvertor implements Converter<String, AuthProvider> {
    @Override
    public AuthProvider convert(String source) {
        return source == null ? null : AuthProvider.valueOf(source);
    }
}
