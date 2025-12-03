// package com.example;

// import com.jme3.app.SimpleApplication;
// import com.jme3.input.KeyInput;
// import com.jme3.input.controls.ActionListener;
// import com.jme3.input.controls.KeyTrigger;
// import com.jme3.material.Material;
// import com.jme3.math.ColorRGBA;
// import com.jme3.math.Vector3f;
// import com.simsilica.lemur.*;
// import com.simsilica.lemur.component.SpringGridLayout;

// import java.util.*;


// /**
//  * Full-featured Lemur Code Editor:
//  * - Typing
//  * - Code suggestions
//  * - Line numbers
//  * - Syntax highlighting (customizable)
//  * - Scrollable text area
//  * - Cursor highlighting
//  */
// public class LemurCodeEditor {

//     private Container rootPanel;
//     private Container lineNumberPanel;
//     private Container textPanel;
//     private ListBox scrollPanel;
//     private Container suggestionPanel;

//     private List<List<Label>> codeLabels;
//     private int cursorLine = 0;
//     private int cursorPos = 0;

//     private Map<String, ColorRGBA> syntaxColors;
//     private List<String> keywords;
//     private int selectedSuggestion = -1;

//     private SimpleApplication app;

//     public LemurCodeEditor(SimpleApplication app, int width, int height) {
//         this.app = app;

//         rootPanel = new Container();
//         rootPanel.setPreferredSize(new Vector3f(width, height, 0));

//         lineNumberPanel = new Container();
//         lineNumberPanel.setBackgroundColor(ColorRGBA.Gray);
//         lineNumberPanel.setLayout(new SpringGridLayout(Axis.Y, Axis.X));
//         rootPanel.addChild(lineNumberPanel);

//         textPanel = new Container();
//         textPanel.setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Even));

//         Material textMaterial = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//         textMater
//         textPanel.setMaterial();

//         scrollPanel = new ScrollPanel(textPanel);
//         scrollPanel.setPreferredSize(width - 50, height); // space for line numbers
//         rootPanel.addChild(scrollPanel);

//         suggestionPanel = new Container();
//         suggestionPanel.setBackgroundColor(ColorRGBA.Gray);
//         suggestionPanel.setVisible(false);
//         rootPanel.addChild(suggestionPanel);

//         codeLabels = new ArrayList<>();
//         syntaxColors = new HashMap<>();
//         keywords = new ArrayList<>(Arrays.asList(
//                 "int","float","double","if","else","for","while","return","class","public","private","void"));

//         // default colors
//         syntaxColors.put("keyword", ColorRGBA.Blue);
//         syntaxColors.put("number", ColorRGBA.Orange);
//         syntaxColors.put("string", ColorRGBA.Green);
//         syntaxColors.put("default", ColorRGBA.White);
//         syntaxColors.put("cursor", ColorRGBA.Yellow);

//         // initial content
//         addLine("public class Test {");
//         addLine("    int x = 10;");
//         addLine("}");

//         app.getGuiNode().attachChild(rootPanel);

//         setupInput();
//         highlightCursor();
//     }

//     /** Add a line */
//     public void addLine(String code) {
//         int lineNumber = codeLabels.size() + 1;

//         Label lnLabel = new Label(String.valueOf(lineNumber));
//         lnLabel.setColor(ColorRGBA.White);
//         lineNumberPanel.addChild(lnLabel);

//         String[] tokens = code.split("(?=\\W)|(?<=\\W)");
//         List<Label> tokenLabels = new ArrayList<>();
//         Container lineContainer = new Container();
//         float xPos = 0;
//         for (String token : tokens) {
//             if (token.trim().isEmpty()) { xPos += 6; continue; }
//             Label lbl = new Label(token);
//             lbl.setColor(getTokenColor(token));
//             lbl.setLocalTranslation(xPos, 0, 0);
//             lineContainer.addChild(lbl);
//             tokenLabels.add(lbl);
//             xPos += lbl.getPreferredSize().x;
//         }
//         textPanel.addChild(lineContainer);
//         codeLabels.add(tokenLabels);
//     }

//     /** Determine color for token */
//     private ColorRGBA getTokenColor(String token) {
//         if (keywords.contains(token)) return syntaxColors.getOrDefault("keyword", ColorRGBA.Blue);
//         else if (token.matches("\\d+")) return syntaxColors.getOrDefault("number", ColorRGBA.Orange);
//         else if (token.startsWith("\"") && token.endsWith("\"")) return syntaxColors.getOrDefault("string", ColorRGBA.Green);
//         else return syntaxColors.getOrDefault("default", ColorRGBA.White);
//     }

//     /** Highlight the cursor */
//     private void highlightCursor() {
//         for(List<Label> line: codeLabels){
//             for(Label lbl: line){
//                 lbl.setColor(getTokenColor(lbl.getText()));
//             }
//         }
//         if(!codeLabels.isEmpty() && !codeLabels.get(cursorLine).isEmpty()){
//             Label cursorLabel = codeLabels.get(cursorLine).get(Math.min(cursorPos, codeLabels.get(cursorLine).size() - 1));
//             cursorLabel.setColor(syntaxColors.getOrDefault("cursor", ColorRGBA.Yellow));
//         }
//     }

//     /** Input setup */
//     private void setupInput() {
//         // Arrow keys + enter + backspace
//         String[] mappings = {"Up","Down","Left","Right","Backspace","Enter"};
//         app.getInputManager().addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
//         app.getInputManager().addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
//         app.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
//         app.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
//         app.getInputManager().addMapping("Backspace", new KeyTrigger(KeyInput.KEY_BACK));
//         app.getInputManager().addMapping("Enter", new KeyTrigger(KeyInput.KEY_RETURN));

