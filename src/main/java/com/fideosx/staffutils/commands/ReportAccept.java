package com.fideosx.staffutils.commands;

import com.fideosx.staffutils.utils.ConfigManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class ReportAccept extends Command {
    public ReportAccept() {
        super(ConfigManager.getAlias("report-accept"), null, ConfigManager.getAliases("report-accept"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("reports")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo reports está deshabilitado.");
            return;
        }
        
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("only_players")));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(new TextComponent(ConfigManager.getMessage("report_accept_usage")));
            return;
        }

        ProxiedPlayer staff = (ProxiedPlayer) sender;
        if (!staff.hasPermission(ConfigManager.getPermission("staff"))) {
            staff.sendMessage(new TextComponent(ConfigManager.getMessage("no_permission")));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            staff.sendMessage(new TextComponent(ConfigManager.getMessage("player_not_found")));
            return;
        }

        UUID reporterUUID = Report.reports.remove(target.getUniqueId());
        if (reporterUUID != null) {
            ProxiedPlayer reporter = ProxyServer.getInstance().getPlayer(reporterUUID);
            if (reporter != null) {
                reporter.sendMessage(new TextComponent(ConfigManager.getMessage("report_handled")
                        .replace("{staff}", staff.getName())));
            }
        }

        staff.sendMessage(new TextComponent(ConfigManager.getMessage("report_accepted")
                .replace("{player}", target.getName())));
    }
}
