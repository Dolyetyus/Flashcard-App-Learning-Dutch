package nl.vu.cs.softwaredesign.Flashcards.Builtin;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import nl.vu.cs.softwaredesign.Flashcards.Flashcard;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;
import nl.vu.cs.softwaredesign.GUI;

import java.io.File;

public abstract class BuiltinFlashcard extends Flashcard {
    protected String hintPath;
    protected String audioPath;

    public String getHintPath() {
        return hintPath;
    }

    public void displayHint() {
        GUI.getInstance().displayImgHint(hintPath);
    }

    public void playAudio(double rate) {
        Media sound = new Media(new File(audioPath).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setRate(rate);
        mediaPlayer.play();
    }
}
