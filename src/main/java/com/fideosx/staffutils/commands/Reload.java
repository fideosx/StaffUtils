package com.fideosx.staffutils.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import com.fideosx.staffutils.utils.ConfigManager;

public class Reload extends Command {
    public Reload() {
        super("sureload", null, ConfigManager.getAliases("reload"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("reload")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo reload está deshabilitado.");
            return;
        }
        
        ProxiedPlayer player = (ProxiedPlayer) sender;
        
        if (!ConfigManager.isModuleEnabled("reload")) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("module_disabled")));
            return;
        }
        
        if (!player.hasPermission(ConfigManager.getPermission("reload"))) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("no_permission")));
            return;
        }
        
        if (args.length == 0) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("reload_usage")));
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "aliases":
                ConfigManager.reloadAliases();
                sender.sendMessage(new TextComponent(ConfigManager.getMessage("reload_aliases")));
                ProxyServer.getInstance().getLogger().info("[StaffUtils] [Reload] » aliases.yml.");
                break;
            case "messages":
                ConfigManager.reloadMessages();
                sender.sendMessage(new TextComponent(ConfigManager.getMessage("reload_messages")));
                ProxyServer.getInstance().getLogger().info("[StaffUtils] }[Reload] » messages.yml.");
                break;
            case "permissions":
                ConfigManager.reloadPermissions();
                sender.sendMessage(new TextComponent(ConfigManager.getMessage("reload_permissions")));
                ProxyServer.getInstance().getLogger().info("[StaffUtils] [Reload] » permissions.yml.");
                break;
            case "webhooks":
                ConfigManager.reloadPermissions();
                sender.sendMessage(new TextComponent(ConfigManager.getMessage("reload_webhooks")));
                ProxyServer.getInstance().getLogger().info("[StaffUtils] [Reload] » webhooks.yml.");
                break;
            case "all":
                ConfigManager.reloadAll();
                sender.sendMessage(new TextComponent(ConfigManager.getMessage("reload_all")));
                ProxyServer.getInstance().getLogger().info("[StaffUtils] [Reload] » ALL.");
                break;
            default:
                sender.sendMessage(new TextComponent(ConfigManager.getMessage("reload_usage")));
                break;
        }
    }
}
