package com.epam.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
public class Message {

    private String author;
    private String text;
    private LocalDateTime creationDateTime;
}

