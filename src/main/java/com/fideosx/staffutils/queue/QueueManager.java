package com.fideosx.staffutils.queue;

import com.fideosx.staffutils.utils.ConfigManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class QueueManager implements Listener {
    private static final Map<String, LinkedList<QueuedPlayer>> queues = new HashMap<>();
    private static final Map<UUID, ScheduledTask> queueTasks = new HashMap<>();

    public static void addToQueue(ProxiedPlayer player, String server) {
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
        if (serverInfo == null) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("server_not_found").replace("{server}", server)));
            return;
        }

        queues.putIfAbsent(server, new LinkedList<>());
        LinkedList<QueuedPlayer> queue = queues.get(server);
        
        if (queue.stream().anyMatch(qp -> qp.getUuid().equals(player.getUniqueId()))) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("queue_already").replace("{server}", server)));
            return;
        }
        
        int priority = getPriority(player);
        QueuedPlayer qp = new QueuedPlayer(player.getUniqueId(), priority);
        queue.add(qp);
        queue.sort(Comparator.comparingInt(QueuedPlayer::getPriority).reversed());
        
        player.sendMessage(new TextComponent(ConfigManager.getMessage("queue_join").replace("{server}", server)));
        startQueueUpdates(player, server);
    }
    
    public static void removeFromQueue(ProxiedPlayer player) {
        queueTasks.remove(player.getUniqueId());
        queues.values().forEach(queue -> queue.removeIf(qp -> qp.getUuid().equals(player.getUniqueId())));
    }
    
    private static void startQueueUpdates(ProxiedPlayer player, String server) {
        ScheduledTask task = ProxyServer.getInstance().getScheduler().schedule(
            ProxyServer.getInstance().getPluginManager().getPlugin("StaffUtils"),
            () -> {
                LinkedList<QueuedPlayer> queue = queues.get(server);
                if (queue == null) return;
                
                int position = 1;
                for (QueuedPlayer qp : queue) {
                    if (qp.getUuid().equals(player.getUniqueId())) {
                        player.sendMessage(new TextComponent(ConfigManager.getMessage("queue_status")
                                .replace("{server}", server)
                                .replace("{position}", String.valueOf(position))
                                .replace("{total}", String.valueOf(queue.size()))));
                        if (position == 1) {
                            ServerInfo targetServer = ProxyServer.getInstance().getServerInfo(server);
                            if (targetServer != null) {
                                player.connect(targetServer);
                                removeFromQueue(player);
                            }
                        }
                        return;
                    }
                    position++;
                }
            },
            10, 10, TimeUnit.SECONDS
        );
        queueTasks.put(player.getUniqueId(), task);
    }
    
    private static int getPriority(ProxiedPlayer player) {
        for (int i = 4; i >= 1; i--) {
            if (player.hasPermission(ConfigManager.getPermission("queue_priority_" + i))) {
                return i;
            }
        }
        return 0;
    }
    
    private static class QueuedPlayer {
        private final UUID uuid;
        private final int priority;

        public QueuedPlayer(UUID uuid, int priority) {
            this.uuid = uuid;
            this.priority = priority;
        }

        public UUID getUuid() {
            return uuid;
        }

        public int getPriority() {
            return priority;
        }
    }
}
