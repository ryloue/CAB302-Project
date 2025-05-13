package com.example.cab302finalproj.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class API_AI {
    private HttpClient client = HttpClient.newHttpClient();

    public void TalktoAI(String prompt) {
        String url = "http://10.88.50.7:11434/api/generate";

        String jsonBody = """
                {
                    "model": "gemma3:1b",
                    "prompt": "%s",
                    "stream": false
                }
                """.formatted(prompt);

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
    }

    public static void main(String[] args) {
        API_AI obj = new API_AI();
        obj.TalktoAI("what's my name");


    }
}

