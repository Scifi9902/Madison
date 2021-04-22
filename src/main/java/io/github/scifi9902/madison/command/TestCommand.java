package io.github.scifi9902.madison.command;

import io.github.scifi9902.madison.utils.CC;
import io.github.scifi9902.madison.utils.command.annotation.Command;
import io.github.scifi9902.madison.utils.command.annotation.Optional;
import io.github.scifi9902.madison.utils.command.annotation.Parameter;
import org.bukkit.command.CommandSender;

public class TestCommand {

    @Command(label = "test")
    public void execute(CommandSender sender, @Parameter(name = "test") String test, @Optional() String optional) {
        sender.sendMessage(CC.chat("&eHello World."));
        sender.sendMessage(CC.chat(test));

        sender.sendMessage(optional);
    }

}
