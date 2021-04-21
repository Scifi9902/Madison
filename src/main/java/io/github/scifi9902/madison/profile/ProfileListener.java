package io.github.scifi9902.madison.profile;

import io.github.scifi9902.madison.MadisonPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;
import java.util.UUID;

public class ProfileListener implements Listener {

    private final ProfileHandler profileHandler;

    public ProfileListener(MadisonPlugin instance) {
        this.profileHandler = instance.getHandlerManager().getHandler(ProfileHandler.class);
    }

    @EventHandler
    public void onAsyncPlayerLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Optional<Profile> profile = profileHandler.getProfile(uuid);

        if (profile.isPresent()) {
            return;
        }

        profileHandler.getProfileRepository().getData(uuid.toString(), Profile.class)
                .handleAsync((found, s) -> {
                    if (found == null) {
                        Profile newProfile = new Profile(uuid);
                        profileHandler.getProfileMap().put(uuid, newProfile);
                    }

                    else {
                        profileHandler.getProfileMap().put(uuid, found);
                    }

                    return null;
                });

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        UUID uuid = event.getPlayer().getUniqueId();

        Optional<Profile> optional = profileHandler.getProfile(uuid);

        if (!optional.isPresent()) {
            return;
        }

        Profile profile = optional.get();

        profileHandler.getProfileMap().remove(uuid);
        profileHandler.getProfileRepository().saveData(profile.getUniqueId().toString(), profile);

    }

}
