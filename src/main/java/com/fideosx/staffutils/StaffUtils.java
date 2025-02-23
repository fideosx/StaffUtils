package com.fideosx.staffutils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import com.fideosx.staffutils.utils.ConfigManager;
import com.fideosx.staffutils.commands.*;
import com.fideosx.staffutils.listeners.ChatListener;
import com.fideosx.staffutils.queue.QueueManager;
import com.fideosx.staffutils.queue.QueueCommand;


public class StaffUtils extends Plugin {
    @Override
    public void onEnable() {
        ConfigManager.init(this);
        logModulesStatus();
        registerCommands();
        registerListeners();
        getLogger().info(ChatColor.AQUA + "-----------------------------------------------------");
        getLogger().info(ChatColor.AQUA + "StaffUtils by fideosx.");
        getLogger().info(ChatColor.AQUA + "Source Code: https://github.com/fideosx/StaffUtils.");
        getLogger().info(ChatColor.AQUA + "-----------------------------------------------------");
        getLogger().info(ChatColor.GREEN + "StaffUtils ha sido habilitado correctamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "StaffUtils ha sido deshabilitado correctamente.");
    }
//  Registrar Cmds y Listeners
    private void registerCommands() {
        if (ConfigManager.isModuleEnabled("helpop")) getProxy().getPluginManager().registerCommand(this, new Helpop());
        if (ConfigManager.isModuleEnabled("helpop")) getProxy().getPluginManager().registerCommand(this, new HelpopReply());
        if (ConfigManager.isModuleEnabled("reports")) {
            getProxy().getPluginManager().registerCommand(this, new Report());
            getProxy().getPluginManager().registerCommand(this, new ReportAccept());
            getProxy().getPluginManager().registerCommand(this, new ReportDeny());        
        }
        if (ConfigManager.isModuleEnabled("alerts")) getProxy().getPluginManager().registerCommand(this, new Alert());
        if (ConfigManager.isModuleEnabled("staffchat")) getProxy().getPluginManager().registerCommand(this, new StaffChat());
        if (ConfigManager.isModuleEnabled("adminchat")) getProxy().getPluginManager().registerCommand(this, new AdminChat());
        if (ConfigManager.isModuleEnabled("reload")) getProxy().getPluginManager().registerCommand(this, new Reload());
        if (ConfigManager.isModuleEnabled("info")) getProxy().getPluginManager().registerCommand(this, new Info());
        if (ConfigManager.isModuleEnabled("queue")) getProxy().getPluginManager().registerCommand(this, new QueueCommand());
        if (ConfigManager.isModuleEnabled("queue"))getProxy().getPluginManager().registerListener(this, new QueueManager());
    }

    private void registerListeners() {
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ChatListener());
    }
// Modulos
    private void logModulesStatus() {
        String[] modules = {"helpop", "reports", "alerts", "staffchat", "adminchat", "reload", "info"};
        for (String module : modules) {
            if (ConfigManager.isModuleEnabled(module)) {
                getLogger().info(ChatColor.AQUA + module + ChatColor.GREEN + " activado.");
            } else {
                getLogger().info(ChatColor.AQUA + module + ChatColor.RED + " desactivado.");
            }
        }
    }
}
