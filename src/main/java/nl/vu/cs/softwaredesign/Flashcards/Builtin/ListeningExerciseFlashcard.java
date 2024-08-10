package nl.vu.cs.softwaredesign.Flashcards.Builtin;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.vu.cs.softwaredesign.GUI;

public class ListeningExerciseFlashcard extends BuiltinFlashcard {
    private GUI gui;
    public ListeningExerciseFlashcard(@JsonProperty("phrase") String phrase,
                                      @JsonProperty("translation") String translation,
                                      @JsonProperty("hintPath") String hintPath,
                                      @JsonProperty("audioPath") String audioPath) {
        this.phrase = phrase;
        this.translation = translation;
        this.hintPath = hintPath;
        this.audioPath = audioPath;
        gui = GUI.getInstance();
    }

    public boolean checkAnswer(String answer) {
        return (this.translation.equalsIgnoreCase(answer) || this.phrase.equalsIgnoreCase(answer));
    }

    @Override
    public void display() {
        gui.displayListeningExercise(this, 0);
        playAudio(1);
    }
}
