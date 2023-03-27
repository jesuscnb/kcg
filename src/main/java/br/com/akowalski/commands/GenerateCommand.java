package br.com.akowalski.commands;


import br.com.akowalski.utils.FileUtils;
import com.google.gson.Gson;
import br.com.akowalski.generators.ClassGeneration;
import br.com.akowalski.generators.RulesGenerator;
import com.squareup.javapoet.JavaFile;
import org.apache.commons.lang3.exception.ExceptionUtils;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import br.com.akowalski.pojos.DevPoolClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Command(name = "kcode", aliases = {"kc"}, mixinStandardHelpOptions = true, version = "Kowalski Code Generator 1.0.0",
        description = "Generate Default CRUD implementations for Java and MongoDB")
public class GenerateCommand implements Runnable {

    static class TemplateInput {
        @Option(names = {"-j", "--json"}, required = true, paramLabel = "JSON", description = "Json template")
        String json;

        @Option(names = {"-f", "--file"}, required = true, paramLabel = "ARCHIVE", description = "Json archive file path")
        File archive;

    }

    enum Modulues {
        C, S, R, E
    }

    @ArgGroup(multiplicity = "1")
    TemplateInput args;

    @Option(names = {"-o", "--output"}, paramLabel = "GENERATE PATH", description = "Location to save generated code")
    String output;

    @Option(names = {"-e", "--exclude"}, split = ",", paramLabel = "EXCLUDE LAYERS", description = "Add modules to skip generated")
    List<Modulues> exclude = new ArrayList<>();


    @Override
    public void run() {

        try {
            String template = "";
            Gson gson = new Gson();

            if (Objects.nonNull(args.archive)) {
                template = new Scanner(args.archive).useDelimiter("\\Z").next();
            } else if (Objects.nonNull(args.json)) {
                template = args.json;
            }


            DevPoolClass classe = gson.fromJson(template, DevPoolClass.class);
            ClassGeneration classGenerator = ClassGeneration.init();

            /**
             * Gerando entity
             */
            if (!exclude.contains(Modulues.E)) {
                JavaFile contrucEntity = classGenerator.contrucEntity(classe);
                FileUtils.writeToOutputFile(contrucEntity, output);
            }

            /**
             * Gerador service com Abstract
             */
            if (!exclude.contains(Modulues.S)) {
                JavaFile constructService = classGenerator.constructService(classe);
                FileUtils.writeToOutputFile(constructService, output);
            }

            /**
             * Gerador de resource
             */
            if (!exclude.contains(Modulues.C)) {
                JavaFile construcResource = classGenerator.constructResource(classe);
                FileUtils.writeToOutputFile(construcResource, output);
            }

            /**
             * Gerador de rules
             */
            if (!exclude.contains(Modulues.R)) {
                JavaFile generateRules = RulesGenerator.init().run(classe);
                FileUtils.writeToOutputFile(generateRules, output);
            }

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }

    }
}
