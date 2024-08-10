package nl.vu.cs.softwaredesign.Flashcards;

import nl.vu.cs.softwaredesign.GUI;

public abstract class Flashcard {
    protected String phrase;
    protected String translation;
    protected Object hint;
    private int levelID;

    public String getPhrase() {
        return phrase;
    }

    public String getTranslation(){
        return translation;
    }

    public int getLevelID() {
        return levelID;
    }

    public void setLevelID(int levelID) {
        this.levelID = levelID;
    }

    public abstract boolean checkAnswer(String answer);

    public void display() {
        //System.out.println("I am indeed used");
        //GUI.getInstance().displayFCard(this, 100, 0);

    }
}

