package nl.vu.cs.softwaredesign;

import javafx.scene.image.Image;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class User {
    private static User instance = null; // Singleton instance
    private int totalScore = 0;
    private Set<Achievement> achievements = new HashSet<>();

    private User() {
        initializeAchievements(); // Initialize achievements when the User instance is created
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int scoreToAdd) {
        this.totalScore = scoreToAdd;
    }

    public void resetTotalScore(){
        totalScore = 0;
    }

    public Set<Achievement> getAchievements() {
        return achievements;
    }

    private void initializeAchievements() {
        Achievement highScoreAchievement = new Achievement("High Score", "Achieve a total score of 100 or higher", new Image("score.png"), false, 0);
        Achievement streakAchievement = new Achievement("Streak", "Maintain a streak of 5 or higher", new Image("streak.png"), false, 0);
        Achievement createLevelAchievement = new Achievement("Create Level", "Create your first custom level", new Image("create.png"), false, 0);

        achievements.addAll(Arrays.asList(highScoreAchievement, streakAchievement, createLevelAchievement));
    }

    /*public void notify(String str){
    //switch:

        case "custom level created and added":
            achievements.get(first custom level).completed = true;

     */
}
