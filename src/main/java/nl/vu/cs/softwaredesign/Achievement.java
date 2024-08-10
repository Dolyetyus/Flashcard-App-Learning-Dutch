package nl.vu.cs.softwaredesign;

import javafx.scene.image.Image;

public class Achievement implements Observer{
    private String name;
    private String description;
    private Image badge;
    private boolean obtained;
    private int displayedCounter;

    public Achievement(String name, String description, Image badge, boolean obtained, int displayedCounter) {
        this.name = name;
        this.description = description;
        this.badge = badge;
        this.obtained = obtained;
        this.displayedCounter = displayedCounter;
    }
    public void update(int totalScore, int streak) {
        Customiser customiser = Customiser.getInstance();
        checkAchievementCriteria(totalScore, streak, customiser);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Image getBadge() {
        return badge;
    }

    public boolean isObtained() {
        return obtained;
    }

    public void setObtained() {
        this.obtained = true;
    }

    public void resetObtained(){
        this.obtained = false;
    }

    public int getDisplayedCounter(){
        return displayedCounter;
    }
    public void incrementDisplayedCounter(){
        displayedCounter++;
    }

    public void checkAchievementCriteria(int totalScore, int streak, Customiser customiser) {
        boolean criteriaMet = false;

        // Check the criteria based on the achievement's name
        switch (name) {
            case "High Score":
                criteriaMet = totalScore >= 100;
                break;
            case "Streak":
                criteriaMet = streak >= 5;
                break;
            case "Create Level":
                criteriaMet = !customiser.checkCustomLevelsExist();
                break;
            default:
                break;
        }

        if (criteriaMet && !isObtained()) {
            setObtained();
            displayedCounter++;
        }
    }
}
