package com.fideosx.staffutils.queue;

import com.fideosx.staffutils.utils.ConfigManager;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class QueueCommand extends Command {
    
    public QueueCommand() {
        super("queue", null, ConfigManager.getAliases("queue"));
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Este comando solo puede ser usado por jugadores."));
            return;
        }
        
        ProxiedPlayer player = (ProxiedPlayer) sender;
        
        if (args.length < 1) {
            player.sendMessage(new TextComponent(ConfigManager.getMessage("queue_usage")));
            return;
        }
        
        String server = args[0];
        QueueManager.addToQueue(player, server);
    }
}
