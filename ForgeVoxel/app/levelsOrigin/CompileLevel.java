import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.example.TextEditor.Interpreter.CodeParser;

public class CompileLevel {
    private static String outputPath = "app/levels/";
    public static void main(String[] args) throws Exception{
        try {
            Scanner input = new Scanner(System.in);

            String folderName = input.nextLine();

            File folder = new File(folderName);
            if(folder.exists() && folder.isDirectory()){
                File infoFile = getFile(folder, "info.txt", "info.txt not found in folder " + folderName)[0];
                Scanner fileReader = new Scanner(infoFile);

                String levelName = null;
                int dimensions[] = null;
                File image = null;
                File function = null;
                String[] dataRead;

                dataRead = fileReader.nextLine().split("=");
                if (dataRead[0].equals("name")) {
                    levelName = dataRead[1];
                }
                else{
                    System.out.println("\"name\" not found at 1st line");
                    System.exit(1);
                }

                dataRead = fileReader.nextLine().split("=");
                if (dataRead[0].equals("size")) {
                    dataRead = dataRead[1].split(" ");
                    dimensions = new int[]{
                        Integer.parseInt(dataRead[0]),
                        Integer.parseInt(dataRead[1]),
                        Integer.parseInt(dataRead[2])
                    };
                }
                else{
                    System.out.println("\"size\" not found at 2st line or incorrect");
                    System.exit(1);
                }

                dataRead = fileReader.nextLine().split("=");
                if (dataRead[0].equals("image")) {
                    image = new File(folder + "/" + dataRead[1]);
                    if(!image.exists()){System.out.println("Image not found in folder");}
                }
                else{
                    System.out.println("\"image\" not found at 1st line");
                    System.exit(1);
                }

                dataRead = fileReader.nextLine().split("=");
                if (dataRead[0].equals("function")) {
                    function = new File(folder + "/" + dataRead[1]);
                    if(!function.exists()){System.out.println("Function not found in folder");}
                }
                else{
                    System.out.println("\"function\" not found at 1st line");
                    System.exit(1);
                }

                fileReader.close();

                writeLevel(levelName, dimensions, image, function);
            }
            else{
                System.out.println("Folder " + folder.getName() + " not found or not a folder");
            }

            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static File[] getFile(File folder, String fileName, String errorMessage){
        File[] file = folder.listFiles((File file_, String name) -> name.equals(fileName));

        if(file != null && file.length != 0){
            return file;
        }
        else{
            System.out.println(errorMessage);
            return null;
        }
    }

    public static void writeLevel(String levelName, int dimensions[], File image, File program){

        String outputPath = CompileLevel.outputPath + levelName + ".dat";
        String userFunctionPath = CompileLevel.outputPath + levelName + ".txt";

        try{
            File userFunctonFile = new File(userFunctionPath);
            userFunctonFile.createNewFile();
            
            FileWriter userFile = new FileWriter(userFunctionPath);

            userFile.write("false\n");
            userFile.close();

            File outputFile = new File(outputPath);
            outputFile.createNewFile();

            DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(outputFile));
            outputStream.writeUTF(levelName);

            outputStream.writeInt(dimensions[0]);
            outputStream.writeInt(dimensions[1]);
            outputStream.writeInt(dimensions[2]);

            byte[] imageArray = readFileAsByte(image);
            outputStream.writeInt(imageArray.length);
            outputStream.write(imageArray);

            ByteArrayOutputStream funcOutBAOS = new ByteArrayOutputStream();
            ObjectOutputStream funcOutOOS = new ObjectOutputStream(funcOutBAOS);
            funcOutOOS.writeObject(CodeParser.compileProgram(program));
            funcOutOOS.flush();
            byte[] astByteArray = funcOutBAOS.toByteArray();
            outputStream.writeInt(astByteArray.length);
            outputStream.write(astByteArray);

            outputStream.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private static byte[] readFileAsByte(File file) throws FileNotFoundException, IOException{
        byte[] byteArray = new byte[(int)file.length()];

        FileInputStream fileInputStream = new FileInputStream(file);
        fileInputStream.read(byteArray);
        fileInputStream.close();

        return byteArray;
    }
}
