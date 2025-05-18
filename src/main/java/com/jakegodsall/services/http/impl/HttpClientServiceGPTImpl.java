package com.jakegodsall.services.http.impl;

import com.jakegodsall.services.http.HttpClientService;
import com.jakegodsall.config.impl.ApiKeyConfigImpl;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import com.jakegodsall.config.ApiKeyConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of HttpClientService for GPT-specific HTTP requests.
 */
public class HttpClientServiceGPTImpl implements HttpClientService {
    private static final String API_KEY_ENV_VAR = "OPENAI_API_KEY";
    private final CloseableHttpClient httpClient;
    private String BEARER_TOKEN;

    public HttpClientServiceGPTImpl(CloseableHttpClient httpClient) {
        // First try to get API key from environment variable
        BEARER_TOKEN = System.getenv(API_KEY_ENV_VAR);
        
        // If not found in environment, try to get from config file
        if (BEARER_TOKEN == null || BEARER_TOKEN.trim().isEmpty()) {
            try {
                ApiKeyConfig apiKeyConfig = new ApiKeyConfigImpl();
                BEARER_TOKEN = apiKeyConfig.getApiKeyFromFile(ApiKeyConfigImpl.CONFIG_DIR);
                System.out.println("API key loaded from config file");
            } catch (Exception e) {
                System.err.println("Failed to load API key from file: " + e.getMessage());
                throw new IllegalStateException("No API key found in environment or config file");
            }
        } else {
            System.out.println("API key loaded from environment variable");
        }

        if (BEARER_TOKEN == null || BEARER_TOKEN.trim().isEmpty()) {
            throw new IllegalStateException("Failed to load API key from any source");
        }

        this.httpClient = httpClient;
    }

    @Override
    public HttpResponse sendGetRequest(String url) throws IOException {
        HttpGet request = new HttpGet(url);
        request.setHeader("Authorization", "Bearer " + BEARER_TOKEN);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json; charset=UTF-8");
        return httpClient.execute(request);
    }

    @Override
    public HttpResponse sendPostRequest(String url, String payload) throws IOException {
        HttpPost request = new HttpPost(url);
        request.setHeader("Authorization", "Bearer " + BEARER_TOKEN);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json; charset=UTF-8");

        StringEntity entity = new StringEntity(payload, StandardCharsets.UTF_8);
        request.setEntity(entity);

        return httpClient.execute(request);
    }

    public void close() throws IOException {
        httpClient.close();
    }
} 