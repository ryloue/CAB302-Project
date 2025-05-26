package com.example.cab302finalproj.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class API_AI {
    private HttpClient client = HttpClient.newHttpClient();
    public static String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
    public static String Call_Ai(String prompt) {
        String url = "http://127.0.0.1:11434/api/generate";
//        String url = "http://10.88.50.7:11434/api/generate";

        String escapedPrompt = escapeJson(prompt);  // Escape problematic characters

        String jsonBody = """
                {
                    "model": "zephyr",
                    "prompt": "%s",
                    "stream": false
                }
                """.formatted(escapedPrompt); // change the model name to zephyr or tinyllama:1.1b

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(response.body());
        return response.body();
    }


}