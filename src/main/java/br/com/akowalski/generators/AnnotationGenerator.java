package br.com.akowalski.generators;

import br.com.akowalski.pojos.KcgAttribute;
import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.AnnotationSpec;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AnnotationGenerator {

    public static AnnotationGenerator init() {
        return new AnnotationGenerator();
    }

    public Set<AnnotationSpec> addAnnotationsOnEntityFields(KcgAttribute attribute) {
        Set<AnnotationSpec> anottations = new HashSet<>();

        if (Objects.nonNull(attribute.serializedName())) {
            anottations.add(AnnotationSpec.builder(SerializedName.class).addMember("value", "$S", attribute.serializedName()).build());
        }

        return anottations;

    }

}
