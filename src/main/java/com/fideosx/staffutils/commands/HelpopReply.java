package com.fideosx.staffutils.commands;

import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;
import com.fideosx.staffutils.utils.ConfigManager;

public class HelpopReply extends Command {
    public HelpopReply() {
        super("helpop-reply", null, ConfigManager.getAliases("helpop-reply"));
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

        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_reply_usage")));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("player_not_online")));
            return;
        }
        if (!sender.hasPermission(ConfigManager.getPermission("staff"))) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("no_permission")));
            return;
        }

        String replyMessage = String.join(" ", args).substring(args[0].length()).trim();
        target.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_reply_format")
                .replace("{staff}", sender.getName())
                .replace("{message}", replyMessage)));
        sender.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_reply_sent")));
    }
}
