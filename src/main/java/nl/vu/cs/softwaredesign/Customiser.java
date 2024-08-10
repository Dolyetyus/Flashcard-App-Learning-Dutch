package nl.vu.cs.softwaredesign;

import nl.vu.cs.softwaredesign.Flashcards.Builtin.FlashcardLoader;
import nl.vu.cs.softwaredesign.Flashcards.Custom.CustomFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Flashcard;
import nl.vu.cs.softwaredesign.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Customiser {
    private GameManager gameManager = GameManager.getInstance();
    private List<Level> customLevels;
    FlashcardLoader loader = new FlashcardLoader();
    private int lastAssignedLevelID = 0;
    private int lastAssignedCardID = 0;

    //private User user = User.getInstance();

    private static Customiser instance = null;
    public static Customiser getInstance() {
        if (instance == null) {
            instance = new Customiser();
        }
        return instance;
    }

    public Customiser() {
        this.customLevels = new ArrayList<>();
    }

    public void createLevel(String name, int difficulty) {
        int levelId = generateCustomLevelId();
        Level newLevel = new Level(name, levelId, difficulty);
        customLevels.add(newLevel);
        checkCustomLevelsExist();
        gameManager.addLevel(newLevel);
        //user.notify("custom level created and added);
    }
    
    public void setLevelName(int levelID, String name) {
        Level level = getLevelById(levelID);
        if (level != null)
            level.setLevelName(name);
    }

    public void deleteLevel(int levelId) {
        Level levelToDelete = getLevelById(levelId);
        if (levelToDelete != null) {
            customLevels.remove(levelToDelete);
            gameManager.removeLevel(levelId);
        }
    }

    public Level getLevelById(int levelID) {
        for (Level level : customLevels) {
            if (level.getLevelId() == levelID) {
                return level;
            }
        }
        return null;
    }

    public Level getLevelByName(String name) {
        for (Level level : customLevels) {
            if (level.getLevelName().equals(name)) {
                return level;
            }
        }
        return null;
    }

    public List<Level> getCustomLevels() {
        return customLevels;
    }

    private int generateCustomLevelId() {
        // ID is unique and follows the custom level ID pattern (e.g. "9xx")
        lastAssignedLevelID++;
        return 900 + lastAssignedLevelID;
    }

    public void createFlashcard(int levelId, String phrase, String translation, String hint) {
        Level level = getLevelById(levelId);
        if (level != null) {
            lastAssignedCardID++;
            CustomFlashcard flashcard = new CustomFlashcard(phrase, translation, hint, lastAssignedCardID);
            level.addFlashcard(flashcard);
        }
    }
    
    public void updateFlashcard(int levelId, int flashcardId, String phrase, String translation, String hint) {
        Level level = getLevelById(levelId);
        if (level != null) {
            level.getFlashcards().stream()
                    .filter(flashcard -> flashcard instanceof CustomFlashcard)
                    .map(flashcard -> (CustomFlashcard) flashcard)
                    .filter(customFlashcard -> customFlashcard.getID() == flashcardId)
                    .findFirst()
                    .ifPresent(customFlashcard -> {
                        customFlashcard.updateFlashcard(phrase, translation, hint);
                    });
        }
    }

    public void deleteFlashcard(int levelId, int flashcardId) {
        Level level = getLevelById(levelId);
        if (level != null) {
            level.getFlashcards().removeIf(flashcard ->
                    flashcard instanceof CustomFlashcard &&
                            ((CustomFlashcard) flashcard).getID() == flashcardId);
        }
    }

    public void importLevel(String path, String name, int difficulty) throws IOException {
        int levelId = generateCustomLevelId();
        Level newLevel = new Level(name, levelId, difficulty);
        customLevels.add(newLevel);
        gameManager.addLevel(newLevel);

        List<CustomFlashcard> createdFlashcards = loader.loadLevel(path);
        List<Flashcard> flashcards = new ArrayList<>(createdFlashcards.size());
        flashcards.addAll(createdFlashcards);
        newLevel.assignFlashcards(flashcards);
        loader.loadLevel(path);
    }

    public void exportLevel(int levelId) throws IOException {
        Level level = getLevelById(levelId);
        assert level != null;
        List<Flashcard> flashcards = level.getFlashcards();

        List<CustomFlashcard> customFlashcards = new ArrayList<>();
        for (Flashcard flashcard : flashcards) {
            if (flashcard instanceof CustomFlashcard) {
                customFlashcards.add((CustomFlashcard) flashcard);
            }
        }

        loader.saveLevel(customFlashcards);
    }

    public boolean checkCustomLevelsExist(){
        return customLevels.isEmpty();
    }
}
