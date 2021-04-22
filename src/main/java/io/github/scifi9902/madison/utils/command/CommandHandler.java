package io.github.scifi9902.madison.utils.command;

import io.github.scifi9902.madison.utils.command.annotation.Command;
import io.github.scifi9902.madison.utils.command.annotation.SubCommand;
import io.github.scifi9902.madison.utils.command.converter.IConverter;
import io.github.scifi9902.madison.utils.command.converter.impl.IntegerConverter;
import io.github.scifi9902.madison.utils.command.converter.impl.StringConverter;
import io.github.scifi9902.madison.utils.command.data.CommandData;
import io.github.scifi9902.madison.utils.command.data.SubCommandData;
import lombok.Getter;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class CommandHandler {

    private final String fallbackPrefix;

    private CommandMap commandMap;

    private final Map<String,CustomCommand> registeredCommands = new HashMap<>();

    private final Map<Class<?>, IConverter<?>> converterMap = new HashMap<>();

    public CommandHandler(JavaPlugin plugin, String fallbackPrefix) {
        this.fallbackPrefix = fallbackPrefix;

        try {
            Field field = plugin.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            this.commandMap = (CommandMap) field.get(plugin.getServer());
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }

        this.registerConverter(new IntegerConverter());
        this.registerConverter(new StringConverter());
    }

    public void registerConverter(IConverter<?> converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Failed to register converter, the converter is null");
        }

        this.getConverterMap().put(converter.getType(), converter);
    }

    public void registerCommand(Object object) {
        List<Method> commandMethods = Arrays.stream(object.getClass().getMethods()).filter(method -> method.isAnnotationPresent(Command.class)).collect(Collectors.toList());
        List<Method> subCommandMethods = Arrays.stream(object.getClass().getMethods()).filter(method -> method.isAnnotationPresent(SubCommand.class)).collect(Collectors.toList());

        for (Method method : commandMethods) {
            CommandData commandData = new CommandData(method, object, method.getAnnotation(Command.class));
            CustomCommand customCommand = new CustomCommand(this, commandData);
            this.getRegisteredCommands().put(commandData.getCommand().label().toLowerCase(), customCommand);
            this.getCommandMap().register(fallbackPrefix, customCommand);
        }

        for (Method method : subCommandMethods) {
            SubCommandData subCommandData = new SubCommandData(method, object, method.getAnnotation(SubCommand.class));
            String parentLabel = subCommandData.getSubCommand().parent().toLowerCase();
            if (this.getRegisteredCommands().containsKey(parentLabel)) {
                CustomCommand command = this.getRegisteredCommands().get(parentLabel);

                if (command.getCommandData().getSubCommands().contains(subCommandData)) {
                    continue;
                }

                command.getCommandData().getSubCommands().add(subCommandData);
            }
        }

    }

    public IConverter<?> getConverter(Class<?> tClass) {
        if (this.getConverterMap().containsKey(tClass)) {
            return this.getConverterMap().get(tClass);
        }
        return null;
    }

}
