package br.com.akowalski.commands;

import br.com.akowalski.pojos.KcgContent;
import br.com.akowalski.requests.Modules;
import br.com.akowalski.template.CreateContent;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CommandLine.Command(name = "g", aliases = {"-g", "--generator"}, description = "Create content by template")
public class ContentCommands extends CommonCommands implements Runnable {



    @CommandLine.ArgGroup(multiplicity = "1")
    TemplateInput args;

    @Override
    public void run() {
        CreateContent.create(new KcgContent(args.json, args.archive, exclude, output));
    }

}
