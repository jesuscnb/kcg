package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.utils.MessageFormatUtils;
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
import org.apache.commons.lang3.compare.ComparableUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static br.com.akowalski.constants.Messages.FOUR_WHITESPACES;
import static br.com.akowalski.utils.MessageFormatUtils.Type.MAX_DATE;
import static br.com.akowalski.utils.MessageFormatUtils.Type.MAX_SIZE;
import static br.com.akowalski.utils.MessageFormatUtils.Type.MIN_DATE;
import static br.com.akowalski.utils.MessageFormatUtils.Type.MIN_SIZE;
import static br.com.akowalski.utils.MessageFormatUtils.Type.NOT_NULL;

public class RulesGenerator {

    public static RulesGenerator init() {
        return new RulesGenerator();
    }

    public JavaFile construct(DevPoolClass devPoolClass) throws IOException {

        String entityName = StringUtils.capitalize(devPoolClass.name());
        String name = entityName + "Rules";
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".models." + devPoolClass.name());

        CodeBlock.Builder code = CodeBlock.builder();
        code.addStatement("this.rules = new $T<>()", ParameterizedTypeName.get(HashSet.class));

        List<MethodSpec> methods = new ArrayList<>();


        devPoolClass.attributes().stream().forEach(s -> {
            if (Objects.nonNull(s.rules())) {
                Class<?> type = getFieldType(s);

                MethodSpec rules = MethodSpec.methodBuilder(s.name().toLowerCase())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ClassName.bestGuess(devPoolClass.packageName() + ".rules." + name))
                        .addParameter(type, "value")
                        .addCode(createRule(type, s.name(), s.rules()))
                        .build();

                methods.add(rules);
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

        return JavaFile
                .builder(devPoolClass.packageName() + ".rules", typeSpec)
                .indent(Messages.FOUR_WHITESPACES)
                .build();


    }

    public CodeBlock createRule(Class<?> type, String fieldName, List<String> rules) {
        CodeBlock.Builder builder = CodeBlock.builder();
        builder.beginControlFlow("this.rules.add(new $T(() -> ", ParameterizedTypeName.get(ResultRuleHolder.class));

        Optional<String> notNull = rules.stream().filter(s -> s.equalsIgnoreCase("notNull")).findFirst();
        if (notNull.isPresent()) {

            if (Objects.equals(type, String.class)) {
                builder.beginControlFlow("if ($T.isEmpty(value)) ", ParameterizedTypeName.get(StringUtils.class));
            } else {
                builder.beginControlFlow("if ($T.isNull(value)) ", ParameterizedTypeName.get(Objects.class));
            }

            builder.addStatement("return $T.of(false, \"" + MessageFormatUtils.format(NOT_NULL, fieldName) + "\")", ParameterizedTypeName.get(Pair.class))
                    .endControlFlow().add("\n");
        }

        Optional<String> maxSize = rules.stream().filter(s -> s.contains("maxSize")).findFirst();
        if (maxSize.isPresent()) {

            String value = maxSize.get().split("=")[1];

            if (Objects.equals(type, String.class)) {
                int max = Integer.parseInt(value);
                builder.beginControlFlow("if (value.length() > " + max + ") ");

            } else if (Objects.equals(type, int.class)) {
                int max = Integer.parseInt(value);
                builder.beginControlFlow("if (value > " + max + ") ");

            } else if (Objects.equals(type, BigDecimal.class)) {
                BigDecimal max = new BigDecimal(value);
                builder.beginControlFlow("if ($T.is(value).greaterThan(new BigDecimal(" + max + ")))", ParameterizedTypeName.get(ComparableUtils.class));

            } else if (Objects.equals(type, double.class)) {
                double min = Double.valueOf(value);
                builder.beginControlFlow("if ($T.is(value).greaterThan(" + min + "))", ParameterizedTypeName.get(ComparableUtils.class));
            }

            builder.addStatement("return $T.of(false, \"" + MessageFormatUtils.format(MAX_SIZE, fieldName, value) + "\")", ParameterizedTypeName.get(Pair.class))
                    .endControlFlow().add("\n");

        }

        Optional<String> minSize = rules.stream().filter(s -> s.contains("minSize")).findFirst();
        if (minSize.isPresent()) {
            String value = minSize.get().split("=")[1];

            if (Objects.equals(type, String.class)) {
                int min = Integer.parseInt(value);
                builder.beginControlFlow("if (value.length() < " + min + ") ");

            } else if (Objects.equals(type, int.class)) {
                int min = Integer.parseInt(value);
                builder.beginControlFlow("if (value < " + min + ") ");

            } else if (Objects.equals(type, BigDecimal.class)) {
                BigDecimal min = new BigDecimal(value);
                builder.beginControlFlow("if ($T.is(value).lessThan(new BigDecimal(" + min + ")))", ParameterizedTypeName.get(ComparableUtils.class));

            } else if (Objects.equals(type, double.class)) {
                double min = Double.valueOf(value);
                builder.beginControlFlow("if ($T.is(value).lessThan(" + min + "))", ParameterizedTypeName.get(ComparableUtils.class));
            }

            builder.addStatement("return $T.of(false, \"" + MessageFormatUtils.format(MIN_SIZE, fieldName, value) + "\")", ParameterizedTypeName.get(Pair.class))
                    .endControlFlow().add("\n");
        }

        Optional<String> minDate = rules.stream().filter(s -> s.contains("minDate")).findFirst();
        if (minDate.isPresent()) {

            String value = minDate.get();

            if (value.equalsIgnoreCase("now")) {
                builder.beginControlFlow("if (inputDate.isBefore(LocalDate.now()))");
            } else {
                value = value.split("=")[1];

                builder.add("$T inputDate = value.toInstant() \n", ParameterizedTypeName.get(LocalDate.class))
                        .indent()
                        .add(".atZone($T.systemDefault()) \n", ParameterizedTypeName.get(ZoneId.class))
                        .addStatement(".toLocalDate()").add("\n")
                        .unindent()
                        .beginControlFlow("if (inputDate.isBefore(LocalDate.parse(\"" + value + "\")))");
            }

            builder.addStatement("return $T.of(false, \"" + MessageFormatUtils.format(MIN_DATE, fieldName, value) + "\")", ParameterizedTypeName.get(Pair.class))
                    .endControlFlow().add("\n");
        }


        Optional<String> maxDate = rules.stream().filter(s -> s.contains("maxDate")).findFirst();
        if (maxDate.isPresent()) {

            String value = maxDate.get();

            if (value.contains("now")) {
                builder.beginControlFlow("if (inputDate.isAfter(LocalDate.now()))");
                value = "now";
            } else {
                value = value.split("=")[1];

                if (minDate.isEmpty()) {
                    builder.add("$T inputDate = value.toInstant() \n", ParameterizedTypeName.get(LocalDate.class))
                            .add(".atZone($T.systemDefault()) \n", ParameterizedTypeName.get(ZoneId.class))
                            .addStatement(".toLocalDate()");
                }

                builder.beginControlFlow("if (inputDate.isAfter(LocalDate.parse(\"" + value + "\")))");
            }

            builder.addStatement("return $T.of(false, \"" + MessageFormatUtils.format(MAX_DATE, fieldName, value) + "\")", ParameterizedTypeName.get(Pair.class))
                    .endControlFlow().add("\n");
        }

        return builder.addStatement("return $T.of(true, null)", ParameterizedTypeName.get(Pair.class))
                .endControlFlow("))")
                .addStatement("return this")
                .build();

    }


    public Class<?> getFieldType(DevPoolAttribute attribute) {

        switch (attribute.type().toLowerCase()) {
            case "boolean":
            case "bool":
                return boolean.class;
            case "integer":
            case "int":
                return int.class;
            case "date":
                return Date.class;
            case "double":
                return double.class;
            case "bigdecimal":
                return BigDecimal.class;
            default:
                return String.class;
        }

    }


}
