package com.example.cab302finalproj.model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * A utility class for making API calls to a local AI.
 * This class provides methods to interact with AI models running on localhost.
 */
public class API_AI {
    private HttpClient client = HttpClient.newHttpClient();

    /**
     * Escapes special characters in a string to make it JSON-safe.
     *
     * @param text the input string that may contain special characters
     * @return a JSON-safe string with escaped characters
     * @throws NullPointerException if text is null
     *
     * @example
     * String input = "Hello \"World\"\nNew line";
     * String escaped = escapeJson(input);
     * // Result: "Hello \\\"World\\\"\\nNew line"
     */
    public static String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Makes a synchronous API call to a local AI with the given prompt.
     * This method sends a POST request to the local Ollama API endpoint and returns
     * the raw JSON response from the AI model.
     *
     * @param prompt the text prompt to send to the AI model for processing
     * @return the raw JSON response string from the AI service, which typically contains
     *         the generated text along with metadata
     * @example
     * String response = Call_Ai("What is the capital of France?");
     * // Returns JSON response containing the AI's answer
     */
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