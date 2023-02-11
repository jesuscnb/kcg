import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import pojos.DevPoolAnnotation;
import pojos.DevPoolAttribute;
import pojos.DevPoolClass;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Main {

    private static File outputFile;

    private static final String FOUR_WHITESPACES = "    ";

    public static void main(String[] args) throws IOException {
        Main process = new Main();

        outputFile = new File(Paths.get(new File(".").getAbsolutePath() + "/gensrc").toUri());

        Gson gson = new Gson();
        DevPoolClass classe = gson.fromJson(process.getUserSchema(), DevPoolClass.class);
        TypeSpec typeSpec = process.contrucEntityClass(classe);
        process.writeToOutputFile("br.com.docvirtus", typeSpec);

    }

    public Set<FieldSpec> constructFields(Set<DevPoolAttribute> attributes) {
        Set<FieldSpec> fiels = new HashSet<>();
        for (DevPoolAttribute s : attributes) {
            fiels.add(contructField(s));
        }
        return fiels;
    }


    public TypeSpec contrucEntityClass(DevPoolClass devPoolClass) {
        Set<FieldSpec> fields = constructFields(devPoolClass.attributes());
        Set<MethodSpec> methods = constructGettersAndSetters(fields);
        return TypeSpec.classBuilder(devPoolClass.name())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Entity.class)
                        .addMember("value", "$S", devPoolClass.serializedName() != null ? devPoolClass.serializedName() : devPoolClass.name().toLowerCase())
                        .addMember("noClassnameStored", "$L", true)
                        .build()
                )
                .addFields(fields)
                .addMethods(methods)
                .build();
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
                            .addParameter(s.type, "name")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(s.type)
                            .addStatement("this." + name + " = "+ name)
                            .build()
            );

        });
        return methods;
    }


    public Set<AnnotationSpec> construcAnnotations(List<DevPoolAnnotation> annotations) {
        Set<AnnotationSpec> anottations = new HashSet<>();
        anottations.forEach(s -> {

        });
        return anottations;
    }

    public FieldSpec contructField(DevPoolAttribute attribute) {

        switch (attribute.type().toLowerCase()) {
            case "boolean":
            case "bool":
                return FieldSpec.builder(Boolean.class, attribute.name())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotations(addAnnotationsOnEntityFields(attribute))
                        .build();
            case "integer":
                return FieldSpec.builder(Integer.class, attribute.name())
                        .addAnnotations(addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "date":
                return FieldSpec.builder(Date.class, attribute.name())
                        .addAnnotations(addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "double":
                return FieldSpec.builder(Double.class, attribute.name())
                        .addAnnotations(addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "bigdecimal":
                return FieldSpec.builder(BigDecimal.class, attribute.name())
                        .addAnnotations(addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
            case "objectid":
                return FieldSpec.builder(ObjectId.class, attribute.name())
                        .addModifiers(Modifier.PRIVATE)
                        .addAnnotation(Id.class)
                        .addAnnotations(addAnnotationsOnEntityFields(attribute))
                        .build();
            default:
                return FieldSpec.builder(String.class, attribute.name())
                        .addAnnotations(addAnnotationsOnEntityFields(attribute))
                        .addModifiers(Modifier.PRIVATE)
                        .build();
        }

    }

    private Set<AnnotationSpec> addAnnotationsOnEntityFields(DevPoolAttribute attribute) {
        Set<AnnotationSpec> anottations = new HashSet<>();

        if (attribute.indexed()) {

            AnnotationSpec.Builder aBuilder = AnnotationSpec.builder(Indexed.class);

            if (attribute.unique()) {
                aBuilder.addMember("options", "$L",
                        AnnotationSpec.builder(IndexOptions.class).addMember("unique", "$L", true).build()
                );
            }

            anottations.add(aBuilder.build());
        }

        if (Objects.nonNull(attribute.serializedName())) {
            anottations.add(AnnotationSpec.builder(SerializedName.class)
                    .addMember("value", "$S", attribute.serializedName())
                    .build());
        }

        return anottations;

    }


    private void writeToOutputFile(String packageName, TypeSpec typeSpec) throws IOException {
        JavaFile javaFile = JavaFile
                .builder(packageName, typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();
        javaFile.writeTo(outputFile);
    }


    public String getUserSchema() {
        return """
                {
                	"name": "Usuario",
                	"serializedName": "usuarios",
                	"attributes": [
                	   {
                			"name": "id",
                			"type": "objectid",
                			"serializedName": "hexaId"
                		},
                		{
                			"name": "nome",
                			"type": "string",
                			"indexed": true,           			
                			"unique": true
                		},
                		{
                			"name": "dataNascimento",
                			"serializedName": "data_nascimento",
                			"type": "date",
                			"required": true
                		}
                	]

                }                                                             
                                """;
    }


}
