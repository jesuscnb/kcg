package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.pojos.DevPoolAttribute;
import br.com.akowalski.pojos.DevPoolClass;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;


public class EnumerateGenerator {

    public static EnumerateGenerator init() {
        return new EnumerateGenerator();
    }

    public JavaFile contruct(DevPoolClass devPoolClass) {

        TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(devPoolClass.name());
        for (DevPoolAttribute attribute : devPoolClass.attributes()) {
            enumBuilder.addEnumConstant(attribute.name());
        }

        return JavaFile
                .builder(devPoolClass.packageName() + ".enumerates", enumBuilder.addModifiers(Modifier.PUBLIC).build())
                .indent(Messages.FOUR_WHITESPACES)
                .build();

    }

}
