package com.jakegodsall.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jakegodsall.exceptions.NoSuchLanguageException;
import com.jakegodsall.models.Language;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code LanguageConfig} class handles the loading and retrieval of language configurations
 * from a JSON file. It provides methods to get language details and names.
 */
public class LanguageConfig {
    private static final String LANGUAGE_CONFIG_FILE = "/language_config.json";
    public static Map<String, Language> languageMap;

    static {
        loadConfig();
    }

    /**
     * Loads the language configuration from the specified JSON file into the {@code languageMap}.
     * This method is called when the class is loaded.
     */
    public static void loadConfig() {
        try (InputStream inputStream = LanguageConfig.class.getResourceAsStream(LANGUAGE_CONFIG_FILE)) {
            loadConfig(inputStream);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Loads the language configuration from the provided InputStream into the {@code languageMap}.
     *
     * @param inputStream the InputStream from which to read the language configuration
     * @throws IOException if an I/O error occurs
     */
    public static void loadConfig(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("InputStream is null");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        languageMap = objectMapper.readValue(inputStream, new TypeReference<Map<String, Language>>() {});
    }

    /**
     * Retrieves the {@code Language} object corresponding to the given language code.
     *
     * @param code the language code
     * @return the {@code Language} object for the given code
     * @throws NoSuchLanguageException if the language code is not found in the {@code languageMap}
     */
    public static Language getLanguage(String code) {
        if (!languageMap.containsKey(code.toLowerCase())) {
            throw new NoSuchLanguageException("Language with code '" + code + "' is not found");
        }
        return languageMap.get(code);
    }

    /**
     * Retrieves a map of all language codes to their corresponding language names.
     *
     * @return a map where the keys are language codes and the values are language names
     */
    public static Map<String, String> getAllLanguageNames() {
        Map<String, String> languageNames = new HashMap<>();
        for (Map.Entry<String, Language> entry : languageMap.entrySet()) {
            languageNames.put(entry.getKey(), entry.getValue().getName());
        }
        return languageNames;
    }

    /**
     * Retrieves a list of all supported language codes.
     *
     * @return a list of all supported language codes
     */
    public static List<String> getSupportedLanguageCodes() {
        return new ArrayList<>(languageMap.keySet());
    }
}
