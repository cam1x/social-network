package com.epam.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Data
@Document
public class User {

    @Id
    private String login;
    private String name;
    private String password;
    private Collection<Message> messages;
    private Collection<String> movies;
    private Collection<AudioTrack> audioTracks;
    private Collection<Friendship> friendships;
}
