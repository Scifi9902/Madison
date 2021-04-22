package io.github.scifi9902.madison.utils.command;

import io.github.scifi9902.madison.utils.CC;
import io.github.scifi9902.madison.utils.command.annotation.Optional;
import io.github.scifi9902.madison.utils.command.converter.IConverter;
import io.github.scifi9902.madison.utils.command.data.CommandData;
import io.github.scifi9902.madison.utils.command.data.SubCommandData;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CustomCommand extends Command implements TabCompleter {

    private final CommandData commandData;

    private final CommandHandler commandHandler;

    public CustomCommand(CommandHandler commandHandler, CommandData commandData) {
        super(commandData.getCommand().label());

        this.commandHandler = commandHandler;
        this.commandData = commandData;

        if (this.commandData.getCommand().aliases().length > 0) {
            this.setAliases(Arrays.asList(this.commandData.getCommand().aliases()));
        }

        this.setLabel("/" + this.commandData.getCommand().label());

    }

    @SneakyThrows
    @Override
    public boolean execute(CommandSender commandSender, String label, String[] arguments) {

        Object object;
        Method method;
        SubCommandData subCommandData = null;
        String[] args;
        String permission;

        if (arguments.length >= 1 && !this.commandData.getSubCommands().isEmpty() && this.commandData.getSubCommands().stream().anyMatch(subData -> subData.getSubCommand().label().equalsIgnoreCase(arguments[0]) || Arrays.stream(subData.getSubCommand().aliases()).filter(alias -> alias.equalsIgnoreCase(arguments[0])).findAny().orElse(null) != null)) {
            subCommandData = this.commandData.getSubCommands().stream().filter(data -> data.getSubCommand().label().equalsIgnoreCase(arguments[0]) || Arrays.stream(data.getSubCommand().aliases()).filter(alias -> alias.equalsIgnoreCase(arguments[0])).findFirst().orElse(null) != null).findFirst().orElse(null);
            args = Arrays.copyOfRange(arguments, 1, arguments.length);

            if (subCommandData != null) {
                object = subCommandData.getObject();
                method = subCommandData.getMethod();
                permission = subCommandData.getSubCommand().permission();
            } else {
                object = commandData.getObject();
                method = commandData.getMethod();
                permission = commandData.getCommand().permission();
            }
        } else {
            object = commandData.getObject();
            method = commandData.getMethod();
            args = arguments;
            permission = commandData.getCommand().permission();
        }

        if (method.getParameters()[0].getType().equals(Player.class) && !(commandSender instanceof Player)) {
            commandSender.sendMessage(CC.chat("&cOnly players may execute this command."));
            return true;
        }

        if (!commandSender.hasPermission(permission)) {
            commandSender.sendMessage(CC.chat("&cNo permission."));
            return true;
        }


        Parameter[] parameters = Arrays.copyOfRange(method.getParameters(), 1, method.getParameters().length);

        if (parameters.length > 0 && !parameters[0].getType().isArray()) {
            Object[] converted = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];

                Optional optional = parameter.getAnnotation(Optional.class);


                if (i >= args.length && optional == null) {
                    if (subCommandData == null) {
                        commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + Arrays.stream(parameters).map(parameter1 -> "<" + (parameter1.isAnnotationPresent(io.github.scifi9902.madison.utils.command.annotation.Parameter.class) ? parameter1.getAnnotation(io.github.scifi9902.madison.utils.command.annotation.Parameter.class).name() : parameter1.getName()) + ">").collect(Collectors.joining(" ")));
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + subCommandData.getSubCommand().label() + " " + Arrays.stream(parameters).map(parameter1 -> "<" + (parameter1.isAnnotationPresent(io.github.scifi9902.madison.utils.command.annotation.Parameter.class) ? parameter1.getAnnotation(io.github.scifi9902.madison.utils.command.annotation.Parameter.class).name() : parameter1.getName()) + ">").collect(Collectors.joining(" ")));
                    }

                    return true;
                }

                IConverter<?> converter = this.getCommandHandler().getConverter(parameter.getType());

                if (converter == null) {
                    throw new IllegalArgumentException("Unable to locate a converter for " + parameter.getType().getName() + ".");
                } else {
                    converted[i] = converter.fromString(commandSender, i >= args.length ? optional.value() : args[i]);
                }
            }

            converted = ArrayUtils.add(converted, 0, method.getParameters()[0].getType().cast(commandSender));

            method.invoke(object, converted);
            return true;
        } else if (parameters.length == 1 && parameters[0].getType().isArray()) {
            method.invoke(object, commandSender, args);
        } else {
            method.invoke(object, commandSender);
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] strings) {
        String[] args;
        Method method;
        if (strings.length >= 1 && !this.commandData.getSubCommands().isEmpty() && this.commandData.getSubCommands().stream().anyMatch(subCommand -> !subCommand.getSubCommand().label().isEmpty() && subCommand.getSubCommand().label().equalsIgnoreCase(strings[1]) || Arrays.stream(subCommand.getSubCommand().aliases()).anyMatch(s -> s.equalsIgnoreCase(strings[1])))) {
            args = Arrays.copyOfRange(strings, 1, strings.length);
            SubCommandData subCommandData = this.commandData.getSubCommands().stream().filter(subCommandData1 -> subCommandData1.getSubCommand().label().equalsIgnoreCase(strings[1]) || Arrays.stream(subCommandData1.getSubCommand().aliases()).anyMatch(s -> s.equalsIgnoreCase(strings[1]))).findFirst().orElse(null);

            if (subCommandData != null) {
                method = subCommandData.getMethod();
            } else {
                method = commandData.getMethod();
            }

        } else {
            method = commandData.getMethod();
            args = strings;
        }

        final IConverter<?> converter = method.getParameters().length > args.length ? this.getCommandHandler().getConverter(method.getParameters()[args.length].getType()) : this.commandHandler.getConverter(String.class);
        return converter == null ? super.tabComplete(commandSender, label, args) : converter.tabComplete(commandSender);
    }
}
