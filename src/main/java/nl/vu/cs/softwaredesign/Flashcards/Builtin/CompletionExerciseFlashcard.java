package nl.vu.cs.softwaredesign.Flashcards.Builtin;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.vu.cs.softwaredesign.GUI;

import java.util.*;
import java.util.stream.Collectors;

public class CompletionExerciseFlashcard extends BuiltinFlashcard {
    private final List<String> missingWords;
    Random random = new Random();
    private GUI gui;
    private List<String> words;
    private boolean correct = false;

    public CompletionExerciseFlashcard(@JsonProperty("phrase") String phrase,
                                       @JsonProperty("translation") String translation,
                                       @JsonProperty("hintPath") String hintPath,
                                       @JsonProperty("audioPath") String audioPath) {
        this.phrase = phrase;
        this.translation = translation;
        this.hintPath = hintPath;
        this.audioPath = audioPath;
        this.missingWords = new ArrayList<>();
        gui = GUI.getInstance();
        this.prepareQuestion();
    }

    @Override
    public void display() {
        gui.displayCompletionExerciseFCard(this, 0);
        playAudio(1);
    }

    public boolean checkAnswer(String answers) {
        List<String> userAnswers = Arrays.stream(answers.split("\\s+"))
                .map(String::trim)
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());

        List<String> normalizedMissingWords = missingWords.stream()
                .map(String::toLowerCase)
                .sorted()
                .collect(Collectors.toList());

        correct = userAnswers.equals(normalizedMissingWords);
        return correct;
    }

    private void prepareQuestion(){
        words = new ArrayList<>(Arrays.asList(phrase.split("\\s+")));
        missingWords.clear();

        int wordsToRemove = random.nextInt(2) + 2;

        Set<Integer> removedIndices = new HashSet<>();
        while (removedIndices.size() < wordsToRemove) {
            int indexToRemove = random.nextInt(words.size());
            if (!removedIndices.contains(indexToRemove)) {
                missingWords.add(words.get(indexToRemove));
                words.set(indexToRemove, "____");
                removedIndices.add(indexToRemove);
            }
        }
    }

    @Override
    public String getPhrase() {
        if (!correct){
            return String.join(" ", words);
        }
        else {
            return phrase;
        }
    }
}
