package nl.vu.cs.softwaredesign.Flashcards.Builtin;

import nl.vu.cs.softwaredesign.Flashcards.Builtin.Data.Database;
import nl.vu.cs.softwaredesign.Flashcards.Custom.CustomFlashcard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import nl.vu.cs.softwaredesign.Flashcards.Flashcard;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

// Go down to see how to use this class
public class FlashcardLoader {
    private ObjectMapper objectMapper = new ObjectMapper();
    int saveCounter = 1;

    public List<? extends Flashcard> load(String type) throws IOException{
        String json;
        switch (type) {
            case "Basic":
                json = Database.basicJSON;
                return objectMapper.readValue(json, new TypeReference<List<BasicExerciseFlashcard>>() {
                });
            case "Completion":
                json = Database.completionJSON;
                return objectMapper.readValue(json, new TypeReference<List<CompletionExerciseFlashcard>>() {
                });
            case "Listening":
                json = Database.listeningJSON;
                return objectMapper.readValue(json, new TypeReference<List<ListeningExerciseFlashcard>>() {});
            case "Fun":
                json = Database.funJSON;
                return objectMapper.readValue(json, new TypeReference<List<FunExerciseFlashcard>>() {});
            default:
                throw new IllegalArgumentException("Invalid flashcard type: " + type);
        }
    }

    public List<CustomFlashcard> loadLevel(String filePath) throws IOException {
        File file = new File(filePath);
        return objectMapper.readValue(file, new TypeReference<>() {
        });
    }

    public void saveLevel(List<CustomFlashcard> flashcards) throws IOException {
        String fileName = String.format("Exports/save%02d.json", saveCounter);
        String filePath = System.getProperty("user.dir") + System.getProperty("file.separator") + fileName;
        File file = new File(filePath);

        boolean fileCreated = file.createNewFile();
        if (!fileCreated) {
            System.out.println("File already exists: " + filePath);
            System.out.println("The flashcards in the file will be overwritten");
        }

        saveCounter++;
        objectMapper.writeValue(file, flashcards);
    }
}

/*
            HOW TO USE:
            -In the main game function, to initiliase the flashcards, first initiliase a level
            -Then call loadBasic, loadListening etc. to populate a list of flashcards:
            FlashcardLoader loader = new FlashcardLoader();
            List<BasicExerciseFlashcard> createdFlashcards = loader.loadBasic();
            level.assignFlashcards(createdFlashcards);

            Then the level has the imported flashcard objects in its flashcards list

            FLASHCARD SAVER HOW TO USE:
            List<CustomFlashcard> flashcardsToSave = returnFlashcards(levelID); list of custom flashcards
            loader.saveLevel(flashcardsToSave);
            This will save the flashcards inside a level

            FLASHCARD LOADER HOW TO USE:
            -Init a new custom level to place the flashcards, then:
            List<CustomFlashcard> loadedFlashcards = loader.load(filepath); ("Export/savexx.json")
            newLevel.assignFlashcards(loadedFlashcards);
 */
