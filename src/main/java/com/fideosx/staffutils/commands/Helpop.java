package com.fideosx.staffutils.commands;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import com.fideosx.staffutils.utils.ConfigManager;

public class Helpop extends Command {
    public Helpop() {
        super("helpop", null, ConfigManager.getAliases("helpop"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("helpop")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo helpop está deshabilitado.");
            return;
        }
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("only_players")));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_usage")));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        String message = String.join(" ", args);

        for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
            if (staff.hasPermission(ConfigManager.getPermission("staff"))) {
                staff.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_format")
                        .replace("{player}", player.getName())
                        .replace("{message}", message)));
            }
        }
        player.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_sent")));
    }
}
