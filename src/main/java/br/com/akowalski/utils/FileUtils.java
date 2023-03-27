package br.com.akowalski.utils;

import com.squareup.javapoet.JavaFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class FileUtils {

    public static void writeToOutputFile(JavaFile javaFile, String customPath) throws IOException {

        if (StringUtils.isNotEmpty(customPath)) {
            javaFile.writeTo(new File(customPath));
        } else {
            javaFile.writeTo(new File(Paths.get(new File(".").getAbsolutePath() + "/src/main/java").toUri()));
        }
    }

}
