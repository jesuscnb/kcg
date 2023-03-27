package br.com.akowalski.generators;

import br.com.docvirtus.commons.annotation.Entity;
import br.com.docvirtus.commons.service.AbstractService;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import br.com.akowalski.pojos.DevPoolClass;
import br.com.akowalski.utils.FileUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.Set;

public class ClassGeneration {

    private static final String FOUR_WHITESPACES = "    ";

    private FielGenerator fielGenerator = FielGenerator.init();


    public static ClassGeneration init() {
        return new ClassGeneration();
    }

    public JavaFile contrucEntity(DevPoolClass devPoolClass) throws IOException {
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
                .indent(FOUR_WHITESPACES)
                .build();

    }


    public JavaFile constructService(DevPoolClass devPoolClass) throws IOException {
        String entityName = StringUtils.capitalize(devPoolClass.name());
        String name = entityName + "Service";
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".models." + devPoolClass.name());


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
                .builder(devPoolClass.packageName() + ".service", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();


    }


    public JavaFile constructResource(DevPoolClass devPoolClass) throws IOException {
        String name = StringUtils.capitalize(devPoolClass.name());
        String resourceName = devPoolClass.name().concat("Resource");
        String serviceName = devPoolClass.name().concat("Service");
        ClassName className = ClassName.bestGuess(devPoolClass.packageName() + ".service." + serviceName);
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".models." + devPoolClass.name());

        MethodSpec init = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode(CodeBlock.builder()
                        .addStatement("new " + resourceName + ".init()")
                        .build())
                .build();

        FieldSpec fiedlService = FieldSpec
                .builder(className, "service")
                .initializer(CodeBlock.builder()
                        .add(serviceName + ".init()")
                        .build())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();


        CodeBlock resources = CodeBlock
                .builder()
                .add("path(Config.get().SERVER_PATH + \"/" + devPoolClass.name().toLowerCase() + "\" , () -> {\n").indent()
                .add("post(\"\", (request, response) -> service.save(gson.fromJson(request.body(), " + entityClass.simpleName() + ".class)), JsonTransform::response ); \n")
                .add("put(\"/:id\", (request, response) -> service.update(gson.fromJson(request.body(), " + entityClass.simpleName() + ".class),request.params(\":id\")), JsonTransform::response ); \n")
                .add("get(\"\", (request, response) -> service.findAll(), JsonTransform::response ); \n")
                .add("get(\"/:id\", (request, response) -> service.findById(request.params(\":id\")), JsonTransform::response ); \n")
                .add("delete(\"/:id\", (request, response) -> service.delete(request.params(\":id\")), JsonTransform::response ); \n").unindent()
                .add("});")
                .build();

        MethodSpec contructor = MethodSpec
                .constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addCode(resources)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(resourceName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(init)
                .addMethod(contructor)
                .addField(fiedlService)
                .build();

        return JavaFile
                .builder(devPoolClass.packageName() + ".resource", typeSpec)
                .addStaticImport(spark.Spark.class,"post", "put", "delete", "get", "path")
                .indent(FOUR_WHITESPACES)
                .build();


    }

}
