package com.fideosx.staffutils.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {
    private static Configuration messages;
    private static Configuration permissions;
    private static Configuration aliases;
    private static Configuration modules;
    private static Configuration webhooks;
    private static Plugin plugin;

    public static void init(Plugin pluginInstance) {
        plugin = pluginInstance;
        messages = loadConfig("messages.yml");
        permissions = loadConfig("permissions.yml");
        aliases = loadConfig("aliases.yml");
        modules = loadConfig("modules.yml", getDefaultModulesContent());
        webhooks = loadConfig("webhooks.yml");
    }

    private static Configuration loadConfig(String fileName) {
        return loadConfig(fileName, null);
    }

    private static Configuration loadConfig(String fileName, String defaultContent) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                if (defaultContent != null) {
                    Files.write(file.toPath(), defaultContent.getBytes());
                } else {
                    InputStream stream = plugin.getResourceAsStream(fileName);
                    if (stream != null) {
                        Files.copy(stream, file.toPath());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            return YamlConfiguration.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reloadAliases() {
        aliases = loadConfig("aliases.yml");
    }

    public static void reloadMessages() {
        messages = loadConfig("messages.yml");
    }

    public static void reloadPermissions() {
        permissions = loadConfig("permissions.yml");
    }

    public static void reloadModules() {
        modules = loadConfig("modules.yml", getDefaultModulesContent());
    }

    public static void reloadWebhooks() {
        webhooks = loadConfig("webhooks.yml");
    }

    public static void reloadAll() {
        reloadAliases();
        reloadMessages();
        reloadPermissions();
        reloadModules();
        reloadWebhooks();
    }

    public static String getPrefix() {
        return ColorUtil.colorize(messages.getString("messages.prefix", "&7[&bStaffUtils&7] "));
    }

    public static String getMessage(String key) {
        if (messages == null) {
            return "&cMensaje no encontrado: " + key;
        }
        
        if (messages.get("messages." + key) instanceof List) {
            List<String> messageList = messages.getStringList("messages." + key);
            return messageList.stream()
                    .map(ColorUtil::colorize)
                    .map(line -> line.replace("{prefix}", getPrefix()))
                    .collect(Collectors.joining("\n"));
        }
        
        String message = messages.getString("messages." + key, "&cMensaje no encontrado: " + key);
        return ColorUtil.colorize(message).replace("{prefix}", getPrefix());
    }

    public static String getPermission(String key) {
        return permissions != null ? permissions.getString("permissions." + key, "staffutils.default") : "staffutils.default";
    }

    public static String getAlias(String key) {
        return aliases != null ? aliases.getStringList("aliases." + key).get(0) : key;
    }

    public static String[] getAliases(String key) {
        List<String> aliasList = aliases != null ? aliases.getStringList("aliases." + key) : null;
        return aliasList != null ? aliasList.toArray(new String[0]) : new String[]{key};
    }

    public static boolean isModuleEnabled(String module) {
        return modules != null && modules.getBoolean("modules." + module, true);
    }

    // Método para obtener la URL del webhook
    public static String getWebhookUrl(String key) {
        if (webhooks == null) {
            return null;
        }
        return webhooks.getString("webhooks." + key + ".url", null);
    }

    // Método para obtener los mensajes del webhook en formato de lista
    public static List<String> getWebhookMessages(String key) {
        if (webhooks == null) {
            return null;
        }
        return webhooks.getStringList("webhooks." + key + ".message");
    }

    // Método para obtener el header del webhook
    public static String getWebhookHeader(String key) {
        if (webhooks == null) {
            return null;
        }
        return webhooks.getString("webhooks." + key + ".header", null);
    }

    // Obtener ID del rol del webhook
    public static String getWebhookRoleId(String key) {
        if (webhooks == null) {
            return null;
        }
        return webhooks.getString("webhooks." + key + ".id", null);
    }

    private static String getDefaultModulesContent() {
        return "modules:\n" +
               "  helpop: true\n" +
               "  reports: true\n" +
               "  queue: true\n" +
               "  alerts: false\n" +
               "  staffchat: false\n" +
               "  adminchat: false\n" +
               "  reload: true\n" +
               "  info: true\n";
            
    }
}

class ColorUtil {
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
