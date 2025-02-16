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

public class AdminChat extends Command {
    private static final Map<UUID, Boolean> toggledAdmins = new HashMap<>();

    public AdminChat() {
        super("adminchat", null, ConfigManager.getAliases("adminchat"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("adminchat")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo adminchat está deshabilitado.");
            return;
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("only_players")));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission(ConfigManager.getPermission("adminchat"))) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("no_permission")));
            return;
        }

        if (args.length == 0) {
            boolean isEnabled = toggledAdmins.getOrDefault(player.getUniqueId(), false);
            toggledAdmins.put(player.getUniqueId(), !isEnabled);
            player.sendMessage(new TextComponent(ConfigManager.getMessage(isEnabled ? "adminchat_disabled" : "adminchat_enabled")));
            return;
        }

        sendAdminChatMessage(player, String.join(" ", args));
    }

    public static boolean isToggled(ProxiedPlayer player) {
        return toggledAdmins.getOrDefault(player.getUniqueId(), false);
    }

    public static void sendAdminChatMessage(ProxiedPlayer player, String message) {
        for (ProxiedPlayer admin : ProxyServer.getInstance().getPlayers()) {
            if (admin.hasPermission(ConfigManager.getPermission("adminchat"))) {
                admin.sendMessage(new TextComponent(ConfigManager.getMessage("adminchat_format")
                        .replace("{player}", player.getName())
                        .replace("{message}", message)));
            }
        }
    }
}
