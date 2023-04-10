package br.com.akowalski.commands;


import br.com.akowalski.generators.EntityGenerator;
import br.com.akowalski.generators.ResourceGenerator;
import br.com.akowalski.generators.ServiceGenerator;
import br.com.akowalski.utils.FileUtils;
import com.google.gson.Gson;
import br.com.akowalski.generators.RulesGenerator;
import com.google.gson.reflect.TypeToken;
import com.squareup.javapoet.JavaFile;
import org.apache.commons.lang3.exception.ExceptionUtils;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import br.com.akowalski.pojos.KcgClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

@Command(name = "kcg", aliases = {"kcg"}, mixinStandardHelpOptions = true, version = "Kowalski Code Generator 1.0.0",
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

            List<KcgClass> classes = gson.fromJson(template, new TypeToken<ArrayList<KcgClass>>(){}.getType());

            for (KcgClass classe : classes) {

                /**
                 * Gerando entity
                 */
                if (!exclude.contains(Modulues.E)) {
                    JavaFile contrucEntity = EntityGenerator.init().contruct(classe);
                    FileUtils.writeToOutputFile(contrucEntity, output);
                }

                /**
                 * Gerador service com Abstract
                 */
                if (!exclude.contains(Modulues.S)) {
                    JavaFile constructService = ServiceGenerator.init().construct(classe);
                    FileUtils.writeToOutputFile(constructService, output);
                }

                /**
                 * Gerador de resource
                 */
                if (!exclude.contains(Modulues.C)) {
                    JavaFile construcResource = ResourceGenerator.init().construct(classe);
                    FileUtils.writeToOutputFile(construcResource, output);
                }

                /**
                 * Gerador de rules
                 */
                if (!exclude.contains(Modulues.R)) {
                    JavaFile generateRules = RulesGenerator.init().construct(classe);
                    FileUtils.writeToOutputFile(generateRules, output);
                }

            }

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }

    }
}
