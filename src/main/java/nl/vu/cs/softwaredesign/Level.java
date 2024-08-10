package nl.vu.cs.softwaredesign;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.BasicExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.CompletionExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.FunExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.ListeningExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Custom.CustomFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Flashcard;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class Level {
    private GameManager gameManager = GameManager.getInstance();
    private GUI gui = GUI.getInstance();
    private String levelName;
    private int levelId;
    private List<Flashcard> flashcards;
    private LinkedList<Flashcard> flashcardQueue;
    private int levelScore;
    private int difficulty;

    public Level(String levelName, int levelId, int difficulty) {
        this.levelName = levelName;       // I think we will use this constructor
        this.levelId = levelId;           // mostly for the custom levels
        this.difficulty = difficulty;
        this.flashcards = new LinkedList<>();
        this.flashcardQueue = new LinkedList<>();
        this.levelScore = 0;
    }

    // Getter functions to display on GUI etc
    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String name) {
        this.levelName = name;
    }

    public int getLevelId() {
        return levelId;
    }

    public int getIndexOf(Flashcard fcard) {
        for (int i = 0; i < flashcards.size(); ++i)
            if (fcard.getPhrase().equals(flashcards.get(i).getPhrase()))
                return i;
        return -1;
    }

    public Flashcard getFcardByPhrase(String phrase) {
        for (Flashcard fcard : flashcards)
            if (fcard.getPhrase().equals(phrase))
                return fcard;
        return null;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void addFlashcard(Flashcard flashcard) {
        this.flashcards.add(flashcard);
        flashcard.setLevelID(levelId);
        //flashcard.setLevelID(this.levelId);
    }

    public void deleteFlashcard(Flashcard flashcard) {
        this.flashcards.remove(flashcard);
        flashcard.setLevelID(-1);
    }

    public void assignFlashcards(List<Flashcard> initFlashcards) {
        this.flashcards = initFlashcards;
    }

    public int getLevelScore(){
        return levelScore;
    }

    public void updateLevelScore(int scoreAmount) {
        if (scoreAmount > levelScore) {
            levelScore = scoreAmount;
        }
    }

    public void prepareLevel() {
        flashcardQueue.addAll(flashcards);
        randomizeOrder();
        playLevel();
    }

    private void playLevel() {
        if (!checkForEnd()){
            Flashcard currentFlashcard = flashcardQueue.peek();
            if (currentFlashcard != null) {
                currentFlashcard.display();
            }
        }
        else{
            levelFinished();
        }
    }

    private void randomizeOrder(){
        Collections.shuffle(flashcardQueue);
    }

    public boolean checkForEnd() {
        return flashcardQueue.isEmpty();
    }

    public boolean answerQuestion(String answer) {
        Flashcard currFCard = flashcardQueue.peek();
        boolean isCorrect = currFCard.checkAnswer(answer);

        if (!(currFCard instanceof FunExerciseFlashcard)) {
            Media audio = new Media(new File(isCorrect ? "src/main/resources/correct.mp3" : "src/main/resources/wrong.mp3").toURI().toString());
            MediaPlayer audioPlayer = new MediaPlayer(audio);
            audioPlayer.play();

            if (isCorrect) {
                if (currFCard instanceof BasicExerciseFlashcard)
                    gui.displayBasicExerciseFCard((BasicExerciseFlashcard) currFCard, 1);
                else if (currFCard instanceof ListeningExerciseFlashcard)
                    gui.displayListeningExercise((ListeningExerciseFlashcard) currFCard, 1);
                else if(currFCard instanceof CompletionExerciseFlashcard)
                    gui.displayCompletionExerciseFCard((CompletionExerciseFlashcard) currFCard, 1);
                else if(currFCard instanceof CustomFlashcard)
                    gui.displayCustomFCard((CustomFlashcard) currFCard, 1);
            }
            else {
                if (currFCard instanceof BasicExerciseFlashcard)
                    gui.displayBasicExerciseFCard((BasicExerciseFlashcard) currFCard, -1);
                else if (currFCard instanceof ListeningExerciseFlashcard)
                    gui.displayListeningExercise((ListeningExerciseFlashcard) currFCard, -1);
                else if(currFCard instanceof CompletionExerciseFlashcard)
                    gui.displayCompletionExerciseFCard((CompletionExerciseFlashcard) currFCard, -1);
                else if(currFCard instanceof CustomFlashcard)
                    gui.displayCustomFCard((CustomFlashcard) currFCard, -1);
            }
        }

        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> {
            if (isCorrect) {
                flashcardQueue.removeFirst();
                gameManager.calculateScore(levelId, true);
                if (flashcardQueue.peek() == null) {
                    gui.displayLevelsMenu();
                }
            }
            else {
                flashcardQueue.addLast(flashcardQueue.removeFirst());
                gameManager.calculateScore(levelId, false);
            }

            playLevel();
        });
        delay.play();

        return isCorrect;
    }

    public void levelFinished() {
        gameManager.updateUserAndLevelScores(levelId);
        gameManager.resetScoreAttributes();
    }
}
