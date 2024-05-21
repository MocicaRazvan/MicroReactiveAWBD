package com.example.userservice.convertors;

import com.example.commonmodule.convertors.BaseWritingConverter;
import com.example.userservice.enums.OTPType;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class OTPTypeWritingConverter extends BaseWritingConverter<OTPType> {
    public OTPTypeWritingConverter() {
        super(OTPType.class);
    }
}