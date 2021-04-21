package io.github.scifi9902.madison.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import io.github.scifi9902.madison.handler.IHandler;
import lombok.Getter;

import java.util.Collections;

@Getter
public class MongoHandler implements IHandler {

    private final MongoDatabase mongoDatabase;

    public MongoHandler(String host, int port, boolean auth, String username, String password, String database) {

        final MongoClient mongoClient;

        if (auth) {
            mongoClient = new MongoClient(new ServerAddress(host,port), Collections.singletonList(MongoCredential.createCredential(username,database, password.toCharArray())));
        }

        else {
            mongoClient = new MongoClient(new ServerAddress(host,port));
        }

        this.mongoDatabase = mongoClient.getDatabase(database);
    }

}
