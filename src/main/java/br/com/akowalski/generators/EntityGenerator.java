package br.com.akowalski.generators;

import br.com.akowalski.pojos.DevPoolClass;
import br.com.docvirtus.commons.annotation.Entity;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.Set;

public class EntityGenerator {

    private final FielGenerator fielGenerator = FielGenerator.init();

    public static EntityGenerator init() {
        return new EntityGenerator();
    }

    public JavaFile contruct(DevPoolClass devPoolClass) {
        Set<FieldSpec> fields = fielGenerator.construct(devPoolClass.attributes());
        Set<MethodSpec> methods = MethodGenerator.init().constructGettersAndSetters(fields);
        TypeSpec typeSpec = TypeSpec.classBuilder(devPoolClass.name())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Entity.class)
                        .addMember("value", "$S", devPoolClass.serializedName() != null ? devPoolClass.serializedName() : devPoolClass.name().toLowerCase())
                        .build()
                )
                .addFields(fields)
                .addMethods(methods)
                .build();

        return JavaFile
                .builder(devPoolClass.packageName() + ".models", typeSpec)
                .build();

    }

}
