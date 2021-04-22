package io.github.scifi9902.madison.utils.command.converter;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface IConverter<T> {

    Class<T> getType();

    T fromString(CommandSender sender, String string);

    List<String> tabComplete(CommandSender sender);

}
