/*
@Time    : 2023/9/29 9:28
@Author  : Elaikona
*/
package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHelper {
    private FileHelper() {
    }

    public static String fileToString(String path) {
        try {
            return Files.readString(Path.of(path));
        } catch (IOException e) {
            System.out.println(e.toString());
            return "";
        }
    }

    public static void writeToFile(String path, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
