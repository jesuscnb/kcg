package br.com.akowalski.generators;

import br.com.akowalski.pojos.DevPoolClass;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import br.com.akowalski.pojos.DevPoolAttribute;

import javax.lang.model.element.Modifier;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FieldGenerator {

    private AnnotationGenerator annotationGenerator = AnnotationGenerator.init();

    public static FieldGenerator init() {
        return new FieldGenerator();
    }
    public Set<FieldSpec> construct(DevPoolClass devPoolClass) {
        Set<FieldSpec> fiels = new HashSet<>();
        for (DevPoolAttribute s : devPoolClass.attributes()) {
            fiels.add(contruct(s, devPoolClass.packageName()));
        }
        return fiels;
    }

    public FieldSpec contruct(DevPoolAttribute attribute, String packageName) {

        switch (attribute.type().toLowerCase()) {
            case "boolean":
            case "bool":
                return FieldSpec.builder(boolean.class, attribute.name())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .build();
            case "integer":
            case "int":
                return FieldSpec.builder(int.class, attribute.name())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "date":
                return FieldSpec.builder(Date.class, attribute.name())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "double":
                return FieldSpec.builder(double.class, attribute.name())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "bigdecimal":
                return FieldSpec.builder(BigDecimal.class, attribute.name())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "objectid":
                return FieldSpec.builder(String.class, attribute.name())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(BsonId.class)
                        .addAnnotation(AnnotationSpec.builder(BsonRepresentation.class).addMember("value", "$T.$L", BsonType.class, BsonType.OBJECT_ID).build())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .build();
            case "array":
            case "list":
                ClassName entityClass = ClassName.bestGuess(packageName + ".models." + attribute.element());
                return FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(List.class), entityClass), attribute.name())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "enum":
            case "enumerate":
                ClassName enumClass = ClassName.bestGuess(packageName + ".enumerates." + attribute.element());
                return FieldSpec.builder(enumClass, attribute.name())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            default:
                return FieldSpec.builder(String.class, attribute.name())
                        .addAnnotations(annotationGenerator.addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
        }

    }

}
