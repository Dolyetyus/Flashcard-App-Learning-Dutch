package nl.vu.cs.softwaredesign.Flashcards.Builtin;

import nl.vu.cs.softwaredesign.GUI;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicExerciseFlashcard extends BuiltinFlashcard {
    private GUI gui;
    public BasicExerciseFlashcard(@JsonProperty("phrase") String phrase,
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
        gui.displayBasicExerciseFCard(this, 0);
        super.playAudio(1);
    }

    public boolean checkAnswer(String answer) {
        return this.translation.equalsIgnoreCase(answer);
    }
}
