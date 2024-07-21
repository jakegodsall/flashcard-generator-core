package org.jakegodsall.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jakegodsall.exceptions.ApiKeyNotFoundException;

import java.io.*;
import java.util.logging.Logger;

/**
 * Configuration utility class for handling API key configuration.
 * Provides methods to read and write the API key from/to a JSON configuration file in the ~/.flashcard-generator directory.
 */
public class ApiKeyConfig {
    /**
     * File name of the file containing the API key.
     */
    public static final String CONFIG_FILE = "/api_config.json";

    /**
     * Directory for the configuration file, defaulting to the user's home directory.
     */
    public static final String CONFIG_DIR = System.getProperty("user.home") + "/.flashcard-generator";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(ApiKeyConfig.class.getName());

    /**
     * Reads the API key from the JSON configuration file.
     *
     * @return the API key as a String, or an empty string if the key is not found or an error occurs.
     */
    public static String getApiKeyFromJsonFile() {
        // get API file
        try (InputStream inputStream = getFileStream(CONFIG_FILE)) {
            // Navigate API file
            JsonNode rootNode = objectMapper.readTree(inputStream);
            System.out.println("Root Node: " + rootNode);
            JsonNode apiKey = rootNode.path("apiKey");

            // Check to see if field exists
            if (apiKey.isMissingNode() || !apiKey.isValueNode())
                throw new ApiKeyNotFoundException("API key not found");

            // Return the API key
            return apiKey.asText();
        } catch (ApiKeyNotFoundException | IOException exception) {
            System.err.println(exception.getMessage());
        }
        return "";
    }

    /**
     * Stores the API configuration file in a secret directory in the user's home directory.
     *
     * @param apiKey the key to store in the configuration file.
     */
    public static void storeApiKeyInJsonFile(String apiKey) {
        // Check to see if the directory already exists

        // if it doesn't exist, create it

        // put the key into a file

        // save the file in the directory
    }

    /**
     * Returns an InputStream for the specified configuration file.
     *
     * @param fileName the name of the configuration file.
     * @return an InputStream for the specified file.
     * @throws FileNotFoundException if the file does not exist.
     */
    public static InputStream getFileStream(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        if (!file.exists())
            throw new FileNotFoundException("API file does not exist");
        return new FileInputStream(file);
    }


}
