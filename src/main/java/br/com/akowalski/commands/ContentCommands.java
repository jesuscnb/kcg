package br.com.akowalski.commands;

import br.com.akowalski.helpers.KcgHelper;
import br.com.akowalski.pojos.KcgContent;
import br.com.akowalski.template.CreateContent;
import picocli.CommandLine;

@CommandLine.Command(name = "g", aliases = {"-g", "--generator"}, description = "Create content by template")
public class ContentCommands extends CommonCommands implements Runnable {

    @CommandLine.ArgGroup(multiplicity = "1")
    TemplateInput args;

    @Override
    public void run() {
        var classes = KcgHelper.parse(args.json, args.archive);
        CreateContent.create(new KcgContent(classes, exclude, output));
    }

}
