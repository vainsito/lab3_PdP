package utils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class FileMaker {
    public static void createFile(String path) {
        File archivo = new File(path);
        try{
            // Crear el directorio si no existe
            archivo.getParentFile().mkdirs();
            if (!archivo.exists()) {
                archivo.createNewFile();
            } else {
                // esto trunca el tamaño del archivo a 0, borra todo lo de adentro
                FileWriter fileWriter = new FileWriter(archivo);
            }
        } catch (IOException e) {
            System.err.println("An error while creating the file");
            e.printStackTrace();
        }
    }
}
