package com.fideosx.staffutils.utils;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class DiscordWebhook {
    private final String webhookUrl;

    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    // Método para enviar el embed sin mención de rol
    public void sendEmbed(String title, List<String> descriptions) {
        // Verificar que la URL es válida
        if (!isValidUrl(webhookUrl)) {
            System.out.println("[StaffUtils] [WebHooks] » Eror, url invalido o no encontrado. URL: " + webhookUrl);
            return;  // Log error
        }

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Concatenar las líneas con saltos de línea
            String fullDescription = String.join("\n", descriptions);

            // Crear el JSON de embed
            JSONObject embed = new JSONObject();
            embed.put("title", title);
            embed.put("description", fullDescription);
            embed.put("color", 16711680); // Color rojo

            // Crear el JSON del payload
            JSONArray embeds = new JSONArray();
            embeds.put(embed);

            JSONObject payload = new JSONObject();
            payload.put("embeds", embeds);

            // Enviar el payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            connection.getResponseCode(); // Enviar la solicitud
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para enviar el embed con mención de rol opcional
    public void sendEmbedWithMention(String title, List<String> descriptions, String roleId) {
        // Verificar que la URL es válida
        if (!isValidUrl(webhookUrl)) {
            System.out.println("[StaffUtils] [WebHooks] » Eror, url invalido o no encontrado. URL: " + webhookUrl);
            return;  // Log error
        }

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Concatenar las líneas con saltos de línea
            String fullDescription = String.join("\n", descriptions);

            // Crear el JSON de embed
            JSONObject embed = new JSONObject();
            embed.put("title", title);
            embed.put("description", fullDescription);
            embed.put("color", 16711680); // Color rojo

            // Crear el JSON del payload
            JSONArray embeds = new JSONArray();
            embeds.put(embed);

            JSONObject payload = new JSONObject();
            payload.put("embeds", embeds);

            // Si se proporciona un ID de rol, agregar la mención
            if (roleId != null && !roleId.isEmpty()) {
                String mention = "<@&" + roleId + ">"; // Formato para mencionar el rol
                payload.put("content", mention); // Mencionar el rol en el mensaje
            }

            // Enviar el payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            connection.getResponseCode(); // Enviar la solicitud
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para validar si la URL es válida
    private boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            // Verificar que la URL tiene un protocolo adecuado
            return url.getProtocol().equalsIgnoreCase("http") || url.getProtocol().equalsIgnoreCase("https");
        } catch (MalformedURLException e) {
            return false;  // Si la URL no es válida, lanzar excepción y retornar false
        }
    }
}
