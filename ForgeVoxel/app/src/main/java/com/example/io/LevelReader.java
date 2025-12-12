package com.example.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.Scanner;

import com.example.TextEditor.Interpreter.statements.Statement;

import javafx.scene.image.Image;

public class LevelReader {

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
}