package nl.vu.cs.softwaredesign;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.stage.Popup;
import javafx.util.Duration;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.BasicExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.CompletionExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.FunExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Builtin.ListeningExerciseFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Custom.CustomFlashcard;
import nl.vu.cs.softwaredesign.Flashcards.Flashcard;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class GUI{
    private static class GUIBtn {
        private static double btnMinWidth = 200;
        private static double btnMinHeight = 80;
        private static String btnStyle = "-fx-background-radius: 20; -fx-font-size: 18px;";
    
        private static Button createGUIBtn(String label) {
            Button btn = new Button(label);
            btn.setMinSize(btnMinWidth, btnMinHeight);
            btn.setStyle(btnStyle);
            return btn;
        }
        private static Button createEMGUIBtn(String label) {
            Button btn = new Button(label);
            btn.setMinSize(btnMinWidth * 0.75, btnMinHeight * 0.75);
            btn.setStyle("-fx-background-radius: 20; -fx-font-size: 13.5px;");
            return btn;
        }

        private static Button createAudioBtn(String label) {
            Button btn = new Button(label);
            btn.setMinSize(btnMinWidth * 0.5, btnMinHeight * 0.5);
            btn.setStyle("-fx-background-radius: 20; -fx-font-size: 13.5px;");
            return btn;
        }
    }

    private enum Mode {VIEW, REMOVE, EDIT, ADD, SAVE}
    private Scene mainMenu = getMainMenu();
    private Customiser customiser = Customiser.getInstance();
    private User user = User.getInstance();
    private Stage currentStage = null;
    private GameManager gameManager = GameManager.getInstance();
    private static GUI instance = null;

    private VBox createVBox() {
        VBox vBox = new VBox(20);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-color: #ccc39e;");
        return vBox;
    }
    
    private HBox createHBox() {
        HBox hBox = new HBox(20);
        hBox.setAlignment(Pos.CENTER);
        hBox.setStyle("-fx-background-color: #ccc39e;");
        return hBox;
    }
    private Text createText(String label, int fontSize) {
        Text title = new Text(label);
        title.setFont(new Font(fontSize));
        return title;
    }

    private Scene getLevelsMenu() {
        VBox root = createVBox();

        Text title = createText("Levels", 70);
        root.getChildren().add(title);

        List<Level> levelList = gameManager.getLevels();

        for (Level level: levelList) {
            Button btn = GUIBtn.createGUIBtn(level.getLevelName());
            btn.addEventHandler(ActionEvent.ACTION, event -> {
                gameManager.startLevel(level.getLevelId());
            });
            root.getChildren().add(btn);
        }

        Button backToMainMenuBtn = GUIBtn.createGUIBtn("Back to main menu");
        backToMainMenuBtn.addEventHandler(ActionEvent.ACTION, event -> {currentStage.setScene(getMainMenu());
            handleAchievementNotification(user.getTotalScore(), gameManager.getCurrentStreak(), user, customiser);

                                                                        });
        root.getChildren().add(backToMainMenuBtn);

        Scene levelsMenu = new Scene(root, 800, 750);
        return levelsMenu;
    }

    private String getBtnLabel(Flashcard fcard) {
        String[] words = fcard.getPhrase().split(" ", 2);
        return words[0];
    }

    private Scene getFcardMenuEM(String inputPhrase, String inputTranslation,
                                 String inputHint, int fcardIdx, int levelID, Mode mode, int feedback) {

        Level level = customiser.getLevelById(levelID);
        CustomFlashcard fcard = null;
        if (mode == Mode.EDIT)
            fcard = (CustomFlashcard) level.getFlashcards().get(fcardIdx);

        final CustomFlashcard cpyFcard = fcard;

        VBox root = createVBox();

        String titleLabel;
        if (mode == Mode.EDIT)
            titleLabel = "Edit flashcard";
        else
            titleLabel = "Add flashcard";
        Text title = createText(titleLabel, 70);

        Text phraseLabel = createText("Phrase", 22);
        TextField phraseField;
        if (mode == Mode.EDIT || feedback < 1)
            phraseField = new TextField(inputPhrase);
        else
            phraseField = new TextField();
        phraseField.setMaxWidth(200);

        Text translationLabel = createText("Translation", 22);
        TextField translationField;
        if (mode == Mode.EDIT || feedback < 1)
            translationField = new TextField(inputTranslation);
        else
            translationField = new TextField();
        translationField.setMaxWidth(200);

        Text hintLabel = createText("Hint", 22);
        TextField hintField;
        if (mode == Mode.EDIT || feedback < 1)
            hintField = new TextField(inputHint);
        else
            hintField = new TextField();
        hintField.setMaxWidth(200);

        Text feedbackText = createText("", 22);
        if (feedback < 1) {
            feedbackText.setFill(Color.RED);
            if (feedback == 0)
                feedbackText.setText("Phrase already exists");
            else
                feedbackText.setText("Incomplete input");
        }

        String submitBtnLabel;
        if (mode == Mode.EDIT)
            submitBtnLabel = "Apply";
        else
            submitBtnLabel = "Create";

        Button submitBtn = GUIBtn.createEMGUIBtn(submitBtnLabel);
        submitBtn.setOnAction(event -> {
            String currPhrase = phraseField.getText().trim();
            String currTranslation = translationField.getText().trim();
            String currHint = hintField.getText().trim();


            if (currPhrase.isEmpty() || currTranslation.isEmpty() || currHint.isEmpty()) {
                currentStage.setScene(getFcardMenuEM(currPhrase, currTranslation, currHint, fcardIdx, levelID, mode, -1));
                return;
            }

            CustomFlashcard maybeFcard = (CustomFlashcard) level.getFcardByPhrase(currPhrase);
            if (mode == Mode.ADD && maybeFcard != null) {// && maybeFcard.getPhrase() != currPhrase)
                currentStage.setScene(getFcardMenuEM(currPhrase, currTranslation, currHint, fcardIdx, levelID, mode, 0));
                return;
            }

            CustomFlashcard newFcard = new CustomFlashcard(currPhrase, currTranslation, currHint, -1);
            if (mode == Mode.EDIT)
                level.deleteFlashcard(cpyFcard);
            level.addFlashcard(newFcard);

            currentStage.setScene(getFcardMenuVM(level.getIndexOf(newFcard), levelID));
        });

        Button cancelBtn = GUIBtn.createEMGUIBtn("Cancel");
        if (mode == Mode.EDIT)
            cancelBtn.setOnAction(event -> currentStage.setScene(getFcardMenuVM(fcardIdx, levelID)));
        else
            cancelBtn.setOnAction(event -> currentStage.setScene(getCustomLevelMenu(levelID, Mode.VIEW)));

        root.getChildren().addAll(title, phraseLabel, phraseField, translationLabel, translationField, hintLabel,
                hintField, feedbackText, submitBtn, cancelBtn);
        Scene fcardMenuEM = new Scene(root, 800, 750);
        return fcardMenuEM;
    }

    private Scene getFcardMenuVM(int fcardIdx, int levelID) {
        Level level = customiser.getLevelById(levelID);
        CustomFlashcard fcard = (CustomFlashcard) level.getFlashcards().get(fcardIdx);


        VBox root = createVBox();
        Text title = createText("View flashcard", 70);

        Text phraseLabel = createText("Phrase", 22);
        phraseLabel.setFill(Color.WHITE);

        Text phrase = createText(fcard.getPhrase(), 22);

        Text translationLabel = createText("Translation", 22);
        translationLabel.setFill(Color.WHITE);

        Text translation = createText(fcard.getTranslation(), 22);

        Text hintLabel = createText("Hint", 22);
        hintLabel.setFill(Color.WHITE);

        Text hint = createText(fcard.getHint(), 22);

        Button editBtn = GUIBtn.createEMGUIBtn("Edit");
        editBtn.setOnAction(event -> currentStage.setScene(getFcardMenuEM(fcard.getPhrase(), fcard.getTranslation(),
                fcard.getHint(), fcardIdx, levelID, Mode.EDIT, 1)));

        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
        backToMenuBtn.setOnAction(event -> currentStage.setScene(getCustomLevelMenu(levelID, Mode.VIEW)));

        root.getChildren().addAll(title, phraseLabel, phrase, translationLabel, translation, hintLabel, hint, editBtn,
                backToMenuBtn);
        Scene fcardMenuVM = new Scene(root, 800, 750);
        return fcardMenuVM;
    }

    private Scene getCustomLevelMenu(int levelID, Mode mode) {

        VBox root = createVBox();

        Level level = customiser.getLevelById(levelID);
        Text title = createText("Flashcards of\n" + level.getLevelName(), 70);
        List<Flashcard> fcards = level.getFlashcards();

        GridPane fcardsGrid = new GridPane();
        int numAddedElems = 0;
        for (Flashcard fcard : fcards) {
            final int cpyNumAddedElems = numAddedElems;

            //    System.out.println("in custom fcard for-loop");
            Button btn = GUIBtn.createGUIBtn(getBtnLabel(fcard));
            //System.out.println("enters for loop");

            btn.setOnAction(event -> {
                //        System.out.println("custom fcard button clicked" + btn.getText());
                if (mode == Mode.REMOVE) {
                    level.deleteFlashcard(fcard);
                    currentStage.setScene(getCustomLevelMenu(levelID, mode));
                } else {
                    currentStage.setScene(getFcardMenuVM(cpyNumAddedElems, levelID));
                }
//               }else
//                    currentStage.setScene(removeEditNameCreateLevel(customLevel.getLevelId(), 0,
//                                                                                        customLevel.getLevelName()));
                //currentStage.setScene(//getLevelMenu(customLevel.getLevelId()));
                // gameManager.startLevel(customLevel.getLevelId());
            });

            GridPane.setRowIndex(btn, numAddedElems / 4);
            GridPane.setColumnIndex(btn, numAddedElems % 4);
            fcardsGrid.getChildren().add(btn);
            numAddedElems++;
        }

        HBox editOptions = createHBox();

        Button removeFcardBtn = GUIBtn.createEMGUIBtn("Remove flashcard");
        if (mode == Mode.REMOVE)
            removeFcardBtn.setStyle(removeFcardBtn.getStyle() + " -fx-background-color: #77A1AF;");
        removeFcardBtn.setOnAction(event -> {
            Mode newMode = (mode != Mode.REMOVE)? Mode.REMOVE : Mode.VIEW;
            currentStage.setScene(getCustomLevelMenu(levelID, newMode));
        });

//        Button editLevelNameBtn = GUIBtn.createEMGUIBtn("Edit level name");
//        if (mode == Mode.EDIT)
//            editLevelNameBtn.setStyle(editLevelNameBtn.getStyle() + " -fx-background-color: #77A1AF;");
//        editLevelNameBtn.setOnAction(event -> {
//            Mode newMode = (mode != Mode.EDIT)? Mode.EDIT: Mode.VIEW;
//            currentStage.setScene(getCustomLevelsMenu(newMode));
//        });

        Button addFcardBtn = GUIBtn.createEMGUIBtn("Add flashcard");
        addFcardBtn.setOnAction(event -> currentStage.setScene(getFcardMenuEM("", "", "",
                -1, levelID, Mode.ADD, 1)));

        editOptions.getChildren().addAll(removeFcardBtn, addFcardBtn);

        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
        backToMenuBtn.setOnAction(event -> currentStage.setScene(getCustomLevelsMenu(mode, 0)));
        root.getChildren().addAll(title, fcardsGrid, editOptions, backToMenuBtn);
        Scene customLevelMenu = new Scene(root, 800, 750);
        return customLevelMenu;
    }

    //    private Scene getEditLevelName(String oldName, String newName, int feedback)
//    {
//        VBox root = createVBox();
//        Text title = createText("Edit name of level", 70);
//
//        Text oldNameLabel = createText("Old name", 22);
//        TextField oldNameField = new TextField(oldName);
//
//        Text newNameLabel = createText("New name", 22);
//        TextField newNameField = new TextField(newName);
//
//        Text feedbackText = new Text();
//        feedbackText.setFont(new Font(22));
//        if (feedback < 1) {
//            feedbackText.setFill(Color.RED);
//            if (feedback == -1)
//                feedbackText.setText("Incomplete input");
//            else
//                feedbackText.setText("Level not found");
//        } else {
//            feedbackText.setFill(Color.GREEN);
//            feedbackText.setText("Applied");
//        }
//
//        Button applyBtn = GUIBtn.createGUIBtn("Apply");
//        applyBtn.setOnAction(event -> {
//            String oldName = oldNameField.getText();
//            String newName = newNameField.getText();
//            if (oldName.isEmpty() || newName.isEmpty())
//                currentStage.setScene(getEditLevelName(oldName, newName, -1));
//
//            Level level = customiser.getLevelByName(oldName);
//            if (level == null)
//                currentStage.setScene(getEditLevelName(oldName, newName, 0));
//            else
//                currentStage.setScene(getEditLevelName(oldName, newName, 1));
//        });
//
//        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
//        backToMenuBtn.setOnAction(event -> currentStage.setScene((getCustomLevelsMenu(true))));
//
//        root.getChildren().addAll(title, oldNameLabel, oldNameField, newNameLabel, newNameField, feedbackText, applyBtn,
//                                                                                                        backToMenuBtn);
//        Scene editLevelName = new Scene (root, 800, 700);
//        return editLevelName;
//    }

    private Scene getEditNameAddLevel(Mode mode, int levelID, int feedback) {
        VBox root = createVBox();

        Text title;
        if (mode == Mode.EDIT)
            title = createText("Edit level name", 70);
//        else if (mode == 0)
//            title = createText("Edit name of level\n" + customiser.getLevelById(levelID).getLevelName(),70);
        else
            title = createText("Create Level", 70);

        Text levelName = createText("Level name", 22);

        TextField levelNameField = new TextField();
        levelNameField.setMaxWidth(200);
        if (mode == Mode.EDIT || feedback == 0)
            levelNameField.setText(customiser.getLevelById(levelID).getLevelName());

        String btnLabel;
//        if (mode == -1)
//            btnString = "Remove";
        if (mode == Mode.EDIT)
            btnLabel = "Apply";
        else
            btnLabel = "Add";

        Text feedbackText = createText("", 22);
        if (feedback < 1) {
            feedbackText.setFill(Color.RED);
            if (feedback == 0)
                feedbackText.setText("Already exists");
            else
                feedbackText.setText("Incomplete input");
        }

        Button submitBtn = GUIBtn.createEMGUIBtn(btnLabel);
        submitBtn.setOnAction(event -> {
            String inputStr = levelNameField.getText().trim();
            if (inputStr.isEmpty()) {
                currentStage.setScene(getEditNameAddLevel(mode, levelID, -1));
                return;
            }

            Level maybeLevel = customiser.getLevelByName(inputStr);
            if (maybeLevel != null)/*&& maybeLevel.getLevelName() != inputStr)*/ {
                currentStage.setScene(getEditNameAddLevel(mode, maybeLevel.getLevelId(), 0));
                return;
            }

            if (mode == Mode.EDIT)
                customiser.setLevelName(levelID, inputStr);
            else
                customiser.createLevel(inputStr, -1); //FIXME: change difficulty if needed

            currentStage.setScene(getCustomLevelsMenu(Mode.VIEW, 0));
        });

        //                customiser.deleteLevel(levelID);
//            else if (mode == 0)
//            else
//                customiser.createCustomLevel(levelNameField.getText(), -1);

        Button cancelBtn = GUIBtn.createEMGUIBtn("Cancel");
        cancelBtn.setOnAction(event -> currentStage.setScene(getCustomLevelsMenu(mode, 0)));

        root.getChildren().addAll(title, levelName, levelNameField, feedbackText, submitBtn, cancelBtn);
        Scene editNameAddLevel = new Scene(root, 800, 750);
        return editNameAddLevel;
    }

    private Scene getImportLevelMenu(int feedback) {
        VBox root = createVBox();

        Text title = createText("Import level", 70);

        Text pathLabel = createText("Path to level", 22);
        TextField path = new TextField();
        path.setMaxWidth(200);

        Text levelNameLabel = createText("New name for level", 22);
        TextField levelName = new TextField();
        levelName.setMaxWidth(200);

        Text feedbackText = createText("", 22);
        if (feedback == -1) {
            feedbackText.setText("Incorrect path");
            feedbackText.setFill(Color.RED);
        } else if (feedback == 1) {
            feedbackText.setText("Imported");
            feedbackText.setFill(Color.GREEN);
        } else if (feedback == -2) {
            feedbackText.setText("Incomplete input");
            feedbackText.setFill(Color.RED);
        }

        Button importBtn = GUIBtn.createEMGUIBtn("Import");
        importBtn.setOnAction(event -> {
            String inputPath = path.getText();
            String inputLevelName = levelName.getText();

            if (inputPath.isEmpty() || inputLevelName.isEmpty()) {
                currentStage.setScene(getImportLevelMenu(-2));
                return;
            }

            try {
                customiser.importLevel(inputPath, inputLevelName, -1);
                currentStage.setScene(getImportLevelMenu(1));
            } catch (IOException e) {
                currentStage.setScene(getImportLevelMenu(-1));
            }
        });

        Button backToMenu = GUIBtn.createGUIBtn("Back to menu");
        backToMenu.setOnAction(event -> currentStage.setScene(getCustomLevelsMenu(Mode.VIEW, 0)));

        root.getChildren().addAll(title, pathLabel, path, levelNameLabel, levelName, feedbackText, importBtn,
                backToMenu);
        Scene importLevelMenu = new Scene(root, 800, 750);
        return importLevelMenu;
    }

    private Scene getCustomLevelsMenu(Mode mode, int saveFeedback) {
        VBox root = createVBox();

        Text title = createText("Custom levels", 70);

//        Text indication;
//        if (viewEditMode == true)
//            indication = createText("Tap on a level to edit its name.", 22);
//        else
//   .,,         indication = createText("", 22);

        //assert gameManager != null;
        //List<Level> levelList = new ArrayList<Level>();
        List<Level> customLevels = customiser.getCustomLevels();
        //if(gameManager.getLevels() != null)
        //levelList = gameManager.getLevels();

        GridPane levelsGrid = new GridPane();
        int numAddedElems = 0;
        for (Level customLevel : customLevels) {
            Button btn = GUIBtn.createGUIBtn(customLevel.getLevelName());

            btn.setOnAction(event -> {
                if (mode == Mode.REMOVE) {
                    customiser.deleteLevel(customLevel.getLevelId());
                    currentStage.setScene(getCustomLevelsMenu(mode, 0));
                } else if (mode == Mode.SAVE) {
                    try {
                        customiser.exportLevel(customLevel.getLevelId());
                        currentStage.setScene(getCustomLevelsMenu(mode, 1));
                    } catch (IOException e) {
                        currentStage.setScene(getCustomLevelsMenu(mode, -1));
                    }
                } else if (mode == Mode.EDIT) {
                    currentStage.setScene(getEditNameAddLevel(mode, customLevel.getLevelId(), 1));
                } else {
                    currentStage.setScene(getCustomLevelMenu(customLevel.getLevelId(), mode));
                }
//               }else
//                    currentStage.setScene(removeEditNameCreateLevel(customLevel.getLevelId(), 0,
//                                                                                        customLevel.getLevelName()));
                //currentStage.setScene(//getLevelMenu(customLevel.getLevelId()));
                // gameManager.startLevel(customLevel.getLevelId());
            });

            GridPane.setRowIndex(btn, numAddedElems / 4);
            GridPane.setColumnIndex(btn, numAddedElems % 4);
            levelsGrid.getChildren().add(btn);
            numAddedElems++;
        }

        Text saveFeedbackText = createText("",22);
        if (saveFeedback < 0) {
            saveFeedbackText.setText("Failed to save");
            saveFeedbackText.setFill(Color.RED);
        } else if (saveFeedback > 0) {
            saveFeedbackText.setText("Saved in Exports directory");
            saveFeedbackText.setFill(Color.GREEN);
        }

        HBox editOptions1 = createHBox();

        Button removeLevelBtn = GUIBtn.createEMGUIBtn("Remove level");
        if (mode == Mode.REMOVE)
            removeLevelBtn.setStyle(removeLevelBtn.getStyle() + " -fx-background-color: #77A1AF;");
        removeLevelBtn.setOnAction(event -> {
            Mode newMode = (mode != Mode.REMOVE)? Mode.REMOVE : Mode.VIEW;
            currentStage.setScene(getCustomLevelsMenu(newMode, 0));
        });

        Button editLevelNameBtn = GUIBtn.createEMGUIBtn("Edit level name");
        if (mode == Mode.EDIT)
            editLevelNameBtn.setStyle(editLevelNameBtn.getStyle() + " -fx-background-color: #77A1AF;");
        editLevelNameBtn.setOnAction(event -> {
            Mode newMode = (mode != Mode.EDIT)? Mode.EDIT: Mode.VIEW;
            currentStage.setScene(getCustomLevelsMenu(newMode, 0));
        });

        Button addLevelBtn = GUIBtn.createEMGUIBtn("Add level");
        addLevelBtn.setOnAction(event -> currentStage.setScene(getEditNameAddLevel(Mode.ADD, -1,
                1)));

        editOptions1.getChildren().addAll(removeLevelBtn, editLevelNameBtn, addLevelBtn);

        HBox editOptions2 = createHBox();

        Button saveLevelBtn = GUIBtn.createEMGUIBtn("Save level");
        if (mode == Mode.SAVE)
            saveLevelBtn.setStyle(saveLevelBtn.getStyle() + " -fx-background-color: #77A1AF;");
        saveLevelBtn.setOnAction(event -> {
            Mode newMode = (mode != Mode.SAVE)? Mode.SAVE : Mode.VIEW;
            currentStage.setScene(getCustomLevelsMenu(newMode, 0));
        });

        Button importLevelBtn = GUIBtn.createEMGUIBtn("Import level");
        importLevelBtn.setOnAction(event -> currentStage.setScene(getImportLevelMenu(0)));

        editOptions2.getChildren().addAll(saveLevelBtn, importLevelBtn);

        Button backToMainMenuBtn = GUIBtn.createGUIBtn("Back to main menu");
        backToMainMenuBtn.setOnAction(event -> currentStage.setScene(getMainMenu()));
        root.getChildren().addAll(title, levelsGrid, saveFeedbackText, editOptions1, editOptions2, backToMainMenuBtn);
        Scene customLevelsMenu = new Scene(root, 800, 750);
        return customLevelsMenu;
    }

/*    private Scene getOverviewMenu(){
        if (overviewMenu != null)
            return overviewMenu;

        Text title = new Text("Overview menu");
        title.setFont(new Font(70));

        double btnMinWidth = 200;
        double btnMinHeight = 80;
        String btnStyle = "-fx-background-radius: 20; -fx-font-size: 18px;" ;

        Button backToMainMenuBtn = new Button("Back to main menu");
        backToMainMenuBtn.setMinSize(btnMinWidth, btnMinHeight);
        backToMainMenuBtn.setStyle(btnStyle);
        backToMainMenuBtn.addEventHandler(ActionEvent.ACTION, event -> {currentStage.setScene(getMainMenu());
                                                                        //System.out.println("event handler triggered");
                                                                        });

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ccc39e;");
        root.getChildren().addAll(title, backToMainMenuBtn);
        overviewMenu = new Scene(root, 800, 700);
        return overviewMenu;
    }
*/

    private Scene getAchievementsMenu() {
        Text title = createText("Achievements", 70);

        Set<Achievement> achievements = user.getAchievements();

        Button backToMainMenuBtn = GUIBtn.createGUIBtn("Back to main menu");
        backToMainMenuBtn.setOnAction(event -> currentStage.setScene(getMainMenu()));

        // List of achievements
        VBox achievementsBox = createVBox();

        // Create labels and image views for each achievement with obtained status
        for (Achievement achievement : achievements) {
            HBox achievementBox = createHBox();

            // Image view for the achievement badge
            ImageView badgeImageView = new ImageView(achievement.getBadge());
            badgeImageView.setFitHeight(50); // Set the height as needed
            badgeImageView.setFitWidth(50); // Set the width as needed

            // Label for the achievement name
            Label nameLabel = new Label(achievement.getName());
            nameLabel.setStyle("-fx-font-size: 18px;");

            // Label for the achievement description
            Label descriptionLabel = new Label(achievement.getDescription());
            descriptionLabel.setStyle("-fx-font-size: 14px;");

            Label obtainedLabel;
            if (achievement.isObtained()) {
                obtainedLabel = new Label("Obtained");
            } else {
                obtainedLabel = new Label("Not Obtained");
            }

            // Add the badge image view, achievement name label, description label, and obtained label to the achievement box
            achievementBox.getChildren().addAll(badgeImageView, nameLabel, descriptionLabel, obtainedLabel);

            // Add the achievement box to the achievements box
            achievementsBox.getChildren().add(achievementBox);
        }

        Text totalScoreLabel = createText("Total Score: " + user.getTotalScore(), 22);

        // Root layout for the scene
        VBox root = createVBox();
        root.getChildren().addAll(title, achievementsBox, totalScoreLabel, backToMainMenuBtn);

        // Create the scene
        Scene achievementsMenu = new Scene(root, 800, 750);
        return achievementsMenu;
    }


    public void handleAchievementNotification(int totalScore, int streak, User user, Customiser customiser) {
        for (Achievement achievement : user.getAchievements()) {
            achievement.checkAchievementCriteria(totalScore, streak, customiser);
            if (achievement.isObtained() && achievement.getDisplayedCounter()==1) {
                achievement.incrementDisplayedCounter();
                displayAchievementPopup(achievement);
            }
        }
    }

    public void resetProgress(){
        for (Achievement achievement: user.getAchievements()){
            achievement.resetObtained();
        }
        user.resetTotalScore();
    }

    public void displayAchievementPopup(Achievement achievement) {


        // Create a label to display the achievement name with "New Achievement: " prefix
        Label achievementLabel = new Label("New Achievement: " + achievement.getName());
        achievementLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // Create a StackPane to center the label
        StackPane popupRoot = new StackPane();
        popupRoot.getChildren().add(achievementLabel);
        popupRoot.setAlignment(Pos.CENTER);
        popupRoot.setStyle("-fx-background-color: black; -fx-padding: 10px;");

        // Create the popup
        Popup popup = new Popup();
        popup.getContent().add(popupRoot);

        // Position the popup in the upper left corner
        double xOffset = currentStage.getX() + 20; // Adjust as needed
        double yOffset = currentStage.getY() + 20; // Adjust as needed
        popup.setX(xOffset);
        popup.setY(yOffset);

        // Show the popup
        popup.show(currentStage);

        // Close the popup after a delay (e.g., 3 seconds)
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> popup.hide());
        delay.play();
    }

    private Scene getMainMenu() {
        if (mainMenu != null)
            return mainMenu;

        Text title = createText("LLF", 70);

        Button resumeGameBtn = GUIBtn.createGUIBtn("Resume game");
        resumeGameBtn.addEventHandler(ActionEvent.ACTION, event -> {currentStage.setScene(getLevelsMenu());
                                                                    //System.out.println("event handler triggered");
                                                                    });

        Button newGameBtn = GUIBtn.createGUIBtn("New game");
        newGameBtn.addEventHandler(ActionEvent.ACTION, event -> {currentStage.setScene(getLevelsMenu());
            resetProgress();
                                                                //System.out.println("event handler triggered");
                                                                });

        Button customLevelsBtn = GUIBtn.createGUIBtn("Custom levels");
        customLevelsBtn.addEventHandler(ActionEvent.ACTION, event -> {currentStage.setScene(getCustomLevelsMenu(Mode.VIEW, 0));
                                                                    //System.out.println("event handler triggered");
                                                                    });

        Button achievementsBtn = GUIBtn.createGUIBtn("Achievements");
        achievementsBtn.addEventHandler(ActionEvent.ACTION, event -> {currentStage.setScene(getAchievementsMenu());
                                                                    //System.out.println("event handler triggered");
                                                                    });

        Button quitBtn = GUIBtn.createGUIBtn("Quit");
        quitBtn.addEventHandler(ActionEvent.ACTION, event -> Platform.exit());

        VBox root = createVBox();
        root.getChildren().addAll(title, resumeGameBtn, newGameBtn, customLevelsBtn, achievementsBtn, quitBtn);

        mainMenu = new Scene(root, 800, 750);
        return mainMenu;
    }

    private Scene getFCardCreateMenu(int levelID /*CustomFlashcard customCard*/) {

        Text title = createText("Create flashcard for level: \n" + customiser.getLevelById(levelID).getLevelName(),
                                                                                                            70);

        Text phrase = createText("Phrase", 22);
        TextField phraseField = new TextField();

        Text translation = createText("Translation", 22);
        TextField translationField = new TextField();

        Text hint = createText("Hint", 22);
        TextField hintField = new TextField();

        Button createBtn = GUIBtn.createGUIBtn("Create");
        //currentStage.setScene(new Scene(root, 800, 700));
        createBtn.setOnAction(event -> {
            //Level newlevel = new Level("Mata", 23, 3);
            //int levelId = customiser.createCustomLevel(phraseField.getText(), 3);

            customiser.createFlashcard(levelID, phraseField.getText(), translationField.getText(), hintField.getText());
            //Level newCustomLVL = customiser.getLevelById(levelId);
            //newCustomLVL.addFlashcard();
            //gameManager.addLevel(newCustomLVL);
            currentStage.setScene(getMainMenu()); //FIXME: the respective level's menu, instead of the main menu
            //Scene temp = getLevelsMenu();
            //gameManager.addLevel(newlevel);
        });

        Button cancelBtn = GUIBtn.createGUIBtn("Cancel");
        cancelBtn.setOnAction(event -> currentStage.setScene(getMainMenu())); //FIXME: the respective level's menu,
                                                                             // instead of the main menu

        VBox root = createVBox();
        root.getChildren().addAll(title, phrase, phraseField, translation, translationField, hint, hintField, createBtn,
                                                                                                            cancelBtn);

        Scene fCardCreateMenu = new Scene (root, 800, 750);
        return fCardCreateMenu;
    }

    public void displayCustomFCard(CustomFlashcard fCard, int intFeedback) {
        Text phrase =  createText(fCard.getPhrase(), 22);
        Text hint = createText(fCard.getHint(), 22);
        TextField textField = new TextField();
        textField.setMaxWidth(200);

        Text feedback = new Text();
        if (intFeedback == -1) {
            feedback.setText("WRONG");
            feedback.setFill(Color.RED);
        }
        else if (intFeedback == 1) {
            feedback.setText("OK");
            feedback.setFill(Color.GREEN);
        }

        Button checkAnswerBtn = GUIBtn.createGUIBtn("Check answer");
        checkAnswerBtn.setOnAction(event -> {String enteredAnswer = textField.getText();
            Level level = gameManager.getLevelById(fCard.getLevelID());
            if (level != null)
                level.answerQuestion(enteredAnswer);
            handleAchievementNotification(user.getTotalScore(), gameManager.getCurrentStreak(), user, customiser);
        });

        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
        backToMenuBtn.setOnAction(event -> currentStage.setScene(getLevelsMenu()));

        VBox root = createVBox();
        root.getChildren().addAll(phrase, hint, textField, feedback, checkAnswerBtn, backToMenuBtn);
        currentStage.setScene(new Scene(root, 800, 750));
    }

    public void displayBasicExerciseFCard(BasicExerciseFlashcard fCard, int intFeedback) {
        Text phrase =  createText(fCard.getPhrase(), 22);
        Image hintImage = new Image(new File(fCard.getHintPath()).toURI().toString());
        ImageView hint = new ImageView(hintImage);
        hint.setPreserveRatio(true);
        hint.setFitHeight(180);
        HBox audioButtons = createHBox();
        TextField textField = new TextField();
        textField.setMaxWidth(200);

        Text feedback = new Text();
        if (intFeedback == -1) {
            feedback.setText("WRONG");
            feedback.setFill(Color.RED);
        }
        else if (intFeedback == 1) {
            feedback.setText("OK");
            feedback.setFill(Color.GREEN);
        }

        double btnMinWidth = 200;
        double btnMinHeight = 80;
        String btnStyle = "-fx-background-radius: 20; -fx-font-size: 18px;";

        Button replayAudioBtn = GUI.GUIBtn.createAudioBtn("Play audio");
        replayAudioBtn.setOnAction(event -> fCard.playAudio(1));

        Button replayAudioBtnSlow = GUIBtn.createAudioBtn("Slowed version");
        replayAudioBtnSlow.setOnAction(event -> fCard.playAudio(0.75));

        audioButtons.getChildren().addAll(replayAudioBtn, replayAudioBtnSlow);

        Button checkAnswerBtn = new Button("Check answer");
        checkAnswerBtn.setMinSize(btnMinWidth, btnMinHeight);
        checkAnswerBtn.setStyle(btnStyle);
        checkAnswerBtn.setOnAction(event -> {String enteredAnswer = textField.getText();
            Level level = gameManager.getLevelById(100);
            if (level != null)
                level.answerQuestion(enteredAnswer);
            handleAchievementNotification(user.getTotalScore(), gameManager.getCurrentStreak(), user, customiser);
        });

        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
        backToMenuBtn.setOnAction(event -> currentStage.setScene(getLevelsMenu()));

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ccc39e;");
        root.getChildren().addAll(phrase, hint, audioButtons,textField, feedback, checkAnswerBtn, backToMenuBtn);
        currentStage.setScene(new Scene(root, 800, 750));
    }

    public void displayListeningExercise(ListeningExerciseFlashcard fCard, int intFeedback) {
        Image hintImage = new Image(new File(fCard.getHintPath()).toURI().toString());
        ImageView hint = new ImageView(hintImage);
        hint.setPreserveRatio(true);
        hint.setFitHeight(180);
        HBox audioButtons = createHBox();
        TextField textField = new TextField();
        textField.setMaxWidth(200);

        Text feedback = new Text();
        if (intFeedback == -1) {
            feedback.setText("WRONG");
            feedback.setFill(Color.RED);
        }
        else if (intFeedback == 1) {
            feedback.setText("OK");
            feedback.setFill(Color.GREEN);
        }

        double btnMinWidth = 200;
        double btnMinHeight = 80;
        String btnStyle = "-fx-background-radius: 20; -fx-font-size: 18px;";

        Button replayAudioBtn = GUI.GUIBtn.createAudioBtn("Play audio");
        replayAudioBtn.setOnAction(event -> fCard.playAudio(1));

        Button replayAudioBtnSlow = GUIBtn.createAudioBtn("Slowed version");
        replayAudioBtnSlow.setOnAction(event -> fCard.playAudio(0.75));

        audioButtons.getChildren().addAll(replayAudioBtn, replayAudioBtnSlow);

        Button checkAnswerBtn = new Button("Check answer");
        checkAnswerBtn.setMinSize(btnMinWidth, btnMinHeight);
        checkAnswerBtn.setStyle(btnStyle);
        checkAnswerBtn.setOnAction(event -> {String enteredAnswer = textField.getText();
            Level level = gameManager.getLevelById(200);
            if (level != null)
                level.answerQuestion(enteredAnswer);
            handleAchievementNotification(user.getTotalScore(), gameManager.getCurrentStreak(), user, customiser);
        });

        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
        backToMenuBtn.setOnAction(event -> currentStage.setScene(getLevelsMenu()));

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ccc39e;");
        root.getChildren().addAll(hint, audioButtons,textField, feedback, checkAnswerBtn, backToMenuBtn);
        currentStage.setScene(new Scene(root, 800, 750));
    }

    public void displayFunExercise(FunExerciseFlashcard fCard) {
        Text phrase =  createText(fCard.getPhrase(), 25);
        phrase.setStyle("-fx-font-weight: bold");
        Text translation = createText(fCard.getTranslation(), 18);
        translation.setWrappingWidth(350);
        translation.setTextAlignment(TextAlignment.CENTER);
        Image hintImage = new Image(new File(fCard.getHintPath()).toURI().toString());
        ImageView hint = new ImageView(hintImage);
        hint.setPreserveRatio(true);
        hint.setFitHeight(180);
        HBox audioButtons = createHBox();

        double btnMinWidth = 200;
        double btnMinHeight = 80;
        String btnStyle = "-fx-background-radius: 20; -fx-font-size: 18px;";

        Button replayAudioBtn = GUI.GUIBtn.createAudioBtn("Play audio");
        replayAudioBtn.setOnAction(event -> fCard.playAudio(1));

        Button replayAudioBtnSlow = GUIBtn.createAudioBtn("Slowed version");
        replayAudioBtnSlow.setOnAction(event -> fCard.playAudio(0.75));

        audioButtons.getChildren().addAll(replayAudioBtn, replayAudioBtnSlow);

        Button checkAnswerBtn = new Button("Next");
        checkAnswerBtn.setMinSize(btnMinWidth, btnMinHeight);
        checkAnswerBtn.setStyle(btnStyle);
        checkAnswerBtn.setOnAction(event -> {
            Level level = gameManager.getLevelById(400);
            if (level != null)
                level.answerQuestion("neuken in de keuken - Alex and a dutchie");
            handleAchievementNotification(user.getTotalScore(), gameManager.getCurrentStreak(), user, customiser);
        });

        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
        backToMenuBtn.setOnAction(event -> currentStage.setScene(getLevelsMenu()));

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ccc39e;");
        root.getChildren().addAll(phrase,translation, hint, audioButtons, checkAnswerBtn, backToMenuBtn);
        currentStage.setScene(new Scene(root, 800, 750));
    }

    public void displayCompletionExerciseFCard(CompletionExerciseFlashcard fCard, int intFeedback) {
        Text phrase =  createText(fCard.getPhrase(), 22);
        Text translation = createText(fCard.getTranslation(), 18);
        Image hintImage = new Image(new File(fCard.getHintPath()).toURI().toString());
        ImageView hint = new ImageView(hintImage);
        hint.setPreserveRatio(true);
        hint.setFitHeight(180);
        HBox audioButtons = createHBox();
        TextField textField = new TextField();
        textField.setMaxWidth(200);

        Text feedback = new Text();
        if (intFeedback == -1) {
            feedback.setText("WRONG");
            feedback.setFill(Color.RED);
        }
        else if (intFeedback == 1) {
            feedback.setText("OK");
            feedback.setFill(Color.GREEN);
        }

        double btnMinWidth = 200;
        double btnMinHeight = 80;
        String btnStyle = "-fx-background-radius: 20; -fx-font-size: 18px;";

        Button replayAudioBtn = GUIBtn.createAudioBtn("Play audio");
        replayAudioBtn.setOnAction(event -> fCard.playAudio(1));

        Button replayAudioBtnSlow = GUIBtn.createAudioBtn("Slowed version");
        replayAudioBtnSlow.setOnAction(event -> fCard.playAudio(0.75));

        audioButtons.getChildren().addAll(replayAudioBtn, replayAudioBtnSlow);

        Button checkAnswerBtn = new Button("Check answer");
        checkAnswerBtn.setMinSize(btnMinWidth, btnMinHeight);
        checkAnswerBtn.setStyle(btnStyle);

        checkAnswerBtn.setOnAction(event -> {String enteredAnswer = textField.getText();
            Level level = gameManager.getLevelById(300);
            if (level != null)
                level.answerQuestion(enteredAnswer);
            handleAchievementNotification(user.getTotalScore(), gameManager.getCurrentStreak(), user, customiser);
        });

        Button backToMenuBtn = GUIBtn.createGUIBtn("Back to menu");
        backToMenuBtn.setOnAction(event -> currentStage.setScene(getLevelsMenu()));

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #ccc39e;");
        root.getChildren().addAll(phrase, translation, hint, audioButtons, textField, feedback, checkAnswerBtn,
                                                                                                        backToMenuBtn);
        currentStage.setScene(new Scene(root, 800, 750));
    }

    public void displayImgHint(String hintPath) {
      //FIXME: hint to be displayed on command?
    }

    public void displayLevelsMenu() {
        currentStage.setScene(getLevelsMenu());
    }

    
    public static GUI getInstance() {
        if (instance == null) {
            instance = new GUI();
        }
        return instance;
    }

    public Stage launchGUI(Stage primaryStage){
        currentStage = primaryStage;
        currentStage.setTitle("LLF");
        currentStage.setScene(mainMenu);
        return currentStage;
    }
}
