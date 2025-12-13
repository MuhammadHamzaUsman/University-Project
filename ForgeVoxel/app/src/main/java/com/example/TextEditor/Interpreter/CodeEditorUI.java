package com.example.TextEditor.Interpreter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.lwjgl.glfw.GLFW;

import com.example.App;
import com.example.LevelSelectionUI;
import com.example.MainMenu;
import com.example.io.PuzzelLevel;
import com.example.shape.Colors;
import com.example.shape.Size;
import com.jme3.system.lwjgl.LwjglWindow;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
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

    private Text matchPercentageDisplay;

    private Button nextButton;
    private ImageView[] nextGraphics;
    private int disableOffset = 0;

    private Font font;
    private Image cubeImage;
    private Image sphereImage;
    private Image cylinderImage;

    private ImageView shapeGeom; 
    private Text[] textsGeom;
    private Rectangle colorGeom;

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
        font = Font.loadFont(
            getClass().getResourceAsStream("/BlockCraftMedium-PVLzd.otf"),
            14
        );

        cubeImage = new Image(getClass().getResourceAsStream("/cube.png"));
        sphereImage = new Image(getClass().getResourceAsStream("/sphere.png"));
        cylinderImage = new Image(getClass().getResourceAsStream("/cylinder.png"));
        
        intializeTopBar();
        intializeCodeEditor();
        intializePropertyBar();
        intializeErrorDisplay();
        
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
        topBar = new HBox(5);
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

        ImageView exitButtonGraphic = new ImageView(new Image(getClass().getResourceAsStream("/exit.png")));
        ImageView exitButtonPressedGraphic = new ImageView(new Image(getClass().getResourceAsStream("/exit_pressed.png")));
        
        Button exitButton = new Button();
        exitButton.setBackground(Background.EMPTY);
        exitButton.setMinSize(20, 20);
        exitButton.setGraphic(exitButtonGraphic);
        exitButton.setContentDisplay(ContentDisplay.CENTER);

        exitButton.setOnMousePressed((event) -> exitButton.setGraphic(exitButtonPressedGraphic));
        exitButton.setOnMouseReleased( 
            (event) -> {
                MainMenu.levelIO.updateUserCode(code, app.puzzelLevel);
                MainMenu.levelIO.writeLastPlayedLevel(app.puzzelLevel.levelName, app.puzzelNumber);
                exitButton.setGraphic(exitButtonGraphic);
                app.stop();
            } 
        );

        nextGraphics = new ImageView[]{
            new ImageView(new Image(getClass().getResourceAsStream("/next.png"))),
            new ImageView(new Image(getClass().getResourceAsStream("/next_pressed.png"))),
            new ImageView(new Image(getClass().getResourceAsStream("/next_disable.png"))),
            new ImageView(new Image(getClass().getResourceAsStream("/next_pressed_disable.png")))
        };
        
        nextButton = new Button();
        nextButton.setBackground(Background.EMPTY);
        nextButton.setMinSize(20, 20);
        nextButton.setContentDisplay(ContentDisplay.CENTER);
        
        matchPercentageDisplay = new Text("00.00%");
        matchPercentageDisplay.setFill(Color.rgb(199, 199, 199));
        matchPercentageDisplay.setFont(Font.font(LevelSelectionUI.font.getFamily(), 16));
        
        if(app.puzzelLevel.completed){
            nextButton.setDisable(false);
            nextButton.setGraphic(nextGraphics[0 + disableOffset]);
        }
        else{
            disableOffset += 2;
            nextButton.setDisable(true);
            nextButton.setGraphic(nextGraphics[0 + disableOffset]);
        }

        nextButton.setOnMousePressed((event) -> nextButton.setGraphic(nextGraphics[1 + disableOffset]));
        
        nextButton.setOnMouseReleased( 
            (event) -> {
                nextButton.setGraphic(nextGraphics[0 + disableOffset]);

                if(!nextButton.isDisabled()){
                    int puzzelNumber = app.puzzelNumber + 1;
                    MainMenu.levelIO.updateUserCode(code, app.puzzelLevel);
                    
                    if(puzzelNumber < MainMenu.levels.size()){
                        PuzzelLevel puzzelLevel = MainMenu.levels.get(puzzelNumber);
                        MainMenu.levelIO.loadFunc(puzzelLevel);
                        app.enqueue(
                            () -> {
                                app.nextLevel(puzzelLevel, puzzelNumber);
                            }
                        );
                    }
                    else{
                        MainMenu.levelIO.writeLastPlayedLevel(app.puzzelLevel.levelName, app.puzzelNumber);
                        app.stop();
                    }
                }
            } 
        );

        terminalBackground = new Rectangle(36, 36);
        terminalBackground.setFill(Color.rgb(60, 60, 60));
        terminalBackground.setArcHeight(10);
        terminalBackground.setArcWidth(10);

        HBox geomBar = intializeGeomBar();

        StackPane pane = new StackPane(terminalBackground, terminal);
        pane.setPadding(new Insets(0, 10, 0, 0));

        topBar.getChildren().addAll(exitButton, nextButton, matchPercentageDisplay, geomBar, spacer, pane);
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

        ImageView cubeImageView = new ImageView(cubeImage);
        ImageView sphereImageView = new ImageView(sphereImage);
        ImageView cylinderImageView = new ImageView(cylinderImage);     

        for(ImageView imageView : List.of(cubeImageView, sphereImageView, cylinderImageView)){
            imageView.setFitWidth(25);
            imageView.setFitHeight(25);
            imageView.setPreserveRatio(true);
        }

        HBox shapeRow = new HBox(3);
        shapeRow.setAlignment(Pos.CENTER);
        shapeRow.setPadding(new Insets(0, 3, 0, 3));

        shapeRow.getChildren().addAll(cubeImageView, createVerticalSperator(1, 20), sphereImageView, createVerticalSperator(1, 20), cylinderImageView);

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
        if(errorDisplay != null) {errorDisplay.setText(error);}
    }

    public void close(){
        if(editorStage != null){
            editorStage.close();
        }
    }

    public void setPuzzelCompleted() {
        nextButton.setDisable(false);
        if(disableOffset == 2) {disableOffset -= 2;}
        nextButton.setGraphic(nextGraphics[0 + disableOffset]);
        matchPercentageDisplay.setFill(Color.rgb(20, 140, 84));
    }
    
    public void reset() {
        codeEditor.replaceText("");
        updateErrorDisplay("");

        if(app.puzzelLevel.completed){
            nextButton.setDisable(false);
            nextButton.setGraphic(nextGraphics[0 + disableOffset]);
        }
        else{
            disableOffset += 2;
            nextButton.setDisable(true);
            nextButton.setGraphic(nextGraphics[0 + disableOffset]);
        }
    }

    public void setCode(String userFunction) {
        code = userFunction;
        codeEditor.replaceText(code);
    }

    public void setMatchingPercentage(double matchPercentage) {
        if(matchPercentageDisplay != null){
            matchPercentageDisplay.setText(String.format("%.2f%%", matchPercentage * 100));
        }
    }

    private HBox intializeGeomBar(){
        HBox geomBar = new HBox();
        geomBar.setMaxSize(125, 20);
        
        textsGeom = new Text[]{
            new Text(),
            new Text(),
            new Text(),
            new Text(),
            new Text()
        };
        
        Color[] colors = new Color[]{
            Color.rgb(63, 145, 119),
            Color.rgb(248, 128, 112),
            Color.rgb(38, 125, 195),
            Color.rgb(65, 65, 65),
            Color.rgb(65, 65, 65)
        };
        
        Font font = Font.font(LevelSelectionUI.font.getFamily(), 16);
        Rectangle background;
        StackPane stack;
        
        for(int i = 0; i < 5; i++){
            textsGeom[i].setFill(Color.WHITE);
            textsGeom[i].setFont(font);
            background = new Rectangle(0, 0, 20, 20);
            background.setFill(colors[i]);
            stack = new StackPane(background, textsGeom[i]);
            stack.setMaxSize(20, 20);
            geomBar.getChildren().add(stack);
        }

        stack = (StackPane)geomBar.getChildren().get(3);
        colorGeom = (Rectangle)stack.getChildren().get(0);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        geomBar.getChildren().add(region);

        shapeGeom = new ImageView();
        shapeGeom.setFitWidth(20);
        shapeGeom.setFitHeight(20);
        shapeGeom.setSmooth(false);
        geomBar.getChildren().add(shapeGeom);

        BackgroundFill backgroundFill = new BackgroundFill(
            Color.rgb(60, 60, 60),
            new CornerRadii(3.0),
            Insets.EMPTY
        );

        geomBar.setBackground(new Background(backgroundFill));

        return geomBar;
    }

    public void updateGeomDisplay(String name) {
        String[] info = name.split("-", 2)[1].split("\\|");

        int color = Integer.parseInt(info[0]);
        
        switch (info[1]) {
            case "Cube":
                shapeGeom.setImage(cubeImage);
                break;
            case "Sphere":
                shapeGeom.setImage(sphereImage);
                break;
            case "Cylinder":
                shapeGeom.setImage(cylinderImage);
                break;
            default:
                break;
        }

        char size = info[2].charAt(0);

        int x = Integer.parseInt(info[3]);
        int y = Integer.parseInt(info[4]);
        int z = Integer.parseInt(info[5]);

        textsGeom[0].setText(x + "");
        textsGeom[1].setText(y + "");
        textsGeom[2].setText(z + "");
        textsGeom[3].setText(color + "");
        colorGeom.setFill(Colors.getColor(color).getFxColor());
        textsGeom[4].setText(size + "");
    }

    public void resetGeomDisplay() {
        shapeGeom.setImage(null);

        textsGeom[0].setText("");
        textsGeom[1].setText("");
        textsGeom[2].setText("");
        textsGeom[3].setText("");
        colorGeom.setFill(Color.rgb(65, 65, 65));
        textsGeom[4].setText("");
    }

    public String getCode(){
        return code;
    }
}