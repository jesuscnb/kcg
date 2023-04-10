package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.pojos.KcgClass;
import br.com.akowalski.pojos.KcgSubClass;
import br.com.docvirtus.commons.annotation.Entity;
import br.com.docvirtus.commons.annotation.RulesListener;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityGenerator {

    private final FieldGenerator fielGenerator = FieldGenerator.init();

    public static EntityGenerator init() {
        return new EntityGenerator();
    }

    public JavaFile contruct(KcgClass clazz) {
        List<FieldSpec> fields = fielGenerator.construct(clazz);
        List<MethodSpec> methods = MethodGenerator.init().constructGettersAndSetters(fields);
        TypeSpec.Builder builer = TypeSpec.classBuilder(clazz.name());

        long attWithRules = clazz.attributes().stream().filter(s -> Objects.nonNull(s.rules())).count();
        if (attWithRules > 0) {

            ClassName rulesClass = ClassName.bestGuess(clazz.packageName() + ".rules." + clazz.name() + "Rules");

            builer.addAnnotation(AnnotationSpec.builder(RulesListener.class)
                    .addMember("listener", CodeBlock.builder().add("$T.class", rulesClass).build())
                    .build());
        }

        Set<KcgSubClass> subClasses = clazz.attributes().stream()
                .filter(s -> Objects.nonNull(s.subClass()))
                .map(m -> m.subClass())
                .collect(Collectors.toSet());

        for (KcgSubClass subClass : subClasses) {
            if (subClass.type().equalsIgnoreCase("E")) {
                TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(subClass.name());
                for (String field : subClass.fields()) {
                    enumBuilder.addEnumConstant(field);
                }
                builer.addModifiers(Modifier.PUBLIC).addType(enumBuilder.build());
            }
        }

        TypeSpec typeSpec = builer.addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Entity.class)
                        .addMember("value", "$S", clazz.serializedName() != null ? clazz.serializedName() : clazz.name().toLowerCase())
                        .build()
                )
                .addFields(fields)
                .addMethods(methods)
                .build();

        return JavaFile
                .builder(clazz.packageName() + ".models", typeSpec)
                .indent(Messages.FOUR_WHITESPACES)
                .build();

    }

}
