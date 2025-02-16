package com.fideosx.staffutils.listeners;

import com.fideosx.staffutils.commands.AdminChat;
import com.fideosx.staffutils.commands.StaffChat;
import com.fideosx.staffutils.utils.ConfigManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ProxyServer;

public class ChatListener implements Listener {
    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();

        // Ignorar mensajes que son comandos
        if (message.startsWith("/")) {
            return;
        }

        if (StaffChat.isToggled(player)) {
            event.setCancelled(true);
            String formattedMessage = ConfigManager.getMessage("staffchat_format")
                    .replace("{player}", player.getName())
                    .replace("{message}", message);
            for (ProxiedPlayer staff : ProxyServer.getInstance().getPlayers()) {
                if (staff.hasPermission(ConfigManager.getPermission("staffchat"))) {
                    staff.sendMessage(new TextComponent(formattedMessage));
                }
            }
            return;
        }

        if (AdminChat.isToggled(player)) {
            event.setCancelled(true);
            String formattedMessage = ConfigManager.getMessage("adminchat_format")
                    .replace("{player}", player.getName())
                    .replace("{message}", message);
            for (ProxiedPlayer admin : ProxyServer.getInstance().getPlayers()) {
                if (admin.hasPermission(ConfigManager.getPermission("adminchat"))) {
                    admin.sendMessage(new TextComponent(formattedMessage));
                }
            }
        }
    }
}
