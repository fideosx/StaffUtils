package com.fideosx.staffutils.commands;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import com.fideosx.staffutils.utils.ConfigManager;

public class Info extends Command {
    public Info() {
        super("staffutils", null, ConfigManager.getAliases("staffutils"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("info")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo info está deshabilitado.");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!ConfigManager.isModuleEnabled("info")) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("module_disabled")));
            return;
        }
        
        if (!player.hasPermission(ConfigManager.getPermission("staff"))) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("no_permission")));
            return;
        }
        
        sender.sendMessage(new TextComponent(ConfigManager.getMessage("header")));
        sender.sendMessage(new TextComponent(ConfigManager.getMessage("lines")));
    }
}
