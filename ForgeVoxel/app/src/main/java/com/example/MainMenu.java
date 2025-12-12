package com.example;

import java.util.ArrayList;
import java.util.List;

import com.example.io.LevelReader;
import com.example.io.PuzzelLevel;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MainMenu extends Application{

    public static void main(String[] args) {
        launch(args);
    }
    
    public static List<PuzzelLevel> levels;
    public static Font font;

    public static LevelReader levelReader = new LevelReader();

    public static Thread jmeThread = null;
    private static boolean isLevelSelectionUIOpen = false;

    public void intializeMainMenu(){
        
        font = Font.loadFont(
            getClass().getResourceAsStream("/BlockCraftMedium-PVLzd.otf"),
            14
        );
        
        levels = loadPuzzels();

        Image backgroundImageInput = new Image(getClass().getResourceAsStream("/background.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(
            backgroundImageInput, 
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, 
            new BackgroundSize(0, 0, false, false, false, true)
        );
        Background background = new Background(backgroundImage);

        Image buttonBackground = new Image(getClass().getResourceAsStream("/button.png"));
        ImageView[] imageViews = new ImageView[3];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(buttonBackground);
            imageViews[i].setPreserveRatio(true);
            imageViews[i].setFitHeight(60);
            imageViews[i].setFitWidth(240);
            imageViews[i].setSmooth(false);
            imageViews[i].setCache(true);
        }

        Image buttonPressedBackground = new Image(getClass().getResourceAsStream("/button_pressed.png"));
        ImageView[] imageViewsPressed = new ImageView[3];
        for (int i = 0; i < imageViewsPressed.length; i++) {
            imageViewsPressed[i] = new ImageView(buttonPressedBackground);
            imageViewsPressed[i].setPreserveRatio(true);
            imageViewsPressed[i].setFitHeight(60);
            imageViewsPressed[i].setFitWidth(240);
            imageViewsPressed[i].setSmooth(false);
            imageViewsPressed[i].setCache(true);
        }

        Image logoTextInput = new Image(getClass().getResourceAsStream("/forge_voxel.png"));
        ImageView logoImage = new ImageView(logoTextInput);
        logoImage.setPreserveRatio(true);
        logoImage.setSmooth(false);
        logoImage.setCache(true);
        logoImage.setFitHeight(175);
        logoImage.setFitWidth(963);

        Button startButton = new Button("Start");
        startButton.setBackground(Background.EMPTY);
        startButton.setMinSize(240, 60);
        startButton.setGraphic(imageViews[0]);
        startButton.setContentDisplay(ContentDisplay.CENTER);
        startButton.setTextFill(Color.WHITE);
        startButton.setFont(Font.font(font.getFamily(), FontWeight.BOLD, 30));

        Button continueButton = new Button("Continue");
        continueButton.setBackground(Background.EMPTY);
        continueButton.setMinSize(240, 60);
        continueButton.setGraphic(imageViews[1]);
        continueButton.setContentDisplay(ContentDisplay.CENTER);
        continueButton.setTextFill(Color.WHITE);
        continueButton.setFont(Font.font(font.getFamily(), FontWeight.BOLD, 30));

        Button exitButton = new Button("Exit");
        exitButton.setBackground(Background.EMPTY);
        exitButton.setMinSize(240, 60);
        exitButton.setGraphic(imageViews[2]);
        exitButton.setContentDisplay(ContentDisplay.CENTER);
        exitButton.setTextFill(Color.WHITE);
        exitButton.setFont(Font.font(font.getFamily(), FontWeight.BOLD, 30));

        HBox hbox = new HBox(30, startButton, continueButton);
        hbox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(20, hbox, exitButton);
        vbox.setAlignment(Pos.CENTER);
        
        BorderPane borderPane = new BorderPane();
        borderPane.setBackground(background);
        borderPane.setCenter(vbox);

        StackPane topPane = new StackPane(logoImage);
        topPane.setAlignment(Pos.CENTER);
        topPane.setPrefHeight(175);
        topPane.setPadding(new Insets(100, 0, 0, 0));
        borderPane.setTop(topPane);
        
        Stage stage = new Stage();
        
        LevelSelectionUI levelUi = new LevelSelectionUI(360, 560);
        StackPane stackPane = new StackPane(borderPane);
        Scene scene = new Scene(stackPane, 640, 480);
        
        stage.setScene(scene);
        stage.setMaximized(true);

        startButton.setOnMousePressed(event -> startButton.setGraphic(imageViewsPressed[0]));
        startButton.setOnMouseReleased(event -> startButton.setGraphic(imageViews[0]));

        continueButton.setOnMousePressed(event -> continueButton.setGraphic(imageViewsPressed[1]));
        continueButton.setOnMouseReleased(
            (event) -> {
                continueButton.setGraphic(imageViews[1]);
                
                if(!isLevelSelectionUIOpen){
                    stackPane.getChildren().add(levelUi.intializeUI(stackPane));
                }
            }
        );

        exitButton.setOnMousePressed(event -> exitButton.setGraphic(imageViewsPressed[2]));
        exitButton.setOnMouseReleased(
            (event) -> {
                exitButton.setGraphic(imageViews[2]);
                stage.close();
            }
        );

        stage.show();
    }

    public List<PuzzelLevel> loadPuzzels(){
        File levelsFolder = new File(System.getProperty("user.dir") + "/levels");
        File levelsArray[] = levelsFolder.listFiles((File file, String name) -> name.endsWith(".dat"));
        ArrayList<PuzzelLevel> levels = new ArrayList<>(levelsArray.length);

        for (File level : levelsArray) {
            levels.add(levelReader.readLevel(level.getPath(), true));
        }
        
        return levels;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        intializeMainMenu();
    }
}
