package br.com.akowalski.template;

import br.com.akowalski.generators.EntityGenerator;
import br.com.akowalski.generators.ResourceGenerator;
import br.com.akowalski.generators.RulesGenerator;
import br.com.akowalski.generators.ServiceGenerator;
import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.helpers.KcgHelper;
import br.com.akowalski.pojos.KcgClass;
import br.com.akowalski.pojos.KcgContent;
import br.com.akowalski.requests.Modules;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class CreateContent {

    public static void create(KcgContent content) {
        try {
            AtomicReference<String> template = new AtomicReference<>();

            if (Objects.nonNull(content.archive())) {
                template.set(new Scanner(content.archive()).useDelimiter("\\Z").next());
            } else if (Objects.nonNull(content.json())) {
                template.set(content.json());
            }

            var classes = KcgHelper.parse(template.get());

            for (KcgClass classe : classes) {

                /**
                 * Gerando entity
                 */
                if (!content.exclude().contains(Modules.E)) {
                    var contrucEntity = EntityGenerator.init().contruct(classe);
                    FileHelper.writeToOutputFile(contrucEntity, content.output());
                }

                /**
                 * Gerador service com Abstract
                 */
                if (!content.exclude().contains(Modules.S)) {
                    var constructService = ServiceGenerator.init().construct(classe);
                    FileHelper.writeToOutputFile(constructService, content.output());
                }

                /**
                 * Gerador de resource
                 */
                if (!content.exclude().contains(Modules.C)) {
                    var construcResource = ResourceGenerator.init().construct(classe);
                    FileHelper.writeToOutputFile(construcResource, content.output());
                }

                /**
                 * Gerador de rules
                 */
                if (!content.exclude().contains(Modules.R)) {
                    var generateRules = RulesGenerator.init().build(classe);
                    FileHelper.writeToOutputFile(generateRules, content.output());
                }

            }

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

}
