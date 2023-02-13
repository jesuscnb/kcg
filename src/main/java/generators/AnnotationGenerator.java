package generators;

import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.AnnotationSpec;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import pojos.DevPoolAttribute;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AnnotationGenerator {

    public static AnnotationGenerator init() {
        return new AnnotationGenerator();
    }

    public Set<AnnotationSpec> addAnnotationsOnEntityFields(DevPoolAttribute attribute) {
        Set<AnnotationSpec> anottations = new HashSet<>();

        if (attribute.indexed()) {

            AnnotationSpec.Builder aBuilder = AnnotationSpec.builder(Indexed.class);

            if (attribute.unique()) {
                aBuilder.addMember("options", "$L", AnnotationSpec.builder(IndexOptions.class).addMember("unique", "$L", true).build());
            }

            anottations.add(aBuilder.build());
        }

        if (Objects.nonNull(attribute.serializedName())) {
            anottations.add(AnnotationSpec.builder(SerializedName.class).addMember("value", "$S", attribute.serializedName()).build());
        }

        return anottations;

    }

}
