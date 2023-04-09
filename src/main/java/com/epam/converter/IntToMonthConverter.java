package com.epam.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Month;

@ReadingConverter
public class IntToMonthConverter implements Converter<Integer, Month> {
    @Override
    public Month convert(Integer source) {
        return Month.of(source);
    }
}

