package nl.vu.cs.softwaredesign.Flashcards.Custom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.vu.cs.softwaredesign.Flashcards.Flashcard;
import nl.vu.cs.softwaredesign.GUI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFlashcard extends Flashcard {
    public String hint;
    public int flashcardID;
    private GUI gui;

    public CustomFlashcard(@JsonProperty("phrase") String phrase,
                           @JsonProperty("translation") String translation,
                           @JsonProperty("hint") String hint,
                           @JsonProperty("flashcardID") int ID) {
        this.phrase = phrase;
        this.translation = translation;
        this.hint = hint;
        this.flashcardID = ID;
        gui = GUI.getInstance();
    }

    @Override
    public void display() {
            gui.displayCustomFCard(this, 0);
    }

    public void updateFlashcard(String phrase, String translation, String hint) {
        setPhrase(phrase);
        setTranslation(translation);
        setHint(hint);
    }

    public boolean checkAnswer(String answer) {
        return this.translation.equalsIgnoreCase(answer);
    }

    private void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    private void setTranslation(String translation) {
        this.translation = translation;
    }

    private void setHint(String hint) {
        this.hint = hint;
    }
    public String getHint() {
        return hint;
    }

    public int getID() {
        return flashcardID;
    }
}
