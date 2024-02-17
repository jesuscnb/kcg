package br.com.akowalski.generators;

import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.pojos.KcgProject;
import br.com.docvirtus.commons.Bootstrap;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class MainClassGenerator {

    public static MainClassGenerator init() {
        return new MainClassGenerator();
    }

    public JavaFile construct(KcgProject clazz) {
        CodeBlock resources = CodeBlock
                .builder()
                .addStatement("$T.init()", ParameterizedTypeName.get(Bootstrap.class))
                .build();

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addCode(resources)
                .addParameter(String[].class, "args")
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder("App")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .build();

        return JavaFile
                .builder(clazz.packageName(), typeSpec)
                .indent(FileHelper.FOUR_WHITESPACES)
                .build();

    }

}
