package com.example.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.MainMenu;
import com.example.TextEditor.Interpreter.statements.Statement;

import javafx.scene.image.Image;

public class LevelIO {
    private static final String LAST_PLAYED_LEVEL = System.getProperty("user.dir") + "/levels/lastPlayedLevel.dat";
    private static final String LEVELS_FOLDER = System.getProperty("user.dir") + "/levels";
    private String lastPlayedLevelName;
    private int lastPlayedLevelIndex = -1;

    public List<PuzzelLevel> readPuzzels(boolean readMetaData){
        File levelsFolder = new File(LEVELS_FOLDER);
        File levelsArray[] = levelsFolder.listFiles((File file, String name) -> name.endsWith(".dat"));
        ArrayList<PuzzelLevel> levels = new ArrayList<>(levelsArray.length);

        lastPlayedLevelName = readLastPlayedLevelName();

        File level;
        for (int i = 1; i < levelsArray.length; i++) {
            level = levelsArray[i];

            if(lastPlayedLevelName != null && level.getPath().endsWith("\\" + lastPlayedLevelName + ".dat")){
                lastPlayedLevelIndex = i - 1;
            }

            levels.add(readLevel(level.getPath(), readMetaData));
        }
        
        return levels;
    }

    public PuzzelLevel readLevel(String levelPath, boolean metadDataOnly){
        try {
            File levelFile = new File(levelPath);
            FileInputStream fileInputStream = new FileInputStream(levelFile);
            DataInputStream inputStream = new DataInputStream(fileInputStream);

            File userFile = new File(levelPath.replace(".dat", ".txt"));
            Scanner scanner = new Scanner(userFile);

            String s = scanner.nextLine();
            boolean completed = Boolean.parseBoolean(s.trim());

            scanner.close();

            String levelName = inputStream.readUTF();
            
            int width = inputStream.readInt();
            int height = inputStream.readInt();
            int depth = inputStream.readInt();

            int imageLength = inputStream.readInt();
            byte[] imageArray = new byte[imageLength];
            inputStream.readFully(imageArray);
            Image image = new Image(new ByteArrayInputStream(imageArray));

            if(metadDataOnly){
                inputStream.close();
                return new PuzzelLevel(levelName, width, height, depth, image, null, null, levelFile.getPath(), completed);
            }

            int funcLength = inputStream.readInt();
            byte[] funcByteArray = new byte[funcLength];
            inputStream.read(funcByteArray);
            ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(funcByteArray));
            Statement func = (Statement) objectInput.readObject();

            inputStream.close();

            return new PuzzelLevel(levelName, width, height, depth, image, func, null, levelFile.getPath(), completed);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void loadFunc(PuzzelLevel puzzelLevel){
        try {
            File levelFile = new File(puzzelLevel.filePath);
            FileInputStream fileInputStream = new FileInputStream(levelFile);
            DataInputStream inputStream = new DataInputStream(fileInputStream);

            inputStream.readUTF();
            inputStream.readInt();
            inputStream.readInt();
            inputStream.readInt();

            int imageLength = inputStream.readInt();
            inputStream.skip(imageLength);

            int funcLength = inputStream.readInt();
            byte[] funcByteArray = new byte[funcLength];
            inputStream.read(funcByteArray);
            ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(funcByteArray));
            puzzelLevel.targetFunction = (Statement) objectInput.readObject();
            inputStream.close();

            File userFunctionFil = new File(puzzelLevel.filePath.replace(".dat", ".txt"));
            Scanner userFuncScanner = new Scanner(userFunctionFil);
            StringBuilder userFunction = new StringBuilder();

            userFuncScanner.nextLine();
            while (userFuncScanner.hasNextLine()) {
                String s = userFuncScanner.nextLine();
                userFunction.append(s).append("\n");
            }

            puzzelLevel.userFunction = userFunction.toString();
            userFuncScanner.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCompletion(boolean complete, PuzzelLevel puzzelLevel){
        try {
            File outputFile = new File(puzzelLevel.filePath.replace(".dat", ".txt"));
            FileWriter outputStream = new FileWriter(outputFile);
            outputStream.write(complete ? "true " : "false");
            outputStream.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserCode(String code, PuzzelLevel puzzelLevel){ 
        try {
            File outputFile = new File(puzzelLevel.filePath.replace(".dat", ".txt"));
            FileWriter outputStream = new FileWriter(outputFile);

            outputStream.write(puzzelLevel.completed ? "true " : "false");
            outputStream.write("\n");
            outputStream.write(code == null ? "" : code);
            outputStream.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLastPlayedLevel(String name, int index){
        lastPlayedLevelIndex = index;
        try {
            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(LAST_PLAYED_LEVEL)));
            outputStream.writeUTF(name);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PuzzelLevel readLastPlayedLevel(boolean metadDataOnly){
        PuzzelLevel puzzelLevel = null;

        if(lastPlayedLevelIndex != -1){
            puzzelLevel = MainMenu.levels.get(lastPlayedLevelIndex);

            if(metadDataOnly) return puzzelLevel;
            if(puzzelLevel.targetFunction != null) return puzzelLevel;
            else{
                loadFunc(puzzelLevel);
                return puzzelLevel;
            }
        }
        else{
            if(lastPlayedLevelName != null){
                return readLevel(LEVELS_FOLDER + "/" + lastPlayedLevelName + ".dat", metadDataOnly);
            }
            else{
                return null;
            }
        }
    }

    private String readLastPlayedLevelName(){
        try {
            DataInputStream inputStream = new DataInputStream(new FileInputStream(new File(LAST_PLAYED_LEVEL)));
            String lastPlayedLevelName = inputStream.readUTF();
            inputStream.close();

            return lastPlayedLevelName;
        } 
        catch (IOException e) {
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLastPlayedLevelIndex() {
        return lastPlayedLevelIndex;
    }
}