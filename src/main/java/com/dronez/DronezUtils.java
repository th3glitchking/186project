package com.dronez;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;

public final class DronezUtils {
    public static void sendChatMessage(String format, Object... args) {
        Minecraft.getInstance().player.sendMessage(new StringTextComponent(String.format(format, args)));
    }

    public static void droneSays(String format, Object... args) {
        sendChatMessage("[Drone] " + format, args);
    }

    public static void debug(String format, Object... args) {
        LogManager.getLogger().debug(String.format(format, args));
    }
}
