package io.github.scifi9902.madison.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class CC {

    public static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static List<String> chat(List<String> s) {
        return s.stream().map(CC::chat).collect(Collectors.toList());
    }

}
