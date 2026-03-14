package com.example.documents.web;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@RequiredArgsConstructor
public class CardsClient {

    private static final String CARDS_BASE_URL = "http://localhost:8080";

    private final Gson gson;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public CardInfo fetchCardInfo(String id) throws IOException {
        String url = CARDS_BASE_URL + "/api/cards/get-info?id=" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                return null;
            }

            if (response.statusCode() != 200) {
                throw new IOException("Unexpected cards service status: " + response.statusCode());
            }

            CardInfoResponse payload = gson.fromJson(response.body(), CardInfoResponse.class);

            if (payload == null || payload.getData() == null || payload.getMessage() == null) {
                return null;
            }

            if (!"success".equalsIgnoreCase(payload.getMessage())) {
                return null;
            }

            return payload.getData();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Cards service call interrupted", e);
        }
    }
}