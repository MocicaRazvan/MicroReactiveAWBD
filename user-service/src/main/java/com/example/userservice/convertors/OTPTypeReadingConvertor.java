package com.example.userservice.convertors;

import com.example.commonmodule.convertors.BaseReadingConverter;
import com.example.userservice.enums.OTPType;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class OTPTypeReadingConvertor extends BaseReadingConverter<OTPType> {
    public OTPTypeReadingConvertor() {
        super(OTPType.class);
    }
}