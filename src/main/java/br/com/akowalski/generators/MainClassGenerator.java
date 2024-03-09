package br.com.akowalski.generators;

import br.com.akowalski.helpers.FileHelper;
import br.com.docvirtus.commons.Bootstrap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.lang.model.element.Modifier;
import java.util.List;

public class MainClassGenerator {

    public static MainClassGenerator init() {
        return new MainClassGenerator();
    }

    public JavaFile construct(String name, String packageName, List<Pair<String, String>> resources) {

        List<ClassName> classNames = resources
                .stream()
                .map(m -> ClassName.bestGuess(m.getLeft() + "." + m.getRight()))
                .toList();

        CodeBlock.Builder builder = CodeBlock.builder();
        for (var c : classNames) {
            builder.add("$T.init();" + System.lineSeparator(), c);
        }

        CodeBlock block = builder.build();

        CodeBlock mainContent = CodeBlock
                .builder()
                .addStatement("$T.init()", ParameterizedTypeName.get(Bootstrap.class))
                .add(block)
                .build();

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addCode(mainContent)
                .addParameter(String[].class, "args")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .build();

        return JavaFile
                .builder(packageName, typeSpec)
                .indent(FileHelper.FOUR_WHITESPACES)
                .build();

    }

}
