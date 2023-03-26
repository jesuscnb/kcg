package br.com.akowalski.generators;

import br.com.docvirtus.commons.rules.AbstractRules;
import br.com.docvirtus.commons.rules.ResultRuleHolder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;
import br.com.akowalski.pojos.DevPoolAttribute;
import br.com.akowalski.pojos.DevPoolClass;
import br.com.akowalski.utils.FileUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RulesGenerator {

    private static final String FOUR_WHITESPACES = "    ";

    public static RulesGenerator init() {
        return new RulesGenerator();
    }

    public void run(DevPoolClass devPoolClass) throws IOException {

        String entityName = StringUtils.capitalize(devPoolClass.name());
        String name = entityName + "Rules";
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".models." + devPoolClass.name());

        CodeBlock.Builder code = CodeBlock.builder();
        code.addStatement("this.rules = new HashSet<>()");

        List<MethodSpec> methods = new ArrayList<>();

        devPoolClass.attributes().stream().forEach(s -> {
            if (s.required()) {

                MethodSpec rule = MethodSpec.methodBuilder(s.name().toLowerCase())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess(devPoolClass.packageName() + ".rules." + name))
                        .addParameter(getFieldType(s), "value")
                        .addCode(CodeBlock.builder()
                                .beginControlFlow("this.rules.add(new $T(() -> ", ParameterizedTypeName.get(ResultRuleHolder.class))
                                .beginControlFlow("if (StringUtils.isEmpty(value)) ")
                                .addStatement("return Pair.of(false, \"O campo " + s.name().toLowerCase() + " é obrigatório\")")
                                .endControlFlow()
                                .addStatement("return Pair.of(true, null)")
                                .endControlFlow("))")
                                .addStatement("return this")
                                .build())
                        .build();

                methods.add(rule);
                code.addStatement("this." + s.name().toLowerCase() + "(entity.get" + StringUtils.capitalize(s.name()) + "())");

            }
        });

        MethodSpec contructor = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(entityClass, "entity")
                .addCode(code.build())
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .superclass(AbstractRules.class)
                .addMethod(contructor)
                .addMethods(methods)
                .build();

        JavaFile file = JavaFile
                .builder(devPoolClass.packageName() + ".rules", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);

    }


    public Class<?> getFieldType(DevPoolAttribute attribute) {

        switch (attribute.type().toLowerCase()) {
            case "boolean":
            case "bool":
                return Boolean.class;
            case "integer":
                return Integer.class;
            case "date":
                return Date.class;
            case "double":
                return Double.class;
            case "bigdecimal":
                return BigDecimal.class;
            case "objectid":
                return String.class;
            default:
                return String.class;
        }

    }


}
