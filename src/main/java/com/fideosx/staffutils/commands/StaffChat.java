package com.fideosx.staffutils.commands;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import com.fideosx.staffutils.utils.ConfigManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffChat extends Command {
    private static final Map<UUID, Boolean> toggledStaff = new HashMap<>();

    public StaffChat() {
        super("staffchat", null, ConfigManager.getAliases("staffchat"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("staffchat")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo staffchat está deshabilitado.");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!ConfigManager.isModuleEnabled("staffchat")) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("module_disabled")));
            return;
        }
        
        if (!player.hasPermission(ConfigManager.getPermission("staffchat"))) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("no_permission")));
            return;
        }

        if (args.length == 0) {
            boolean isEnabled = toggledStaff.getOrDefault(player.getUniqueId(), false);
            toggledStaff.put(player.getUniqueId(), !isEnabled);
            player.sendMessage(new TextComponent(ConfigManager.getMessage(isEnabled ? "staffchat_disabled" : "staffchat_enabled")));
            return;
        }

        sendStaffChatMessage(player, String.join(" ", args));
    }

    public static boolean isToggled(ProxiedPlayer player) {
        return toggledStaff.getOrDefault(player.getUniqueId(), false);
    }

    public static void sendStaffChatMessage(ProxiedPlayer player, String message) {
        for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
            if (staff.hasPermission(ConfigManager.getPermission("staffchat"))) {
                staff.sendMessage(new TextComponent(ConfigManager.getMessage("staffchat_format")
                        .replace("{player}", player.getName())
                        .replace("{message}", message)));
            }
        }
    }
}
