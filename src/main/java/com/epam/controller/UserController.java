package com.epam.controller;

import com.epam.model.Friendship;
import com.epam.model.Message;
import com.epam.model.User;
import com.epam.model.aggregation.MaxFriendships;
import com.epam.model.aggregation.MessagesCount;
import com.epam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/{login}")
    public String loadUser(@PathVariable("login") String login, Model model) {
        var user = userRepository.findUser(login);
        model.addAttribute("user", user);
        return "user";
    }

    @GetMapping(path = "/{login}/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody Flux<Message> getUserMessages(@PathVariable("login") String login) {
        return userRepository.findMessages(login).delayElements(Duration.ofMillis(250)).onBackpressureDrop();
    }

    @PostMapping
    public @ResponseBody void addUser(@RequestBody User user) {
        userRepository.insert(user);
    }

    @PostMapping("/{login}/messages")
    public @ResponseBody void addMessage(@PathVariable("login") String login, @RequestBody Message message) {
        if (message.getCreationDateTime() == null) {
            message.setCreationDateTime(LocalDateTime.now());
        }
        userRepository.insertMessage(login, message).subscribe();
    }

    @PostMapping("/{login}/friends")
    public @ResponseBody void addFriend(@PathVariable("login") String login, @RequestBody Friendship friend) {
        if (friend.getDateTime() == null) {
            friend.setDateTime(LocalDateTime.now());
        }
        userRepository.insertFriendship(login, friend);
    }

    @PostMapping("/{login}/movies")
    public @ResponseBody void addMovie(@PathVariable("login") String login, @RequestParam String movie) {
        userRepository.insertMovie(login, movie);
    }

    @GetMapping("/avgMessages")
    public @ResponseBody Collection<MessagesCount> getAvgMessages() {
        return userRepository.getAverageNumberOfMessagesByDayOfWeek();
    }

    @GetMapping("/maxFriendships")
    public @ResponseBody Collection<MaxFriendships> getMaxFriendships() {
        return userRepository.getMaxNewFriendshipByMonth();
    }

    @GetMapping("/minMovies")
    public @ResponseBody int getMinMovie() {
        return userRepository.getMinWatchedMovieByUsersWithFriends();
    }
}
