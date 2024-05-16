package com.example.userservice.convertors;


import com.example.commonmodule.enums.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class RoleReadingConvertor implements Converter<String, Role> {
    @Override
    public Role convert(String source) {
        return source == null ? null : Role.valueOf(source);
    }
}
