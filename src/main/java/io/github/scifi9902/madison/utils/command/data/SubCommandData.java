package io.github.scifi9902.madison.utils.command.data;

import io.github.scifi9902.madison.utils.command.annotation.SubCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
public class SubCommandData {


    private final Method method;

    private final Object object;

    private final SubCommand subCommand;

}
