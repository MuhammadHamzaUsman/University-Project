package com.example.TextEditor.Interpreter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.lwjgl.glfw.GLFW;

import com.example.App;
import com.example.shape.Colors;
import com.example.shape.Size;
import com.jme3.system.lwjgl.LwjglWindow;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CodeEditorUI{

    private int width = 100;
    private int height = 100;
    private int screenX = 0;
    private int screenY = 0;

    private CodeArea codeEditor;
    private VirtualizedScrollPane<CodeArea> vsPane;
    private TextArea errorDisplay;
    private GridPane propertiesGrid;
    private PauseTransition codeRunDelay = new PauseTransition(Duration.seconds(2));
    private String code;
    private Stage editorStage; 
    private Scene scene;
    private VBox layout;
    private boolean isErrorDisplaying = false;
    private HBox root;
    private HBox topBar;
    private App app;
    private Rectangle terminalBackground;

    public CodeEditorUI (App app){
        this.app = app;

        long windowHandle = ((LwjglWindow)app.getContext()).getWindowHandle();
        
        int[] x = new int[1];
        int[] y = new int[1];

        GLFW.glfwGetWindowPos(windowHandle, x, y);
        screenX = x[0];
        screenY = y[0];

        GLFW.glfwGetWindowSize(windowHandle, x, y);
        width = app.getContext().getFramebufferWidth();
        height = app.getContext().getFramebufferHeight();

        codeRunDelay.setOnFinished((event) -> app.runCode(code));
    }

    public void intializeUI(){
        intializeTopBar();
        intializeCodeEditor();
        intializePropertyBar();
        intializeErrorDisplay();

        Font font = Font.loadFont(
            getClass().getResourceAsStream("/BlockCraftMedium-PVLzd.otf"),
            14
        );

        layout = new VBox(topBar, vsPane, propertiesGrid);
        layout.setMaxWidth(Double.MAX_VALUE);
        layout.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(vsPane, Priority.ALWAYS);

        HBox.setHgrow(layout, Priority.ALWAYS);
        HBox.setHgrow(errorDisplay, Priority.SOMETIMES);

        root = new HBox();
        root.setAlignment(Pos.CENTER_LEFT);
        root.getChildren().add(layout);

        scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        editorStage = new Stage();
        editorStage.setScene(scene);

        editorStage.setWidth(width / 2);
        editorStage.setHeight(height);
        editorStage.setX(screenX);
        editorStage.setY(screenY);

        editorStage.setAlwaysOnTop(true);
        editorStage.initStyle(StageStyle.UNDECORATED);

        editorStage.show();
    }

    private void intializeCodeEditor(){
        codeEditor = new CodeArea();
        codeEditor.setMaxWidth(Double.MAX_VALUE);
        codeEditor.setParagraphGraphicFactory(LineNumberFactory.get(codeEditor));
        codeEditor.getStyleClass().add("code-editor");

        vsPane = new VirtualizedScrollPane<>(codeEditor);
        vsPane.setMaxWidth(Double.MAX_VALUE);

        codeEditor.textProperty().addListener((obs, oldText, newText) -> {
            code = newText;
            codeEditor.setStyleSpans(0, Highlighter.highlight(code));
            codeRunDelay.stop();
            codeRunDelay.play();
        });

        codeEditor.addEventHandler(
            KeyEvent.KEY_TYPED, e -> {
                String character = e.getCharacter();
                int caret = codeEditor.getCaretPosition();

                switch (character) {
                    case "(": 
                        codeEditor.insertText(caret, ")"); 
                        codeEditor.moveTo(caret); 
                        break;
                    case "{": 
                        codeEditor.insertText(caret, "}"); 
                        codeEditor.moveTo(caret); 
                        break;
                }
            }
        );

        codeEditor.addEventHandler(
            KeyEvent.KEY_PRESSED, e -> {
                int caret = codeEditor.getCaretPosition();

                if(e.getCode() == KeyCode.TAB) {
                    e.consume();
                    codeEditor.replaceText(caret - 1, caret, "    ");
                }

                if(e.getCode() == KeyCode.ENTER) {
                    Pattern whiteSpace = Pattern.compile("^\\s+");
                    int currentParagraph = codeEditor.getCurrentParagraph();
                    if (currentParagraph > 0) {
                        String previousLine = codeEditor.getParagraph(currentParagraph - 1).getText();
                        
                        Matcher m = whiteSpace.matcher(previousLine);
                        if (m.find()) {
                            String indentation = m.group();
                            codeEditor.insertText(codeEditor.getCaretPosition(), indentation);
                            e.consume();
                        }
                    }
                }
            }
        );
    }

    private void intializeTopBar(){
        topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER);
        topBar.setMaxWidth(Double.MAX_VALUE);
        topBar.setPrefHeight(40);
        topBar.getStyleClass().add("grid-pane");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        ImageView terminal  = new ImageView(new Image(getClass().getResourceAsStream("/terminal.png")));
        terminal.setOnMouseClicked(
            (event) -> {
                if(event.getButton() == MouseButton.PRIMARY){
                    if(isErrorDisplaying){
                        removeErrorDisplay();
                    }
                    else{
                        addErrorDisplay();
                    }
                }
            }
        );

        terminalBackground = new Rectangle(36, 36);
        terminalBackground.setFill(Color.rgb(60, 60, 60));
        terminalBackground.setArcHeight(10);
        terminalBackground.setArcWidth(10);

        StackPane pane = new StackPane(terminalBackground, terminal);
        pane.setPadding(new Insets(0, 10, 0, 0));

        topBar.getChildren().addAll(spacer, pane);
    }

    private void removeErrorDisplay() {
        isErrorDisplaying = false;

        terminalBackground.setFill(Color.rgb(60, 60, 60));

        root.getChildren().remove(1);
        editorStage.setWidth(width / 2);
        editorStage.setHeight(height);
    }

    private void addErrorDisplay() {
        isErrorDisplaying = true;

        terminalBackground.setFill(Color.rgb(80, 80, 80));

        root.getChildren().add(1, errorDisplay);
        editorStage.setWidth(width);
        editorStage.setHeight(height);
    }

    private void intializeErrorDisplay(){
        errorDisplay = new TextArea();
        errorDisplay.setEditable(false);
        errorDisplay.setWrapText(true);
        errorDisplay.setMaxWidth(width / 2);
        errorDisplay.setMaxHeight(Double.MAX_VALUE);
        errorDisplay.setStyle(
            "-fx-control-inner-background: #2b2b2b;" +
            "-fx-font-family: Consolas;" +
            "-fx-text-fill: red;" +
            "-fx-font-size: 14px;"
        );
    }

    private void intializePropertyBar(){
        propertiesGrid = new GridPane();
        propertiesGrid.setMaxWidth(Double.MAX_VALUE);
        propertiesGrid.setPrefHeight(50);
        propertiesGrid.getStyleClass().add("grid-pane");

        int colorCol = getColors(propertiesGrid)[0];
        HBox shapeRow = getRowOfShapes();
        HBox sizeRow = getRowOfSizes();
        
        propertiesGrid.add(shapeRow, colorCol, 0);
        propertiesGrid.add(sizeRow, colorCol, 1);
    }

    private int[] getColors(GridPane grid){
        int numColors = Colors.values().length;

        double colorRow = 2;
        int colorCol = (int)Math.ceil(numColors / colorRow);
        int index = 0;
        int xGrid = 0, yGrid = 0;
        Rectangle backRectangle;

        while (index < numColors) {
            backRectangle = new Rectangle(25, 25, Colors.getColor(index++).getFxColor());
            grid.add(backRectangle, xGrid++, yGrid);
            
            if(xGrid >= colorCol){ 
                xGrid = 0;
                yGrid++;
            }
        }

        return new int []{colorCol, (int)colorRow};
    }

    private Rectangle createVerticalSperator(int width, int height){
        Rectangle rect = new Rectangle(width, height);
        rect.setFill(Color.rgb(79, 79, 79));
        return rect;
    }

    private HBox getRowOfShapes(){

        ImageView cubeImage = new ImageView(new Image(getClass().getResourceAsStream("/cube.png")));
        ImageView sphereImage = new ImageView(new Image(getClass().getResourceAsStream("/sphere.png")));
        ImageView cylinderImage = new ImageView(new Image(getClass().getResourceAsStream("/cylinder.png")));     

        for(ImageView imageView : List.of(cubeImage, sphereImage, cylinderImage)){
            imageView.setFitWidth(25);
            imageView.setFitHeight(25);
            imageView.setPreserveRatio(true);
        }

        HBox shapeRow = new HBox(3);
        shapeRow.setAlignment(Pos.CENTER);
        shapeRow.setPadding(new Insets(0, 3, 0, 3));

        shapeRow.getChildren().addAll(cubeImage, createVerticalSperator(1, 20), sphereImage, createVerticalSperator(1, 20), cylinderImage);

        return shapeRow;
    }

    private HBox getRowOfSizes(){

        Image image = new Image(getClass().getResourceAsStream("/cube.png"));

        ImageView smallImage = new ImageView(image);
        ImageView mediumImage = new ImageView(image);
        ImageView largeImage = new ImageView(image);
        int index = 0;

        for(ImageView imageView : List.of(smallImage, mediumImage, largeImage)){
            imageView.setScaleX(Size.getSize(index).getFactor());
            imageView.setScaleY(Size.getSize(index++).getFactor());
            imageView.setFitWidth(25);
            imageView.setFitHeight(25);
            imageView.setPreserveRatio(true);
        }

        HBox shapeRow = new HBox(3);
        shapeRow.setAlignment(Pos.CENTER);
        shapeRow.setPadding(new Insets(0, 3, 0, 3));

        shapeRow.getChildren().addAll(smallImage, createVerticalSperator(1, 20), mediumImage, createVerticalSperator(1, 20), largeImage);

        return shapeRow;
    }

    public void updatePosition(int x, int y){
        if(editorStage != null){
            screenX = x;
            screenY = y;
            editorStage.setX(x);
            editorStage.setY(y);
        }
    }

    public void updateSize(int width, int height){
        if(editorStage != null){
            editorStage.setWidth(width / 2);
            editorStage.setHeight(height);
        }
    }

    public void updateErrorDisplay(String error){
        errorDisplay.setText(error);
    }

    public void close(){
        if(editorStage != null){
            editorStage.close();
        }
        Platform.exit();
    }
}