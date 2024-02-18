package br.com.akowalski.template;

import br.com.akowalski.generators.MainClassGenerator;
import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.pojos.KcgProject;
import br.com.akowalski.pojos.ProjectFiles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


public class CreateTemplate {

    public static void create(String name, String packageName) {
        var projectName = name.toLowerCase();
        var packagePath = packageName.replaceAll("\\.", "/");
        var projectMaven = name.toLowerCase() + FileHelper.SEPARADOR + FileHelper.MAVEN_PATH;
        var projectResource = name.toLowerCase() + FileHelper.SEPARADOR + FileHelper.MAVEN_RESOURCE;

        try {
            /**
             * Criando app packages
             */
            var packages = List.of(
                    packagePath + FileHelper.SEPARADOR + "enumerates",
                    packagePath + FileHelper.SEPARADOR + "models",
                    packagePath + FileHelper.SEPARADOR + "helpers",
                    packagePath + FileHelper.SEPARADOR + "services",
                    packagePath + FileHelper.SEPARADOR + "requests",
                    packagePath + FileHelper.SEPARADOR + "response",
                    packagePath + FileHelper.SEPARADOR + "resources"
            );

            for (String p : packages) {
                Files.createDirectories(Paths.get(projectMaven.concat(FileHelper.SEPARADOR).concat(p)));
            }

            /**
             * Criando resources
             */

            Files.createDirectory(Path.of(projectResource));

            var ciSettings = new ProjectFiles(
                    "ci_settings.xml",
                    FileHelper.TEMPLATE_PATH,
                    "/",
                    null);

            var gitIgnore = new ProjectFiles(
                    ".gitignore",
                    FileHelper.TEMPLATE_PATH,
                    "/",
                    null);

            var dockerFile = new ProjectFiles(
                    "Dockerfile",
                    FileHelper.TEMPLATE_PATH,
                    "/",
                    null);

            var dockerStack = new ProjectFiles(
                    "docker-stack.yml",
                    FileHelper.TEMPLATE_PATH,
                    "/",
                    null);

            var gitLabCI = new ProjectFiles(
                    ".gitlab-ci.yml",
                    FileHelper.TEMPLATE_PATH,
                    "/",
                    List.of(
                            new ProjectFiles.Tag("APP_NAME", name.toLowerCase()),
                            new ProjectFiles.Tag("CONTEXT", name.toLowerCase())
                    ));

            var pom = new ProjectFiles(
                    "pom.xml",
                    FileHelper.TEMPLATE_PATH,
                    "/",
                    List.of(
                            new ProjectFiles.Tag("GROUP_ID", packageName),
                            new ProjectFiles.Tag("ARTFACT_ID", name.toLowerCase()),
                            new ProjectFiles.Tag("PACKAGE", packageName)
                    ));

            var log4J = new ProjectFiles(
                    "log4j2.xml",
                    FileHelper.TEMPLATE_PATH,
                    FileHelper.MAVEN_RESOURCE,
                    List.of(
                            new ProjectFiles.Tag("APP_PACKAGE", packageName)
                    ));

            var appProperties = new ProjectFiles(
                    "application.properties",
                    FileHelper.TEMPLATE_PATH,
                    FileHelper.MAVEN_RESOURCE,
                    List.of(
                            new ProjectFiles.Tag("CONTEXT", name)
                    ));

            var files = List.of(
                    pom,
                    ciSettings,
                    dockerStack,
                    dockerFile,
                    gitLabCI,
                    gitIgnore,
                    log4J,
                    appProperties
            );

            for (ProjectFiles file : files) {
                var in = CreateTemplate.class.getResourceAsStream(
                        FileHelper.SEPARADOR +
                                file.origin() +
                                FileHelper.SEPARADOR +
                                file.name());

                var path = projectName + FileHelper.SEPARADOR + file.target() + FileHelper.SEPARADOR + file.name();
                Files.copy(in, Paths.get(path));
                var f = new File(path);
                var content = FileUtils.readFileToString(f, "UTF-8");

                if (Objects.nonNull(file.tags())) {
                    for (ProjectFiles.Tag t : file.tags()) {
                        content = StringUtils.replace(content, "{{" + t.name() + "}}", t.value());
                    }
                }

                FileUtils.write(f, content, "UTF-8");
            }


        } catch (IOException e) {
            ExceptionUtils.rethrow(e);
        }

    }

}
