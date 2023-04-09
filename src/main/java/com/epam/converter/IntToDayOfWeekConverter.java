package com.epam.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.DayOfWeek;

@ReadingConverter
public class IntToDayOfWeekConverter implements Converter<Integer, DayOfWeek> {

    @Override
    public DayOfWeek convert(Integer source) {
        return DayOfWeek.of(source).minus(1);
    }
}

