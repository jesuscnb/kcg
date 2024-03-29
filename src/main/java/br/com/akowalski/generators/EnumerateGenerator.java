package br.com.akowalski.generators;

import br.com.akowalski.helpers.FileHelper;
import br.com.akowalski.pojos.KcgAttribute;
import br.com.akowalski.pojos.KcgClass;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;


public class EnumerateGenerator {

    public static EnumerateGenerator init() {
        return new EnumerateGenerator();
    }

    public JavaFile contruct(KcgClass clazz, String packageName) {

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(clazz.name());
        for (KcgAttribute attribute : clazz.attributes()) {
            enumBuilder.addEnumConstant(attribute.name());
        }

        return JavaFile
                .builder(packageName + ".enumerates", enumBuilder.addModifiers(Modifier.PUBLIC).build())
                .indent(FileHelper.FOUR_WHITESPACES)
                .build();

    }

}
