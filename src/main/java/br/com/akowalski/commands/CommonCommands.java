package br.com.akowalski.commands;

import br.com.akowalski.requests.Modules;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommonCommands {

    @CommandLine.Option(names = {"-o", "--output"}, paramLabel = "GENERATE PATH", description = "Location to save generated code")
    String output;

    static class TemplateInput {
        @CommandLine.Option(names = {"-j", "--json"}, required = true, paramLabel = "JSON", description = "Json template")
        String json;

        @CommandLine.Option(names = {"-f", "--file"}, required = true, paramLabel = "ARCHIVE", description = "Json archive file path")
        File archive;
    }

    @CommandLine.Option(names = {"-e", "--exclude"}, split = ",", paramLabel = "EXCLUDE LAYERS", description = "Add modules to skip generated")
    List<Modules> exclude = new ArrayList<>();

}
