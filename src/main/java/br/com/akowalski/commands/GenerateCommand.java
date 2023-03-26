package br.com.akowalski.commands;


import com.google.gson.Gson;
import br.com.akowalski.generators.ClassGeneration;
import br.com.akowalski.generators.RulesGenerator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import br.com.akowalski.pojos.DevPoolClass;

import java.io.File;
import java.util.Objects;
import java.util.Scanner;

@Command(name = "generate", aliases = {"g"}, mixinStandardHelpOptions = true, version = "Dev Pool Generator 1.0.0",
        description = "Generate Default CRUD implementations for Java and MongoDB")
public class GenerateCommand implements Runnable {

    static class Args {
        @Option(names = {"-j", "--json"}, required = true, paramLabel = "JSON", description = "Json template")
        String json;

        @Option(names = {"-f", "--file"}, required = true, paramLabel = "ARCHIVE", description = "Json archive file path")
        File archive;

    }

    @ArgGroup(multiplicity = "1")
    Args args;

    @Override
    public void run() {

        try {
            String template = "";
            Gson gson = new Gson();

            if (Objects.nonNull(this.args.archive)) {
                template = new Scanner(this.args.archive).useDelimiter("\\Z").next();
            } else if (Objects.nonNull(this.args.json)) {
                template = this.args.json;
            }

            if (StringUtils.isEmpty(template)) {
                throw new Exception("Required --file or --json");
            }

            DevPoolClass classe = gson.fromJson(template, DevPoolClass.class);
            ClassGeneration classGenerator = ClassGeneration.init();

            /**
             * Gerando entity
             */
            classGenerator.contrucEntity(classe);


            /**
             * Gerador service com Abstract
             */
            classGenerator.constructService(classe);

            /**
             * Gerador de resource
             */
            classGenerator.constructResource(classe);

            /**
             * Gerador de rules
             */
            RulesGenerator.init().run(classe);

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }

    }
}
