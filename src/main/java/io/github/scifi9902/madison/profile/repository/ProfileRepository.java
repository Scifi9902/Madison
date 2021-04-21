package io.github.scifi9902.madison.profile.repository;

import io.github.scifi9902.madison.MadisonPlugin;
import io.github.scifi9902.madison.database.MongoHandler;
import io.github.scifi9902.madison.database.repository.MongoRepository;
import io.github.scifi9902.madison.profile.Profile;

public class ProfileRepository extends MongoRepository<Profile> {

    public ProfileRepository(MadisonPlugin instance) {
        super(instance);
        this.setCollection(instance.getHandlerManager().getHandler(MongoHandler.class).getMongoDatabase().getCollection("profiles"));
    }


}