//         app.getInputManager().addListener((ActionListener) (name, isPressed, tpf) -> {
//             if (!isPressed) return;
//             switch (name) {
//                 case "Up": if(selectedSuggestion>0) selectedSuggestion--; break;
//                 case "Down": if(selectedSuggestion<suggestionPanel.getChildren().size()-1) selectedSuggestion++; break;
//                 case "Left": if(cursorPos>0) cursorPos--; break;
//                 case "Right": if(cursorPos<codeLabels.get(cursorLine).size()-1) cursorPos++; break;
//                 case "Backspace": deleteChar(); break;
//                 case "Enter": if(selectedSuggestion>=0) applySuggestion(); else insertNewLine(); break;
//             }
//             highlightCursor();
//             updateSuggestions();
//         }, mappings);

//         // Typing letters/numbers/space
//         for(int i=KeyInput.KEY_SPACE;i<=KeyInput.KEY_Z;i++){
//             final int key = i;
//             app.getInputManager().addMapping("Type"+i,new KeyTrigger(key));
//             app.getInputManager().addListener((name, isPressed, tpf)->{
//                 if(!isPressed) return;
//                 char c = keyToChar(key);
//                 if(c!='\0') insertChar(String.valueOf(c));
//             },"Type"+i);
//         }
//     }

//     private char keyToChar(int key){
//         if(key>=KeyInput.KEY_A && key<=KeyInput.KEY_Z) return (char)('a'+key-KeyInput.KEY_A);
//         if(key>=KeyInput.KEY_0 && key<=KeyInput.KEY_9) return (char)('0'+key-KeyInput.KEY_0);
//         if(key==KeyInput.KEY_SPACE) return ' ';
//         return '\0';
//     }

//     private void insertChar(String s){
//         if(codeLabels.isEmpty()) return;
//         List<Label> line = codeLabels.get(cursorLine);
//         Label lbl = new Label(s);
//         lbl.setColor(getTokenColor(s));
//         line.add(cursorPos, lbl);
//         Container lineContainer = (Container) textPanel.getChild(cursorLine);
//         lineContainer.addChild(lbl, cursorPos);
//         cursorPos++;
//         relayoutLine(cursorLine);
//         highlightCursor();
//         updateSuggestions();
//     }

//     private void deleteChar(){
//         if(codeLabels.isEmpty()) return;
//         List<Label> line = codeLabels.get(cursorLine);
//         if(line.isEmpty() || cursorPos==0) return;
//         Label lbl = line.remove(cursorPos-1);
//         Container lineContainer = (Container) textPanel.getChild(cursorLine);
//         lineContainer.removeChild(lbl);
//         cursorPos--;
//         relayoutLine(cursorLine);
//     }

//     private void insertNewLine(){
//         List<Label> newLine = new ArrayList<>();
//         codeLabels.add(cursorLine+1,newLine);
//         Container newLineContainer = new Container();
//         textPanel.addChild(newLineContainer,cursorLine+1);
//         cursorLine++;
//         cursorPos=0;

//         // Update line numbers
//         lineNumberPanel.detachAllChildren();
//         for(int i=1;i<=codeLabels.size();i++){
//             Label lnLabel = new Label(String.valueOf(i));
//             lnLabel.setColor(ColorRGBA.White);
//             lineNumberPanel.addChild(lnLabel);
//         }
//         highlightCursor();
//     }

//     private void relayoutLine(int lineIndex){
//         List<Label> line = codeLabels.get(lineIndex);
//         float xPos=0;
//         for(Label lbl: line){
//             lbl.setLocalTranslation(xPos,0,0);
//             xPos+=lbl.getPreferredSize().x;
//         }
//     }

//     /** Update suggestions */
//     private void updateSuggestions(){
//         suggestionPanel.detachAllChildren();
//         selectedSuggestion=-1;
//         if(codeLabels.isEmpty()) return;

//         String token="";
//         List<Label> line = codeLabels.get(cursorLine);
//         if(!line.isEmpty()) token = line.get(Math.min(cursorPos,line.size()-1)).getText();

//         List<String> matches = new ArrayList<>();
//         for(String kw: keywords){
//             if(kw.startsWith(token) && !kw.equals(token)) matches.add(kw);
//         }
//         if(matches.isEmpty()){ suggestionPanel.setVisible(false); return; }

//         suggestionPanel.setVisible(true);
//         float yPos=0;
//         for(String m:matches){
//             Label lbl=new Label(m);
//             lbl.setColor(ColorRGBA.White);
//             lbl.setLocalTranslation(0,yPos,0);
//             suggestionPanel.addChild(lbl);
//             yPos-=20;
//         }
//     }

//     private void applySuggestion(){
//         if(selectedSuggestion<0 || selectedSuggestion>=suggestionPanel.getChildren().size()) return;
//         Label sug = (Label)suggestionPanel.getChild(selectedSuggestion);
//         insertChar(sug.getText());
//         suggestionPanel.setVisible(false);
//     }

//     /** Public API */
//     public void setSyntaxColor(String type, ColorRGBA color){ syntaxColors.put(type,color);}
//     public void setKeywords(Collection<String> keys){ keywords.clear(); keywords.addAll(keys);}
//     public Container getPanel(){ return rootPanel;}
// }