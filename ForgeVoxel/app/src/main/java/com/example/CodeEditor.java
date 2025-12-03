// package com.example;

// import com.jme3.system.awt.AwtPanel;
// import com.jme3.system.awt.AwtPanelsContext;
// import com.jme3.system.awt.PaintMode;
// import com.jme3.app.SimpleApplication;
// import com.jme3.system.AppSettings;
// import com.jme3.system.JmeContext;

// import javafx.application.Application;
// import javafx.embed.swing.SwingNode;
// import javafx.scene.Scene;
// import javafx.scene.layout.BorderPane;
// import javafx.stage.Stage;

// import javax.swing.*;

// public class CodeEditor extends Application {

//     private App jmeApp;

//     @Override
//     public void start(Stage primaryStage) {
//         BorderPane root = new BorderPane();

//         SwingNode swingNode = new SwingNode();
//         createJmeCanvas(swingNode);

//         root.setCenter(swingNode);

//         primaryStage.setTitle("JavaFX + JME Canvas Example");
//         primaryStage.setScene(new Scene(root, 800, 600));
//         primaryStage.show();
//     }

//     private void createJmeCanvas(SwingNode swingNode) {
//         // Run JME in its own thread
//         new Thread(() -> {
//             AppSettings settings = new AppSettings(true);
//             settings.setCustomRenderer(com.jme3.system.awt.AwtPanelsContext.class);
//             settings.setUseInput(true);

//             jmeApp = new App();
//             jmeApp.setSettings(settings);
//             jmeApp.setShowSettings(false);
//             jmeApp.start(JmeContext.Type.Canvas);

//             jmeApp.start(JmeContext.Type.Canvas);

//             // 4. Wait for context to become available
//             AwtPanelsContext ctx = null;
//             while (ctx == null) {
//                 ctx = (AwtPanelsContext) jmeApp.getContext();
//                 try { Thread.sleep(10); } catch (Exception ignored) {}
//             }

//             // Create a panel with the correct PaintMode
//             AwtPanel panel = ctx.createPanel(PaintMode.Accelerated);

//             // Attach as the main panel
//             ctx.setMainPanel(panel);

//             // Convert to Swing component
//             JPanel jmePanel = panel.getComponent();

//             // 7. Attach to JavaFX via SwingNode
//             SwingUtilities.invokeLater(() ->
//                 swingNode.setContent(jmePanel)
//             );

//         }).start();
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }