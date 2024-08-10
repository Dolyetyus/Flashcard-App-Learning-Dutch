package nl.vu.cs.softwaredesign.Flashcards.Builtin;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.vu.cs.softwaredesign.GUI;

public class FunExerciseFlashcard extends BuiltinFlashcard {
    private GUI gui;
    public FunExerciseFlashcard(@JsonProperty("phrase") String phrase,
                                @JsonProperty("translation") String translation,
                                @JsonProperty("hintPath") String hintPath,
                                @JsonProperty("audioPath") String audioPath) {
        this.phrase = phrase;
        this.translation = translation;
        this.hintPath = hintPath;
        this.audioPath = audioPath;
        gui = GUI.getInstance();
    }

    @Override
    public void display() {
        gui.displayFunExercise(this);
        playAudio(1);
    }

    public boolean checkAnswer(String answer) {
        return false;
    }

    @Override
    public String getTranslation(){
        return translation;
    }
}
