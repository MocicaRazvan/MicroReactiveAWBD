package com.example.userservice.convertors;

import com.example.commonmodule.convertors.BaseWritingConverter;
import com.example.commonmodule.enums.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
//public class RoleWritingConvertor implements Converter<Role, String> {
//    @Override
//    public String convert(Role role) {
//        return role == null ? null : role.name();
//    }
//}

public class RoleWritingConvertor extends BaseWritingConverter<Role> {
    public RoleWritingConvertor() {
        super(Role.class);
    }
}