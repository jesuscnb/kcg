package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.pojos.DevPoolAttribute;
import br.com.akowalski.pojos.DevPoolClass;
import br.com.docvirtus.commons.annotation.Entity;
import br.com.docvirtus.commons.annotation.RulesListener;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class EntityGenerator {

    private final FielGenerator fielGenerator = FielGenerator.init();

    public static EntityGenerator init() {
        return new EntityGenerator();
    }

    public JavaFile contruct(DevPoolClass devPoolClass) {
        Set<FieldSpec> fields = fielGenerator.construct(devPoolClass.attributes());
        Set<MethodSpec> methods = MethodGenerator.init().constructGettersAndSetters(fields);
        TypeSpec.Builder builer = TypeSpec.classBuilder(devPoolClass.name());

        long attWithRules = devPoolClass.attributes().stream().filter(s -> Objects.nonNull(s.rules())).count();
        if (attWithRules > 0) {

            ClassName rulesClass = ClassName.bestGuess(devPoolClass.packageName() + ".rules." + devPoolClass.name() + "Rules");

            builer.addAnnotation(AnnotationSpec.builder(RulesListener.class)
                            .addMember("listener", CodeBlock.builder().add("$T.class", rulesClass).build())
                    .build());
        }

        TypeSpec typeSpec = builer.addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Entity.class)
                        .addMember("value", "$S", devPoolClass.serializedName() != null ? devPoolClass.serializedName() : devPoolClass.name().toLowerCase())
                        .build()
                )
                .addFields(fields)
                .addMethods(methods)
                .build();

        return JavaFile
                .builder(devPoolClass.packageName() + ".models", typeSpec)
                .indent(Messages.FOUR_WHITESPACES)
                .build();

    }

}
