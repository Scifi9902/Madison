package io.github.scifi9902.madison;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import io.github.scifi9902.madison.command.TestCommand;
import io.github.scifi9902.madison.database.MongoHandler;
import io.github.scifi9902.madison.handler.HandlerManager;
import io.github.scifi9902.madison.handler.IHandler;
import io.github.scifi9902.madison.profile.ProfileHandler;
import io.github.scifi9902.madison.profile.ProfileListener;
import io.github.scifi9902.madison.utils.Config;
import io.github.scifi9902.madison.utils.command.CommandHandler;
import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class MadisonPlugin extends JavaPlugin {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create();

    private Config config;

    private HandlerManager handlerManager;

    private CommandHandler commandHandler;

    public void onEnable() {
        this.config = new Config("config", this, this.getDataFolder().getAbsolutePath());
        this.handlerManager = new HandlerManager();

        this.handlerManager.registerHandler(new MongoHandler(config.getString("mongo.host"), config.getInt("mongo.port"), config.getBoolean("mongo.auth"), config.getString("mongo.username"), config.getString("mongo.password"), config.getString("mongo.database")));
        this.handlerManager.registerHandler(new ProfileHandler(this));

        this.registerCommands();
        this.registerListeners();
    }

    public void onDisable() {
        this.handlerManager.getHandlers().forEach(IHandler::unload);
    }

    private void registerCommands() {
        this.commandHandler = new CommandHandler(this, "madison");

        this.commandHandler.registerCommand(new TestCommand());
    }

    private void registerListeners() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ProfileListener(this), this);
    }

}

