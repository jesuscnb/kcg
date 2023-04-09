package br.com.akowalski.generators;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import java.util.HashSet;
import java.util.Set;

public class MethodGenerator {

    public static MethodGenerator init() {
        return new MethodGenerator();
    }

    public Set<MethodSpec> constructGettersAndSetters(Set<FieldSpec> fiels) {
        Set<MethodSpec> methods = new HashSet<>();
        fiels.forEach(s -> {
            String name = StringUtils.capitalize(s.name);

            methods.add(
                    MethodSpec
                            .methodBuilder("get" + name)
                            .addModifiers(Modifier.PUBLIC)
                            .returns(s.type)
                            .addStatement("return this." + s.name)
                            .build());

            methods.add(
                    MethodSpec
                            .methodBuilder("set" + name)
                            .addParameter(s.type, s.name)
                            .addModifiers(Modifier.PUBLIC)
                            .addStatement("this." + s.name + " = "+ s.name)
                            .build()
            );

        });
        return methods;
    }

}
