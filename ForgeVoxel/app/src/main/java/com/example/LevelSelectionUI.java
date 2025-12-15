package com.example;

import com.example.io.PuzzelLevel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;

public class LevelSelectionUI {
    private int width;
    private int height;
    private VBox levelsList;
    private Image completdGraphic;

    private static final double CORRECTION_PERCENTAGE = 0.0095;
    private static final double LEFT_RATIO = CORRECTION_PERCENTAGE + 18.0 / 58.0;
    private static final double RIGHT_RATIO = -CORRECTION_PERCENTAGE + 40.0 / 58.0;
    
    public static Font font;

    public LevelSelectionUI(int width, int height){
        this.width = width;
        this.height = height;

        font = Font.loadFont(
            getClass().getResourceAsStream("/fonts/BoldPixels.ttf"),
            30
        );
    }

    public VBox intializeUI(StackPane stackPane){
        ImageView exitButtonGraphic = new ImageView(new Image(getClass().getResourceAsStream("/images/exit.png")));
        ImageView exitButtonPressedGraphic = new ImageView(new Image(getClass().getResourceAsStream("/images/exit_pressed.png")));
        
        Button exitButton = new Button();
        exitButton.setBackground(Background.EMPTY);
        exitButton.setMinSize(20, 20);
        exitButton.setGraphic(exitButtonGraphic);
        exitButton.setContentDisplay(ContentDisplay.CENTER);
        
        Image cardBackGround = new Image(getClass().getResourceAsStream("/images/level_card_back.png"));
        Image cardBackGroundPressed = new Image(getClass().getResourceAsStream("/images/level_card_back_pressed.png"));
        completdGraphic = new Image(getClass().getResourceAsStream("/images/completed.png"));

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(5, 5, 5, 5));
        layout.setMaxSize(width, height);
        layout.setAlignment(Pos.TOP_LEFT);

        levelsList = new VBox(10);
        levelsList.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        levelsList.setAlignment(Pos.TOP_CENTER);
        
        for(int i = 0; i < MainMenu.levels.size(); i++){
            levelsList.getChildren().add(getLevelCard(i, MainMenu.levels.get(i), cardBackGround, cardBackGroundPressed, completdGraphic));
        }

        Background background = new Background(new BackgroundFill(Color.rgb(31, 31, 31), CornerRadii.EMPTY, new Insets(5)));
        layout.setBackground(background);

        BorderStroke outerBorder = new BorderStroke(
            Color.rgb(50, 50, 50), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(8));

        BorderStroke innerBorder = new BorderStroke(
            Color.rgb(144, 164, 174), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(6), new Insets(1));

        Border border = new Border(outerBorder, innerBorder);
        layout.setBorder(border);

        exitButton.setOnMousePressed((event) -> exitButton.setGraphic(exitButtonPressedGraphic));
        exitButton.setOnMouseReleased( 
            (event) -> {
                exitButton.setGraphic(exitButtonGraphic);
                stackPane.getChildren().removeLast();
            } 
        );

        layout.getChildren().addAll(exitButton, levelsList);
        
