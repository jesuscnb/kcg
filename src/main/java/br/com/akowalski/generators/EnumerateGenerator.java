package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.pojos.KcgAttribute;
import br.com.akowalski.pojos.KcgClass;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;


public class EnumerateGenerator {

    public static EnumerateGenerator init() {
        return new EnumerateGenerator();
    }

    public JavaFile contruct(KcgClass clazz) {

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(clazz.name());
        for (KcgAttribute attribute : clazz.attributes()) {
            enumBuilder.addEnumConstant(attribute.name());
        }

        return JavaFile
                .builder(clazz.packageName() + ".enumerates", enumBuilder.addModifiers(Modifier.PUBLIC).build())
                .indent(Messages.FOUR_WHITESPACES)
                .build();

    }

}
