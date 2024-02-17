package br.com.akowalski;


import br.com.akowalski.commands.Commands;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        new CommandLine(new Commands()).execute(args);
    }

}
