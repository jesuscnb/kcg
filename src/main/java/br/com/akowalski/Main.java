package br.com.akowalski;

import br.com.akowalski.commands.GenerateCommand;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        new CommandLine(new GenerateCommand()).execute(args);
    }

}
