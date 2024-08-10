package nl.vu.cs.softwaredesign;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.*;
import nl.vu.cs.softwaredesign.Flashcards.Flashcard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    private User user = User.getInstance();
    private Customiser customiser = Customiser.getInstance();
    private GameManager gameManager = GameManager.getInstance();
    FlashcardLoader loader = new FlashcardLoader();

    public static void main(String[] args) {
        launch(args);
    }

    private void guiInit(Stage primaryStage){
        GUI gui = GUI.getInstance();
        Stage currStage = gui.launchGUI(primaryStage);
        currStage.show();
    }

    private void levelInit() throws IOException {
        Level basicLevel = new Level("Basic", 100, 1);
        gameManager.addLevel(basicLevel);
        List<BasicExerciseFlashcard> importedBasic = (List<BasicExerciseFlashcard>) loader.load("Basic");
        List<Flashcard> flashcardListBasic = new ArrayList<>(importedBasic.size());
        flashcardListBasic.addAll(importedBasic);
        basicLevel.assignFlashcards(flashcardListBasic);

        Level listeningLevel = new Level("Listening", 200, 2);
        gameManager.addLevel(listeningLevel);
        List<ListeningExerciseFlashcard> importedListening = (List<ListeningExerciseFlashcard>) loader.load("Listening");
        List<Flashcard> flashcardListListening = new ArrayList<>(importedListening.size());
        flashcardListListening.addAll(importedListening);
        listeningLevel.assignFlashcards(flashcardListListening);

        Level completionLevel = new Level("Completion", 300, 3);
        gameManager.addLevel(completionLevel);
        List<CompletionExerciseFlashcard> importedCompletion = (List<CompletionExerciseFlashcard>)loader.load("Completion");
        List<Flashcard> flashcardListCompletion = new ArrayList<>(importedCompletion.size());
        flashcardListCompletion.addAll(importedCompletion);
        completionLevel.assignFlashcards(flashcardListCompletion);

        Level funLevel = new Level("Fun Mode", 400, 1);
        gameManager.addLevel(funLevel);
        List<FunExerciseFlashcard> importedFun = (List<FunExerciseFlashcard>) loader.load("Fun");
        List<Flashcard> flashcardListFun = new ArrayList<>(importedFun.size());
        flashcardListFun.addAll(importedFun);
        funLevel.assignFlashcards(flashcardListFun);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        levelInit();
        guiInit(primaryStage);
    }
}