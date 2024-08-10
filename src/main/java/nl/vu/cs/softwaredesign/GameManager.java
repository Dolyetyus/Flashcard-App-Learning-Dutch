package nl.vu.cs.softwaredesign;

import nl.vu.cs.softwaredesign.Flashcards.Flashcard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameManager implements Subject {  //game manager should be a singleton
    private static GameManager instance;
    private ArrayList<Level> levels = new ArrayList<>();
    private int currentScore = 0;
    private int currentStreak = 0;
    private int multiplier = 1;
    private User user = User.getInstance();
    private List<Observer> observers = new ArrayList<>();

    public static GameManager getInstance() {  //Singleton?
        if (instance == null) {
            instance = new GameManager();
            instance.registerUserAchievements();
        }
        return instance;
    }
    @Override public void registerObserver(Observer o) {
        observers.add(o);
    }

    @Override public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override public void notifyObservers() {
        for (Observer observer: observers) {
            observer.update(currentScore, currentStreak);
        }
    }

    private void registerUserAchievements() {
        for (Achievement achievement: user.getAchievements()) {
            registerObserver(achievement);
        }
    }

    public int getCurrentStreak(){
        return currentStreak;
    }

    public List<Level> getLevels(){
        return levels;
    }

    public int getCurrentScore(){
        return currentScore;
    }

    public void addLevel(Level newLevel) {
        levels.add(newLevel);
    }

    public void removeLevel(int levelID) {
        Level levelToRemove = getLevelById(levelID);
        if (levelToRemove != null) {
            levels.remove(levelToRemove);
        }
    }

    public void startLevel(int levelID) {
        Level levelToStart = getLevelById(levelID);
        if (levelToStart != null) {
            levelToStart.prepareLevel();
        }
    }

    public Level getLevelById(int levelID) {
        for (Level level : levels) {
            if (level.getLevelId() == levelID) {
                return level;
            }
        }
        return null;
    }

    //Actually we can just leave this feature, won't make much sense and complicated
    public boolean checkScoreReqForLevel(int levelID, int score) {
        Level level = getLevelById(levelID);
        return level != null && score >= level.getLevelScore();
    }

    public void calculateScore(int levelID, boolean answer) {
        Level level = getLevelById(levelID);
        assert level != null;
        if (answer) {
            currentScore += 10 * multiplier;
            multiplier += 1;
            currentStreak += 1;
        }
        else {
            multiplier = 1;
            currentStreak=0;
        }
        notifyObservers();
    }

    public void resetScoreAttributes(){
        currentScore = 0;
        multiplier = 1;
        currentStreak = 0;
    }

    public void updateUserAndLevelScores(int levelId){
        Level level = getLevelById(levelId);
        assert level != null;
        int levelScore = level.getLevelScore();

        int userScore = user.getTotalScore();
        int scoreDifference = currentScore - levelScore;
        if (scoreDifference > 0) {
            user.setTotalScore(userScore + scoreDifference);
            level.updateLevelScore(currentScore);
        }
    }
}