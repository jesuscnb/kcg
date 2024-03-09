package br.com.akowalski.commands;

import br.com.akowalski.generators.MainClassGenerator;
import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.helpers.KcgHelper;
import br.com.akowalski.pojos.KcgContent;
import br.com.akowalski.requests.Modules;
import br.com.akowalski.template.CreateContent;
import br.com.akowalski.template.CreateTemplate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import picocli.CommandLine;

import java.io.IOException;
import java.util.List;

@CommandLine.Command(name = "init", description = "Create project with defaults", subcommands = {ContentCommands.class})
public class ProjectCommands extends CommonCommands implements Runnable {

    @CommandLine.Option(names = {"-n", "--name"}, defaultValue = "demo-app")
    String name;

    @CommandLine.ArgGroup(multiplicity = "1")
    TemplateInput args;


    @Override
    public void run() {
        CreateTemplate.create(name, packageName);
        var classes = KcgHelper.parse(args.json, args.archive);

        if (!classes.isEmpty()) {
            CreateContent.create(
                    new KcgContent(
                            classes,
                            packageName,
                            exclude,
                            output + "/" + FileHelper.MAVEN_PATH));
        }

        /**
         * Criando Main class
         */
        try {
            List<Pair<String, String>> resources = null;
            if (!exclude.contains(Modules.C)) {
                resources = classes.parallelStream()
                        .map(s -> Pair.of(packageName.concat(".resources"), s.name().concat("Resource")))
                        .toList();
            }
            var projectMaven = name.toLowerCase() + FileHelper.SEPARADOR + FileHelper.MAVEN_PATH;
            var javaFile = MainClassGenerator.init().construct(FileHelper.MAIN_CLASS_NAME, packageName, resources);

            FileHelper.writeToOutputFile(javaFile, projectMaven);
        } catch (IOException e) {
            ExceptionUtils.rethrow(e);
        }

    }
}
