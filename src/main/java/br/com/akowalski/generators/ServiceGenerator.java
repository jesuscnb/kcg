package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.pojos.KcgClass;
import br.com.docvirtus.commons.data.service.AbstractService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;

public class ServiceGenerator {

    public static ServiceGenerator init(){
        return new ServiceGenerator();
    }

    public JavaFile construct(KcgClass clazz) {
        String entityName = StringUtils.capitalize(clazz.name());
        String name = entityName + "Service";
        ClassName entityClass = ClassName.bestGuess(clazz.packageName() + ".models." + clazz.name());

        MethodSpec instance = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addStatement("return new " + name + "()")
                .returns(TypeVariableName.get(name))
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractService.class), entityClass))
                .addMethod(instance)
                .build();

        return JavaFile
                .builder(clazz.packageName() + ".services", typeSpec)
                .indent(Messages.FOUR_WHITESPACES)
                .build();

    }

}
