package com.fideosx.staffutils.commands;

import com.fideosx.staffutils.utils.ConfigManager;
import com.fideosx.staffutils.utils.DiscordWebhook;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.ProxyServer;

import java.util.ArrayList;
import java.util.List;

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

        // Obtener el ID del rol desde webhooks.yml
        String roleId = ConfigManager.getWebhookRoleId("helpop");

        // Enviar el mensaje al webhook de helpop si está configurado
        String webhookUrl = ConfigManager.getWebhookUrl("helpop");
        if (webhookUrl != null) {
            List<String> webhookMessages = ConfigManager.getWebhookMessages("helpop");
            List<String> formattedMessages = new ArrayList<>();

            for (String line : webhookMessages) {
                formattedMessages.add(line.replace("{player}", player.getName()).replace("{message}", message));
            }

            String webhookHeader = ConfigManager.getWebhookHeader("helpop");
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);

            // Enviar el mensaje con mención de rol si el ID existe
            if (!roleId.isEmpty()) {
                webhook.sendEmbedWithMention(webhookHeader != null && !webhookHeader.isEmpty() ? webhookHeader : "Helpop Request", formattedMessages, roleId);
            } else {
                if (webhookHeader != null && !webhookHeader.isEmpty()) {
                    webhook.sendEmbed(webhookHeader, formattedMessages);
                } else {
                    webhook.sendEmbed("Helpop Request", formattedMessages);
                }
            }
        }

        // Enviar el mensaje a los jugadores con permisos de staff
        for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
            if (staff.hasPermission(ConfigManager.getPermission("staff"))) {
                staff.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_format")
                        .replace("{player}", player.getName())
                        .replace("{message}", message)));
            }
        }

        // Confirmar al jugador que su ayuda ha sido enviada
        player.sendMessage(new TextComponent(ConfigManager.getMessage("helpop_sent")));
    }
}
