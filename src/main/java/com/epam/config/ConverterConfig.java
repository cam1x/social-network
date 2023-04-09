package com.epam.config;

import com.epam.converter.IntToDayOfWeekConverter;
import com.epam.converter.IntToMonthConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class ConverterConfig {

    @Bean
    @Primary
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(List.of(new IntToDayOfWeekConverter(), new IntToMonthConverter()));
    }
}
