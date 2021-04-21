package io.github.scifi9902.madison.profile;

import io.github.scifi9902.madison.MadisonPlugin;
import io.github.scifi9902.madison.handler.IHandler;
import io.github.scifi9902.madison.profile.repository.ProfileRepository;
import lombok.Getter;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileHandler implements IHandler {

    private final Map<UUID, Profile> profileMap = new ConcurrentHashMap<>();

    private final ProfileRepository profileRepository;

    public ProfileHandler(MadisonPlugin instance) {
        this.profileRepository = new ProfileRepository(instance);
    }

    public Optional<Profile> getProfile(UUID uuid) {
        if (this.profileMap.containsKey(uuid)) {
            return Optional.of(this.profileMap.get(uuid));
        }
        return Optional.empty();
    }

    @Override
    public void unload() {
        for (Profile profile : this.profileMap.values()) {
            this.profileRepository.saveData(profile.getUniqueId().toString(), profile);
        }
    }
}
