package br.com.akowalski.commands;

import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.pojos.KcgContent;
import br.com.akowalski.pojos.KcgProject;
import br.com.akowalski.template.CreateContent;
import br.com.akowalski.template.CreateTemplate;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.util.Objects;

@CommandLine.Command(name = "init", description = "Create project with defaults", subcommands = {ContentCommands.class})
public class ProjectCommands extends CommonCommands implements Runnable {

    @CommandLine.Option(names = {"-n", "--name"}, defaultValue = "demo-app")
    String name;

    @CommandLine.Option(names = {"-p", "--package"}, defaultValue = "br.com.example")
    String packageName;

    @CommandLine.ArgGroup(multiplicity = "1")
    TemplateInput args;


    @Override
    public void run() {
        CreateTemplate.create(new KcgProject(name, packageName));
        if (StringUtils.isNotEmpty(args.json) || Objects.nonNull(args.archive)) {
            CreateContent.create(
                    new KcgContent(
                            args.json,
                            args.archive,
                            exclude,
                            output + "/" + FileHelper.MAVEN_PATH)
            );
        }

    }
}