        return layout;
    }

    private StackPane getLevelCard(int puzzelNumber, PuzzelLevel puzzelLevel, Image background, Image backgroundPressed, Image completedImage){
        double intialVerticalPadding = background.getHeight() * 0.016;
        double intialHorizontalPadding = background.getWidth() * 0.05;
        Insets intailPadding = new Insets(intialVerticalPadding, intialHorizontalPadding, intialVerticalPadding, intialHorizontalPadding);

        HBox root = new HBox();
        root.setPadding(intailPadding);

        StackPane leftPart = new StackPane();
        leftPart.setPadding(leftPartPadding(intailPadding));
        leftPart.setPrefWidth(background.getWidth() * LEFT_RATIO);

        ImageView preview = new ImageView(puzzelLevel.image);
        preview.setPreserveRatio(false);
        preview.setSmooth(false);

        leftPart.getChildren().add(preview);
        StackPane.setAlignment(preview, Pos.CENTER);

        Label name = new Label(puzzelLevel.levelName);
        name.setWrapText(true);
        name.setTextFill(Color.WHITE);
        name.setFont(font);
        name.setStyle("-fx-font-smoothing-type: gray;");

        Label dimension = new Label(String.format("%d x %d x %d", puzzelLevel.width, puzzelLevel.height, puzzelLevel.depth));
        dimension.setWrapText(true);
        dimension.setTextFill(Color.WHITE);
        dimension.setFont(font);
        dimension.setStyle("-fx-font-smoothing-type: gray;");

        VBox rightPart = new VBox(1, name, dimension);
        rightPart.setAlignment(Pos.CENTER_LEFT);
        rightPart.setPadding(rightPartPadding(intailPadding));
        rightPart.setPrefWidth(background.getWidth() * RIGHT_RATIO);

        root.getChildren().addAll(leftPart, rightPart);

        root.widthProperty().addListener(
            (obs, oldWidth, newWidth) -> {
                double width = newWidth.doubleValue();
                double height = root.getHeight();

                leftPart.setPrefWidth(width * LEFT_RATIO);
                rightPart.setPrefWidth(width * RIGHT_RATIO);

                double verticalPadding = height * 0.05;
                double horizontalPadding = width * 0.016;

                root.setPadding(new Insets(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));
            }
        );

        root.heightProperty().addListener(
            (obs, oldHeight, newHeight) -> {
                double height = newHeight.doubleValue();
                double width = root.getWidth();

                double verticalPadding = height * 0.05;
                double horizontalPadding = width * 0.016;

                Insets padding = new Insets(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding);
                leftPart.setPadding(leftPartPadding(padding));
                rightPart.setPadding(rightPartPadding(padding));
                root.setPadding(padding);

                double innerWidth = leftPart.getWidth() - (padding.getLeft() + padding.getRight());
                double innerHeight = leftPart.getHeight() - (padding.getTop() + padding.getBottom());

                double side = Math.min(innerWidth, innerHeight);

                preview.setFitWidth(side);
                preview.setFitHeight(side);
            }
        );

        ImageView[] images = new ImageView[]{
            intializeImage(background), intializeImage(backgroundPressed), intializeImage(completedImage)
        };

        StackPane stackPane = new StackPane(images[0], root);
        stackPane.setMaxSize(background.getWidth(), background.getHeight());

        StackPane.setAlignment(images[2], Pos.TOP_RIGHT);
        if(puzzelLevel.completed){
            stackPane.getChildren().add(images[2]);
        }

        stackPane.setOnMousePressed(
            event -> {
                ImageView imageView = (ImageView)stackPane.getChildren().getFirst();
                imageView.setImage(backgroundPressed);
            }
        );
        
        stackPane.setOnMouseReleased(
            event -> {
                ImageView imageView = (ImageView)stackPane.getChildren().getFirst();
                imageView.setImage(background);
                if(MainMenu.jmeThread == null){
                    MainMenu.jmeThread = new Thread(
                        () -> {
                            MainMenu.levelIO.loadFunc(puzzelLevel);
                            MainMenu.app = new App(puzzelLevel, puzzelNumber, this);
                            MainMenu.app.start();
                        }
                    );

                    MainMenu.jmeThread.setDaemon(true);
                    MainMenu.jmeThread.start();
                }
            }
        );
        
        return stackPane;
    }

    private ImageView intializeImage(Image image){
        ImageView cardBack = new ImageView(image);
        cardBack.setSmooth(false);
        cardBack.setFitHeight(image.getHeight());
        cardBack.setFitWidth(image.getWidth());
        cardBack.setCache(true);
        return cardBack;
    }

    private Insets leftPartPadding(Insets insets){
        return new Insets(
            insets.getTop() * 2,
            insets.getRight() * 2,
            insets.getBottom() * 2,
            insets.getLeft() * 2
        );
    }

    private Insets rightPartPadding(Insets insets){
        return new Insets(
            insets.getTop() * 2,
            insets.getRight() * 5,
            insets.getBottom() * 2,
            insets.getLeft() * 5
        );
    }    

    public void setLevelComplted(int index){
        StackPane level = (StackPane)levelsList.getChildren().get(index);
        if(level.getChildren().size() <= 2){
            ImageView compltedImage = intializeImage(completdGraphic);
            StackPane.setAlignment(compltedImage, Pos.TOP_RIGHT);
            level.getChildren().add(compltedImage);
        }
    }
}
