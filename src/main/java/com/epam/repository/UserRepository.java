package com.epam.repository;

import com.epam.model.Friendship;
import com.epam.model.Message;
import com.epam.model.User;
import com.epam.model.aggregation.MaxFriendships;
import com.epam.model.aggregation.MessagesCount;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final MongoOperations mongoOperations;
    private final ReactiveMongoOperations reactiveMongoOperations;

    @PostConstruct
    public void createCollection() {
        if (!mongoOperations.collectionExists(Message.class)) {
            mongoOperations.createCollection(Message.class, CollectionOptions.empty().size(1000).capped());
        }
    }


    public User insert(@NonNull User user) {
        user.getMessages().forEach(mongoOperations::save);
        return mongoOperations.save(user);
    }

    public Flux<User> insertAll(@NonNull Collection<User> users) {
        return reactiveMongoOperations.insertAll(users);
    }

    public Flux<User> findAllUsers() {
        return reactiveMongoOperations.findAll(User.class);
    }

    public User findUser(@NonNull String login) {
        var query = new Query().addCriteria(Criteria.where("login").is(login));
        return mongoOperations.findOne(query, User.class);
    }

    public Mono<Message> insertMessage(@NonNull String login, @NonNull Message message) {
        var query = new Query().addCriteria(Criteria.where("login").is(login));
        var update = new Update().addToSet("messages", message);
        mongoOperations.updateFirst(query, update, User.class);
        return reactiveMongoOperations.save(message);
    }

    public Flux<Message> findMessages(@NonNull String login) {
        var query = new Query().addCriteria(Criteria.where("author").is(login));
        return reactiveMongoOperations.tail(query, Message.class);
    }

    public void insertFriendship(@NonNull String login, @NonNull Friendship friendship) {
        var query = new Query().addCriteria(Criteria.where("login").is(login));
        var update = new Update().addToSet("friendships", friendship);
        mongoOperations.updateFirst(query, update, User.class);
    }

    public void insertMovie(@NonNull String login, @NonNull String movie) {
        var query = new Query().addCriteria(Criteria.where("login").is(login));
        var update = new Update().addToSet("movies", movie);
        mongoOperations.updateFirst(query, update, User.class);
    }

    public Collection<MessagesCount> getAverageNumberOfMessagesByDayOfWeek() {
        var aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("messages").exists(true).not().size(0)),
                Aggregation.unwind("messages"),
                Aggregation.project("login").and("messages.creationDateTime").extractDayOfWeek().as("dayOfWeek"),
                Aggregation.group("login", "dayOfWeek").count().as("msgCount"),
                Aggregation.group("dayOfWeek").avg("msgCount").as("avgMsgCount"));
        return mongoOperations.aggregate(aggregation, User.class, MessagesCount.class).getMappedResults();
    }

    public Collection<MaxFriendships> getMaxNewFriendshipByMonth() {
        var aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("friendships").exists(true)),
                Aggregation.unwind("friendships"),
                Aggregation.project("login").and("friendships.dateTime").extractMonth().as("month"),
                Aggregation.group("login", "month").count().as("friendCount"),
                Aggregation.group("month").max("friendCount").as("maxFriendships"));
        return mongoOperations.aggregate(aggregation, User.class, MaxFriendships.class).getMappedResults();
    }

    public int getMinWatchedMovieByUsersWithFriends() {
        var aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("friendships").exists(true).and("movies").exists(true)),
                Aggregation.project("movies").and("friendships").size().as("friendCount"),
                Aggregation.match(Criteria.where("friendCount").gt(0)),
                Aggregation.unwind("movies"),
                Aggregation.group("movies").count().as("watched"),
                Aggregation.group().min("watched").as("min"));
        var document = mongoOperations.aggregate(aggregation, User.class, Document.class).getUniqueMappedResult();
        return Optional.ofNullable(document).map(doc -> doc.getInteger("min")).orElse(0);
    }
}
