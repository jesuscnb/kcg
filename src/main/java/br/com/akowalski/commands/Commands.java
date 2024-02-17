package br.com.akowalski.commands;

import picocli.CommandLine;

@CommandLine.Command(
        name = "kcg",
        aliases = {"kcg"},
        mixinStandardHelpOptions = true,
        version = "Kowalski Code Generator 1.0.0",
        description = "Generate Default CRUD implementations for Java and MongoDB",
        subcommands = {ProjectCommands.class, ContentCommands.class}
)
public class Commands {

}
