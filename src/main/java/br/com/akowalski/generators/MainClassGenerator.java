package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.pojos.KcgClass;
import br.com.docvirtus.commons.Bootstrap;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;

public class MainClassGenerator {

    public static MainClassGenerator init() {
        return new MainClassGenerator();
    }

    public JavaFile construct(KcgClass clazz) {
        CodeBlock resources = CodeBlock
                .builder()
                .addStatement("log.info(\"start bootstrap\")")
                .addStatement("$T.init()", ParameterizedTypeName.get(Bootstrap.class))
                .build();

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addCode(resources)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder("App")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(main)
                .build();

        return JavaFile
                .builder(clazz.packageName(), typeSpec)
                .indent(Messages.FOUR_WHITESPACES)
                .build();

    }

}
