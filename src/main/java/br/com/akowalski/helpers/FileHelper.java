package br.com.akowalski.helpers;

import com.squareup.javapoet.JavaFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class FileHelper {

    public static final String FOUR_WHITESPACES = "    ";
    public static String MAVEN_PATH = "src/main/java";
    public static String TEMPLATE_PATH = "template";
    public static String MAVEN_RESOURCE = "src/main/resources";
    public static String SEPARADOR = "/";

    public static void writeToOutputFile(JavaFile javaFile, String customPath) throws IOException {
        File file = null;
        if (StringUtils.isNotEmpty(customPath)) {
            file = new File(customPath);
            System.out.println("build codes on directory: " + file.getAbsolutePath() + "/" + javaFile.toJavaFileObject().getName());
        } else {
            file = new File(Paths.get(new File(".").getAbsolutePath() + "/src/main/java").toUri());
            System.out.println("build codes on directory: " + file.getAbsolutePath() + "/" + javaFile.toJavaFileObject().getName());
        }
        javaFile.writeTo(file);
    }

}
