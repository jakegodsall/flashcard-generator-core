package com.jakegodsall.services.flashcard.impl;

import com.jakegodsall.models.enums.FlashcardType;
import com.jakegodsall.models.flashcards.Flashcard;
import com.jakegodsall.services.http.HttpClientService;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import com.jakegodsall.models.Language;
import com.jakegodsall.models.Options;
import com.jakegodsall.services.flashcard.FlashcardService;
import com.jakegodsall.services.input.InputService;
import com.jakegodsall.services.input.impl.InputServiceInteractiveMode;
import com.jakegodsall.services.json.JsonParseService;
import com.jakegodsall.services.prompt.PromptService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the FlashcardService using OpenAI's GPT API.
 */
@RequiredArgsConstructor
public class FlashcardServiceGPTImpl implements FlashcardService {
    private static final String API_MODELS_URL = "https://api.openai.com/v1/models";
    private static final String API_CHAT_URL = "https://api.openai.com/v1/chat/completions";
    private static final Logger logger = Logger.getLogger(FlashcardServiceGPTImpl.class.getName());

    private final HttpClientService httpClientService;
    private final JsonParseService jsonParseService;
    private final PromptService promptGenerator;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Override
    public List<String> getAvailableModels() {
        try {
            HttpResponse response = httpClientService.sendGetRequest(API_MODELS_URL);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            return jsonParseService.parseModels(result);
        } catch (IOException ioException) {
            logger.log(Level.SEVERE, ioException.getMessage(), ioException);
            return List.of();
        }
    }

    @Override
    public Flashcard generateFlashcard(String targetWord, FlashcardType flashcardType, Language language, Options options) {
        try {
            // Generate the prompt based on the flashcard type
            String prompt = promptGenerator.generatePrompt(targetWord, flashcardType, language, options);
            // Generate HTTP POST request body
            String requestBody = promptGenerator.generateRequestBody(prompt);
            // Send the POST request to the GPT API
            HttpResponse response = httpClientService.sendPostRequest(API_CHAT_URL, requestBody);
            // Get the result
            HttpEntity responseEntity = response.getEntity();
            String result = EntityUtils.toString(responseEntity);
            // Parse the result based on the flashcard type
            return jsonParseService.parseFlashcard(result, flashcardType);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            System.err.println(ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Flashcard> generateMultipleFlashcardsForWord(String targetWord, FlashcardType flashcardType, Language language, Options options, int count) {
        try {
            // Generate the prompt for multiple flashcards
            String prompt = promptGenerator.generatePromptForMultipleFlashcards(targetWord, flashcardType, language, options, count);
            // Generate HTTP POST request body
            String requestBody = promptGenerator.generateRequestBody(prompt);
            // Send the POST request to the GPT API
            HttpResponse response = httpClientService.sendPostRequest(API_CHAT_URL, requestBody);
            // Get the result
            HttpEntity responseEntity = response.getEntity();
            String result = EntityUtils.toString(responseEntity);
            // Parse multiple flashcards from the result
            return jsonParseService.parseMultipleFlashcards(result, flashcardType);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            System.err.println(ex.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Flashcard> generateFlashcardsInteractively(FlashcardType flashcardType, Language language, Options options) throws IOException {
        List<Flashcard> flashcards = new ArrayList<>();

        // Instantiate the interactive mode input service
        InputService interactiveInput = new InputServiceInteractiveMode(new BufferedReader(new InputStreamReader(System.in)));

        while (true) {
            // Get input from the user
            List<String> inputWord = interactiveInput.getInput();

            // As the interactive input getInput() returns a singleton list
            String input = inputWord.getFirst();

            if (input.trim().isEmpty()) {
                System.out.println("You entered an empty value. Please provide a valid word");
                continue;
            }
            // Exit condition
            if ("-1".equals(input)) {
                System.out.println("Exiting interactive mode...");
                break;
            }

            // Otherwise generate the flashcard and ad it to the List
            Flashcard flashcard = generateFlashcard(input, flashcardType, language, options);
            flashcards.add(flashcard);

            System.out.println(flashcard);
            System.out.println(flashcards.size() + " flashcards generated.");
        }

        // return the list of generated flashcards
        return flashcards;
    }

    @Override
    public List<Flashcard> generateFlashcardsSequentially(List<String> targetWords, FlashcardType flashcardType, Language language, Options options) {
        List<Flashcard> flashcards = new ArrayList<>();

        // Track the start time
        long startTime = System.currentTimeMillis();

        // Iterate through words, generate flashcards and store them in the List<Flashcard>
        for (int i = 0; i < targetWords.size(); i++) {
            flashcards.add(generateFlashcard(targetWords.get(i), flashcardType, language, options));
            System.out.println("Flashcard " + (i + 1) + " of " + targetWords.size() + " generated.");
        }

        // Track the end time
        long endTime = System.currentTimeMillis();

        // Calculate the total time and print it
        System.out.println("All flashcards generated. Total time: " + (endTime - startTime) / 1000 + " seconds.");

        return flashcards;
    }

    @Override
    public List<Flashcard> generateFlashcardsConcurrently(List<String> targetWords, FlashcardType flashcardType, Language language, Options options) throws InterruptedException, ExecutionException {
        List<Future<Flashcard>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        // Submit a task for each word and inform the client
        for (String targetWord : targetWords) {
            futures.add(executorService.submit(() -> generateFlashcard(targetWord, flashcardType, language, options)));
        }

        System.out.println("All tasks submitted. Waiting for results...");

        // Collect the results and report progress
        List<Flashcard> flashcards = new ArrayList<>();
        for (int i = 0; i < futures.size(); i++) {
            Future<Flashcard> future = futures.get(i);
            try {
                flashcards.add(future.get());
                System.out.println("Flashcard " + (i + 1) + " of " + targetWords.size() + " generated.");
            } catch (ExecutionException e) {
                System.err.println("Error generating flashcard for task " + (i + 1) + ": " + e.getCause());
                throw e;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("All flashcards generated. Total time: " + (endTime - startTime) / 1000 + " seconds.");

        // Properly shut down the executor service
        executorService.shutdown();
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("Executor did not terminate in the specified time.");
        }

        return flashcards;
    }

    public void shutdownService() {
        executorService.shutdown();
    }
}