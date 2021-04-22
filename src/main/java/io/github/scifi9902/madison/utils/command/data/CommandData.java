package io.github.scifi9902.madison.utils.command.data;

import io.github.scifi9902.madison.utils.command.CustomCommand;
import io.github.scifi9902.madison.utils.command.annotation.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CommandData {

    private final Method method;

    private final Object object;

    private final Command command;

    private final List<SubCommandData> subCommands;

    public CommandData(Method method, Object object, Command command) {
        this.method = method;
        this.object = object;
        this.command = command;
        this.subCommands = new ArrayList<>();
    }
}
