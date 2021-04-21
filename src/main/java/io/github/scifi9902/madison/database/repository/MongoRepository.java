package io.github.scifi9902.madison.database.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import io.github.scifi9902.madison.MadisonPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public abstract class MongoRepository<T> {

    private MadisonPlugin instance;

    private MongoCollection collection;

    public MongoRepository(MadisonPlugin instance) {
        this.instance = instance;
    }

    public CompletableFuture<T> getData(String id, Type type) {
        return CompletableFuture.supplyAsync(() -> {
            Document document = (Document) this.collection.find(Filters.eq("_id", id)).first();

            if (document == null) {
                return null;
            }

            return instance.getGson().fromJson(document.toJson(), type);
        });
    }

    public void saveData(String id, T t) {
        CompletableFuture.supplyAsync(() -> this.collection.replaceOne(Filters.eq("_id", id),
                Document.parse(instance.getGson().toJson(t)),
                new UpdateOptions().upsert(true)));
    }

}
