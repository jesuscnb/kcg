package br.com.akowalski.template;

import br.com.akowalski.generators.MainClassGenerator;
import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.pojos.KcgClass;
import br.com.akowalski.pojos.ProjectFiles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


public class CreateTemplate {
	public static String MAVEN_PATH = "src/main/java";
	public static String TEMPLATE_PATH = "template";
	public static String MAVEN_RESOURCE = "src/main/resources";
	public static String SEPARADOR = "/";

	public static void create(KcgClass kcgClass) {
		var projectName = kcgClass.name().toLowerCase();
		var packagePath = kcgClass.packageName().replaceAll("\\.", "/");
		var projectMaven = kcgClass.name().toLowerCase() + SEPARADOR + MAVEN_PATH;
		var projectResource = kcgClass.name().toLowerCase() + SEPARADOR + MAVEN_RESOURCE;

		try {
			/**
			 * Criando app packages
			 */
			var packages = List.of(
					packagePath + SEPARADOR + "enumerates",
					packagePath + SEPARADOR + "models",
					packagePath + SEPARADOR + "helpers",
					packagePath + SEPARADOR + "service",
					packagePath + SEPARADOR + "request",
					packagePath + SEPARADOR + "response",
					packagePath + SEPARADOR + "resources"
			);

			for (String p : packages) {
				Files.createDirectories(Paths.get(projectMaven.concat(SEPARADOR).concat(p)));
			}

			/**
			 * Criando resources
			 */

			Files.createDirectory(Path.of(projectResource));

			var ciSettings = new ProjectFiles(
					"ci_settings.xml",
					TEMPLATE_PATH,
					"/",
					null);

			var gitIgnore = new ProjectFiles(
					".gitignore",
					TEMPLATE_PATH,
					"/",
					null);

			var dockerFile = new ProjectFiles(
					"Dockerfile",
					TEMPLATE_PATH,
					"/",
					null);

			var dockerStack = new ProjectFiles(
					"docker-stack.yml",
					TEMPLATE_PATH,
					"/",
					null);

			var gitLabCI = new ProjectFiles(
					".gitlab-ci.yml",
					TEMPLATE_PATH,
					"/",
					List.of(
							new ProjectFiles.Tag("APP_NAME", kcgClass.name().toLowerCase()),
							new ProjectFiles.Tag("CONTEXT", kcgClass.name().toLowerCase())
					));

			var pom = new ProjectFiles(
					"pom.xml",
					TEMPLATE_PATH,
					"/",
					List.of(
							new ProjectFiles.Tag("GROUP_ID", kcgClass.name().toLowerCase()),
							new ProjectFiles.Tag("ARTFACT_ID", kcgClass.packageName()),
							new ProjectFiles.Tag("PACKAGE", kcgClass.packageName())
					));

			var log4J = new ProjectFiles(
					"log4j2.xml",
					TEMPLATE_PATH,
					MAVEN_RESOURCE,
					List.of(
							new ProjectFiles.Tag("APP_PACKAGE", kcgClass.packageName())
					));

			var appProperties = new ProjectFiles(
					"application.properties",
					TEMPLATE_PATH,
					MAVEN_RESOURCE,
					List.of(
							new ProjectFiles.Tag("CONTEXT", kcgClass.name())
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
				InputStream in = CreateTemplate.class.getResourceAsStream(SEPARADOR + file.origin() + SEPARADOR + file.name());
				String path = projectName + SEPARADOR + file.target() + SEPARADOR + file.name();
				Files.copy(in, Paths.get(path));
				File f = new File(path);
				String content = FileUtils.readFileToString(f, "UTF-8");

				if (Objects.nonNull(file.tags())) {
					for (ProjectFiles.Tag t : file.tags()) {
						content = StringUtils.replace(content, "{{" + t.name() + "}}", t.value());
					}
				}
				FileUtils.write(f, content, "UTF-8");
			}

			/**
			 * Criando Main class
			 */
			var javaFile = MainClassGenerator.init().construct(kcgClass);
			FileHelper.writeToOutputFile(javaFile, projectMaven);


		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] args) {
		KcgClass kcgClass = new KcgClass(
				"api-akowalski",
				null,
				null,
				"br.com.kowalski",
				null
		);
		CreateTemplate.create(kcgClass);
	}

}
