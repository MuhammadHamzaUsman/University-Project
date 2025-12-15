package com.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import com.example.io.LevelIO;
import com.example.io.PuzzelLevel;
import com.example.shape.Voxel;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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

    public static LevelIO levelIO = new LevelIO();

    public static Thread jmeThread = null;
    public static App app = null;
    public static double popSoundLevel = 1;
    
    private boolean isLevelSelectionUIOpen = false;
    private VBox levelSelectionVBox;
    private Stage stage;
    private StackPane screenStackPane;
    private double backgroundMusicLevel = 1;

    public void intializeMainMenu(){
        
        font = Font.loadFont(
            getClass().getResourceAsStream("/fonts/BlockCraftMedium-PVLzd.otf"),
            14
        );
        
        levels = levelIO.readPuzzels(true);

        
        Image backgroundImageInput = new Image(getClass().getResourceAsStream("/images/background.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(
            backgroundImageInput, 
            BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, 
            BackgroundPosition.CENTER, 
            new BackgroundSize(0, 0, false, false, false, true)
        );
        Background background = new Background(backgroundImage);

        Image buttonBackground = new Image(getClass().getResourceAsStream("/images/button.png"));
        ImageView[] imageViews = new ImageView[3];
        for (int i = 0; i < imageViews.length; i++) {
            imageViews[i] = new ImageView(buttonBackground);
            imageViews[i].setPreserveRatio(true);
            imageViews[i].setFitHeight(60);
            imageViews[i].setFitWidth(240);
            imageViews[i].setSmooth(false);
            imageViews[i].setCache(true);
        }

        Image buttonPressedBackground = new Image(getClass().getResourceAsStream("/images/button_pressed.png"));
        ImageView[] imageViewsPressed = new ImageView[3];
        for (int i = 0; i < imageViewsPressed.length; i++) {
            imageViewsPressed[i] = new ImageView(buttonPressedBackground);
            imageViewsPressed[i].setPreserveRatio(true);
            imageViewsPressed[i].setFitHeight(60);
            imageViewsPressed[i].setFitWidth(240);
            imageViewsPressed[i].setSmooth(false);
            imageViewsPressed[i].setCache(true);
        }

        Image logoTextInput = new Image(getClass().getResourceAsStream("/images/forge_voxel.png"));
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
        
        stage = new Stage();
        
        LevelSelectionUI levelUi = new LevelSelectionUI(360, 560);
        
        StackPane screenStackPane = new StackPane(borderPane);
        
        levelSelectionVBox = levelUi.intializeUI(screenStackPane);

        readSoundLevels();
        VBox musicSlider = intializeMusicSliders(backgroundMusicLevel, popSoundLevel);
        screenStackPane.getChildren().add(musicSlider);

        Scene scene = new Scene(screenStackPane, 640, 480);
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );
        
        stage.setScene(scene);
        stage.setMaximized(true);

        startButton.setOnMousePressed(event -> startButton.setGraphic(imageViewsPressed[0]));
        startButton.setOnMouseReleased(
            (event) -> {
                startButton.setGraphic(imageViews[0]);
                PuzzelLevel puzzelLevel = levelIO.readLastPlayedLevel(true);

                if(puzzelLevel != null){
                    levelIO.loadFunc(puzzelLevel);
                    if(MainMenu.jmeThread == null){
                        MainMenu.jmeThread = new Thread(
                            () -> {
                                MainMenu.levelIO.loadFunc(puzzelLevel);
                                app = new App(puzzelLevel, levelIO.getLastPlayedLevelIndex(), levelUi);
                                app.start();
                            }
                        );

                        MainMenu.jmeThread.setDaemon(true);
                        MainMenu.jmeThread.start();
                    }
                }else{
                    if(!isLevelSelectionUIOpen){
                        screenStackPane.getChildren().add(levelSelectionVBox);
                    }
                }
            }
        );

        continueButton.setOnMousePressed(event -> continueButton.setGraphic(imageViewsPressed[1]));
        continueButton.setOnMouseReleased(
            (event) -> {
                continueButton.setGraphic(imageViews[1]);
                
                if(!isLevelSelectionUIOpen){
                    screenStackPane.getChildren().add(levelSelectionVBox);
                }
            }
        );

        exitButton.setOnMousePressed(event -> exitButton.setGraphic(imageViewsPressed[2]));
        exitButton.setOnMouseReleased(
            (event) -> {
                exitButton.setGraphic(imageViews[2]);
                writeSoundLevels();
                close();
            }
        );

        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        stage.setTitle("Forge Voxel");
        stage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        intializeMainMenu();
    }

    private void close(){
        if(jmeThread != null){
            app.stop();
        }

        if(isLevelSelectionUIOpen){
            screenStackPane.getChildren().removeLast();
        }


        stage.close();
    }

    private VBox intializeMusicSliders(double musicVolume, double popVolume){
        try{
            VBox musicSlider = new VBox(10);
            musicSlider.setPadding(new Insets(10, 10, 0, 0));
            musicSlider.setAlignment(Pos.TOP_RIGHT);
            StackPane.setAlignment(musicSlider, Pos.TOP_RIGHT);

            Media backgroundMusic = new Media(getClass().getResource("/sounds/USSR National Anthem lofi by Febri Ultra.mp3").toURI().toString());
            MediaPlayer backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
            backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusicPlayer.setOnReady(() -> backgroundMusicPlayer.play());
            backgroundMusicPlayer.setVolume(musicVolume);

            musicSlider.getChildren().add(
                getSlider(
                    "Background Music", 
                    (obs, oldVol, newVol) -> {
                        if(backgroundMusicPlayer != null){
                            backgroundMusicPlayer.setVolume(newVol.doubleValue());
                        }
                        backgroundMusicLevel = newVol.doubleValue();
                    }, 
                    musicVolume
                )
            );

            Voxel.updatePopSoundVolume(popVolume);
            musicSlider.getChildren().add(
                getSlider(
                    "Game Sounds", 
                    (observable, oldVolume, newVolumn) -> {
                        popSoundLevel = newVolumn.doubleValue();
                        if(jmeThread != null){
                            Voxel.updatePopSoundVolume(popSoundLevel);
                        }
                    }, 
                    popVolume
                )
            );

            musicSlider.setMaxWidth(Region.USE_PREF_SIZE);
            musicSlider.setMinWidth(Region.USE_PREF_SIZE);
            musicSlider.setPrefWidth(Region.USE_COMPUTED_SIZE);
            musicSlider.setMaxHeight(Region.USE_PREF_SIZE);
            musicSlider.setMinHeight(Region.USE_PREF_SIZE);
            musicSlider.setPrefHeight(Region.USE_COMPUTED_SIZE);
            return musicSlider;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private HBox getSlider(String label, ChangeListener<? super Number> listener, double startVolume){
        try{
            Slider soundSlider = new Slider(0, 1, startVolume);
            soundSlider.getStyleClass().add("slider");
            soundSlider.setBackground(Background.EMPTY);
            soundSlider.valueProperty().addListener(listener);

            StackPane trackPane = new StackPane();
            trackPane.setPrefSize(300, 20);
            trackPane.setBackground(
                new Background(
                    new BackgroundImage(
                        new Image("images/slider.png"),
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                        BackgroundPosition.CENTER,
                        new BackgroundSize(300, 20, false, false, false, false)
                    )
                )
            );

            StackPane sliderStack = new StackPane(trackPane, soundSlider);
            
            Label sliderLabel = new Label(label);
            sliderLabel.setTextFill(Color.WHITE);
            sliderLabel.setFont(Font.font(LevelSelectionUI.font.getFamily(), 18));
            
            HBox sliderRow = new HBox(10, sliderLabel, sliderStack);

            return sliderRow;
        } 
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static final String SETTINGS_FILE = System.getProperty("user.dir") + "\\settings\\userSounds.dat";
    
    private void writeSoundLevels(){
        try {
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(SETTINGS_FILE)));
            outputStream.writeDouble(backgroundMusicLevel);
            outputStream.writeDouble(popSoundLevel);
            outputStream.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readSoundLevels(){
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(new File(SETTINGS_FILE)));
            backgroundMusicLevel = inputStream.readDouble();
            popSoundLevel = inputStream.readDouble();
            inputStream.close();
        } 
        catch (Exception e) {
            backgroundMusicLevel = 1;
            popSoundLevel = 1;
            e.printStackTrace();
        }
    }
}
