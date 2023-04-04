package br.com.akowalski.generators;

import br.com.akowalski.constants.Messages;
import br.com.akowalski.pojos.DevPoolClass;
import br.com.docvirtus.commons.config.Config;
import br.com.docvirtus.commons.transform.JsonTransform;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;

public class ResourceGenerator {

    public static ResourceGenerator init() {
        return new ResourceGenerator();
    }

    public JavaFile construct(DevPoolClass devPoolClass) throws IOException {
        String resourceName = devPoolClass.name().concat("Resource");
        String serviceName = devPoolClass.name().concat("Service");
        ClassName className = ClassName.bestGuess(devPoolClass.packageName() + ".services." + serviceName);
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".models." + devPoolClass.name());

        MethodSpec init = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode(CodeBlock.builder()
                        .addStatement("new " + resourceName + "()")
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
                .add("path($T.get().SERVER_PATH + \"/" + devPoolClass.name().toLowerCase() + "\" , () -> {\n", ParameterizedTypeName.get(Config.class)).indent()
                .add("post(\"\", (request, response) -> service.save(JsonTransform.gson().fromJson(request.body(), $T.class)), JsonTransform::response ); \n", entityClass)
                .add("put(\"/:id\", (request, response) -> service.update($T.gson().fromJson(request.body(), $T.class),request.params(\":id\")), JsonTransform::response ); \n", ParameterizedTypeName.get(JsonTransform.class), entityClass)
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
                .addMethod(contructor)
                .addMethod(init)
                .addField(fiedlService)
                .build();

        return JavaFile
                .builder(devPoolClass.packageName() + ".resources", typeSpec)
                .addStaticImport(spark.Spark.class, "post", "put", "delete", "get", "path")
                .indent(Messages.FOUR_WHITESPACES)
                .build();

    }

}
