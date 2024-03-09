package br.com.akowalski.template;

import br.com.akowalski.generators.EntityGenerator;
import br.com.akowalski.generators.ResourceGenerator;
import br.com.akowalski.generators.RulesGenerator;
import br.com.akowalski.generators.ServiceGenerator;
import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.pojos.KcgClass;
import br.com.akowalski.pojos.KcgContent;
import br.com.akowalski.requests.Modules;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CreateContent {

    public static void create(KcgContent content) {
        try {

            for (KcgClass classe : content.classes()) {

                /**
                 * Gerando entity
                 */
                if (!content.exclude().contains(Modules.E)) {
                    var contrucEntity = EntityGenerator.init().contruct(classe, content.packageName());
                    FileHelper.writeToOutputFile(contrucEntity, content.output());
                }

                /**
                 * Gerador service com Abstract
                 */
                if (!content.exclude().contains(Modules.S)) {
                    var constructService = ServiceGenerator.init().construct(classe, content.packageName());
                    FileHelper.writeToOutputFile(constructService, content.output());
                }

                /**
                 * Gerador de resource
                 */
                if (!content.exclude().contains(Modules.C)) {
                    var construcResource = ResourceGenerator.init().construct(classe, content.packageName());
                    FileHelper.writeToOutputFile(construcResource, content.output());
                }

                /**
                 * Gerador de rules
                 */
                if (!content.exclude().contains(Modules.R)) {
                    var generateRules = RulesGenerator.init().build(classe, content.packageName());
                    FileHelper.writeToOutputFile(generateRules, content.output());
                }

            }

        } catch (Exception e) {
            ExceptionUtils.rethrow(e);
        }
    }

}
