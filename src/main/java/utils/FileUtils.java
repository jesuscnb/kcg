package utils;

import com.squareup.javapoet.JavaFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class FileUtils {

    public static void writeToOutputFile(JavaFile javaFile) throws IOException {
        javaFile.writeTo(new File(Paths.get(new File(".").getAbsolutePath() + "/gensrc").toUri()));
    }


}
