package com.fideosx.staffutils.commands;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import com.fideosx.staffutils.utils.ConfigManager;

public class Alert extends Command {
    public Alert() {
        super("alert", null, ConfigManager.getAliases("alert"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("alerts")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo alerts está deshabilitado.");
            return;
        }
        if (!sender.hasPermission(ConfigManager.getPermission("alert"))) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("no_permission")));
            return;
        }

        if (args.length == 0) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("alert_usage")));
            return;
        }

        String message = String.join(" ", args);
        String formattedMessage = ConfigManager.getMessage("alert_format").replace("{message}", message);
        
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            player.sendMessage(new TextComponent(formattedMessage));
        }
    }
}
