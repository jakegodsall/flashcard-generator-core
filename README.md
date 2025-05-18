# Flashcard Generator Core

This project is the `flashcard-generator-core` library that uses the GPT API to create basic sentences using provided words in different languages.

It helps in learning languages by generating example sentences.
The library supports various languages and can export the generated sentences to different file formats such as CSV and JSON.

## Features
- Generate flashcards using a specified word or set of words in various languages.
- Support for different language-specific options, such as choosing grammatical tense or including stress symbols.
- Configuration for language-specific features via a JSON file.
- Export generated flashcards to multiple file formats (CSV, JSON).
- Easily extendable to support more languages.
- Strict formatting rules for consistent and high-quality flashcards:
  - Automatic "to" prefix for infinitive verbs in both languages
  - Consistent capitalization (lowercase for vocabulary, proper sentence case for examples)
  - Proper sentence punctuation
  - Prevention of target word appearance in native language sentences
- Support for generating multiple varied flashcards for the same word

## Requirements
- Java 22 or higher
- Maven
- An API Key (currently only OpenAI API available)


## Setup
1. Clone the repository:

   ```shell
   git clone https://github.com/yourusername/flashcard-generator-core.git
   cd flashcard-generator-core
   ```
   
2. Build the project:

   ```shell
   mvn clean package
   ```

3. Use the generated JAR file in your applications.

   After building the project, the JAR file located in the `target/` directory can be included as a dependency in other applications that wish to utilize the flashcard generation capabilities of this library.

   ```shell
   cd target/
   ```

## API Key Management

The Flashcard Generator Core requires an API key to access the language processing services that power the flashcard generation functionality.

### Storage

The application or service using this core library will need to handle API key management.

The library provides a `ApiKeyConfig` class that can read and write a provided API key to a `api_config.json` file in a `.flashcard-generator` directory in the user's home directory.

##### Important Note
It is important to keep your API key secure and not share it with others. Usage charges may apply depending on the services you use through the application.

## Usage

To generate flashcards with the application there is essentially a three-step process.
1. Load the words into memory using an implementation of `InputService`.
2. Generate the flashcards using an implementation of `FlashcardService`.
3. Export the output using an implementation of `OutputService`.  

While the Flashcard Generator Core does not provide a direct user interface, it is designed to be integrated into other applications that offer various modes for inputting data and exporting the generated flashcards.
This flexibility allows developers to create applications that fit their specific workflow and preferences.

### Input Services

1. **Interactive Mode** (`InputServiceInteractiveMode.java`): Applications using this core can implement an interactive mode where users are prompted to enter words or phrases one at a time. The application would then use the core library to generate flashcards for each input.

2. **Comma Separated String Mode** (`InputServiceCommaSeparatedStringMode.java`): Developers can implement batch processing where the core library processes a list of words provided as a single string, where each word is delimited by a comma.

3. **Plain Text File Mode** (`InputServicePlainTextFileMode.java`): Applications can be designed to read input from files (e.g., text files) and pass this data to the core library for flashcard generation.

### Flashcard Services

1. **GPT Implementation** (`FlashcardServiceGPTImpl.java`): Currently the only flashcard service available is using the OpenAI API. The service uses a `PromptService` to generate the appropriate prompts to the API, `HttpClientService` to generate the HTTP request and handle the HTTP response, and a `JsonParseService` to parse the response under the hood.

#### Flashcard Formatting Rules

The library enforces strict formatting rules to ensure consistency and quality in generated flashcards:

1. **Verb Handling**:
   - Infinitive verbs are automatically prefixed with "to " in both native and target languages
   - Example: "to run" / "to speak" / "to write"

2. **Case Consistency**:
   - Vocabulary words (both native and target) are always in lowercase
   - Example sentences follow proper capitalization rules (start with capital letter)

3. **Sentence Structure**:
   - All example sentences must end with a period
   - Proper sentence structure and punctuation are enforced

4. **Word Usage in Sentences**:
   - For sentence-based flashcards, the target word NEVER appears in the native language sentence
   - Instead, appropriate translations or alternative phrasings are used
   - This ensures proper learning without direct word repetition

5. **Multiple Flashcard Generation**:
   - When generating multiple flashcards for the same word, each flashcard:
     - Uses exactly the same target word (no synonyms or related words)
     - Provides different example sentences and contexts
     - Maintains all formatting rules consistently

### Output Services

1. **CSV Mode** (`OutputServiceCsvMode.java`): Flashcards can be exported to a CSV file, where each flashcard is a separate line with fields separated by commas.
2. **JSON Mode** (`OutputServiceJsonMode.java`): Flashcards can be exported as a JSON file, which is useful for applications that require structured data.

## Language Configuration

The Flashcard Generator Core allows for language-specific configurations to tailor the flashcard generation process to the nuances of different languages.
Language-specific features are configured via the `language_config.json` file located in the `src/main/resources` directory. This file includes options for stress symbols, formality, gender, dialects, politeness levels, and conjugation tenses.

Example `language_config.json`:

```json
{
"ru": {
   "name": "Russian",
   "supports_stress": true,
   "tenses": ["PAST", "PRESENT", "FUTURE"],
   "genders": ["MASCULINE", "FEMININE", "NEUTER"]
  },
"pl": {
   "name": "Polish",
   "supports_stress": false,
   "tenses": ["PAST", "PRESENT", "FUTURE"],
   "genders": ["MASCULINE", "FEMININE", "NEUTER"]
},
"es": {
   "name": "Spanish",
   "supports_stress": false,
   "tenses": ["PAST", "PRESENT", "FUTURE"],
   "genders": ["MASCULINE", "FEMININE"]
  }
}
```

The choice of languages and the specific features that can be configured are heavily dependent on the capabilities of the underlying language model that the application uses. The language model's ability to accurately generate content in different languages and handle linguistic nuances like stress, gender, and tense is a key factor in determining how well the application can support each language.

## Gitflow Workflow Integration

The repository follows the Gitflow branching model and uses three GitHub Actions workflows for automation:

1. **Test Workflow**:
   - Reusable workflow located in `.github/workflows/test.yml`.
   - Runs tests on the project using a Docker container.

2. **Push-Develop Workflow**:
   - Triggered on pushes to the `develop` branch.
   - Calls the reusable `test.yml` workflow to ensure all tests pass before further development.

3. **Push-Main Workflow**:
   - Triggered on pushes to the `main` branch.
   - Calls the reusable `test.yml` workflow to validate the code.
   - After tests pass, deploys the project to Maven Central.

### Workflow Directory

- `.github/workflows/test.yml`: Contains the reusable test workflow.
- `.github/workflows/push-develop.yml`: Calls the `test.yml` workflow for the `develop` branch.
- `.github/workflows/push-main.yml`: Calls the `test.yml` workflow for the `main` branch and handles deployment.

## Future Plans
As the Flashcard Generator Core continues to evolve, several exciting enhancements and new features are planned to improve its functionality and expand its capabilities.

- Functionality for applying the options to flashcard generation (stress, tenses, genders).
- Output service for directly generating output compatible with the Anki spaced-repetition flashcard application.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any improvements or new features.

The repository uses the Gitflow branching model.

## License
This project is licensed under the MIT License.

