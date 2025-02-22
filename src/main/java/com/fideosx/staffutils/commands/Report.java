package com.fideosx.staffutils.commands;

import com.fideosx.staffutils.utils.ConfigManager;
import com.fideosx.staffutils.utils.DiscordWebhook;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Report extends Command {
    public static final Map<UUID, UUID> reports = new HashMap<>();

    public Report() {
        super(ConfigManager.getAlias("report"), null, ConfigManager.getAliases("report"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!ConfigManager.isModuleEnabled("reports")) {
            ProxyServer.getInstance().getLogger().info("[StaffUtils] El módulo reports está deshabilitado.");
            return;
        }

        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Only players can use this command."));
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;

        if (args.length < 2) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("report_usage")));
            return;
        }

        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("player_not_found")));
            return;
        }

        String reason = String.join(" ", args).substring(args[0].length()).trim();
        reports.put(target.getUniqueId(), player.getUniqueId());

        // Obtener la lista de mensajes del webhook
        List<String> webhookMessages = ConfigManager.getWebhookMessages("report");
        List<String> formattedMessages = new ArrayList<>();

        for (String message : webhookMessages) {
            String formattedMessage = message.replace("{player}", player.getName())
                    .replace("{target}", target.getName())
                    .replace("{reason}", reason);
            formattedMessages.add(formattedMessage);
        }

        // Obtener la URL del webhook
        String webhookUrl = ConfigManager.getWebhookUrl("report");

        String webhookHeader = ConfigManager.getWebhookHeader("report");

        // Obtener el ID del rol desde webhooks.yml
        String roleId = ConfigManager.getWebhookRoleId("report");

        if (webhookUrl != null) {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl);

            // Enviar el mensaje con mención de rol si el ID existe
            if (!roleId.isEmpty()) {
                webhook.sendEmbedWithMention(webhookHeader != null && !webhookHeader.isEmpty() ? webhookHeader : "Report Request", formattedMessages, roleId);
            } else {
                if (webhookHeader != null && !webhookHeader.isEmpty()) {
                    webhook.sendEmbed(webhookHeader, formattedMessages);
                } else {
                    webhook.sendEmbed("Report Request", formattedMessages);
                }
            }
        }

        // Crear mensaje con botones para aceptar y denegar
        TextComponent reportMessage = new TextComponent(ConfigManager.getMessage("report_received")
                .replace("{player}", player.getName())
                .replace("{target}", target.getName())
                .replace("{reason}", reason));

        // Botón de Aceptar
        TextComponent acceptButton = new TextComponent(" [ACEPTAR] ");
        acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        acceptButton.setBold(true);
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report-accept " + target.getName()));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Aceptar el reporte de " + target.getName())));

        // Botón de Denegar
        TextComponent denyButton = new TextComponent(" [DENEGAR] ");
        denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
        denyButton.setBold(true);
        denyButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/report-deny " + target.getName()));
        denyButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Denegar el reporte de " + target.getName())));

        // Agregar botones al mensaje
        reportMessage.addExtra(acceptButton);
        reportMessage.addExtra(denyButton);

        // Enviar el mensaje con los botones a los jugadores con permisos de staff
        for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
            if (staff.hasPermission(ConfigManager.getPermission("staff"))) {
                staff.sendMessage(reportMessage);
            }
        }

        // Confirmar al jugador que su reporte ha sido enviado
        player.sendMessage(new TextComponent(ConfigManager.getMessage("report_submitted")));
    }
}
