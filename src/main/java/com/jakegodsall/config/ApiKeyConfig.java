package com.jakegodsall.config;

import com.jakegodsall.exceptions.ApiKeyNotFoundException;

import java.io.IOException;

/**
 * The {@code ApiKeyConfig} interface provides methods for retrieving and storing
 * API keys in a configuration directory.
 * <p>
 * This interface allows clients to:
 * <ul>
 *   <li>Retrieve an API key from a configuration file.</li>
 *   <li>Store an API key in a configuration file.</li>
 * </ul>
 * Both operations can handle various exceptions related to missing API keys or
 * file access issues.
 *
 */
public interface ApiKeyConfig {

    /**
     * Retrieves the API key from a configuration file located in the given
     * configuration directory.
     * <p>
     * This method reads the API key from the file in the specified directory.
     * It throws an {@code ApiKeyNotFoundException} if the API key cannot be found
     * in the configuration file, and an {@code IOException} for other file
     * input/output errors.
     * </p>
     *
     * @param configDir the directory where the configuration file is located
     * @return the API key as a {@code String}
     * @throws ApiKeyNotFoundException if the API key is not found in the configuration file
     * @throws IOException if an I/O error occurs when accessing the file
     */
    String getApiKeyFromFile(String configDir) throws ApiKeyNotFoundException, IOException;

    /**
     * Stores the given API key in a configuration file located in the specified
     * configuration directory.
     * <p>
     * This method writes the provided API key to a file in the specified directory.
     * If the file cannot be written to or an I/O error occurs, an {@code IOException}
     * is thrown.
     * </p>
     *
     * @param apiKey the API key to store in the file
     * @param configDir the directory where the configuration file is located
     * @throws IOException if an I/O error occurs when writing the file
     */
    void storeApiKeyInFile(String apiKey, String configDir) throws IOException;
}