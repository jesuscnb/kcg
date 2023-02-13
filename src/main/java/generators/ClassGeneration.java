package generators;

import com.google.gson.Gson;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.annotations.Entity;
import pojos.DevPoolClass;
import utils.FileUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ClassGeneration {

    private static final String FOUR_WHITESPACES = "    ";

    private FielGenerator fielGenerator = FielGenerator.init();


    public static ClassGeneration init() {
        return new ClassGeneration();
    }

    public void contrucEntity(DevPoolClass devPoolClass) throws IOException {
        Set<FieldSpec> fields = fielGenerator.construct(devPoolClass.attributes());
        Set<MethodSpec> methods = MethodGenerator.init().constructGettersAndSetters(fields);
        TypeSpec typeSpec = TypeSpec.classBuilder(devPoolClass.name())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Entity.class)
                        .addMember("value", "$S", devPoolClass.serializedName() != null ? devPoolClass.serializedName() : devPoolClass.name().toLowerCase())
                        .addMember("noClassnameStored", "$L", true)
                        .build()
                )
                .addFields(fields)
                .addMethods(methods)
                .build();

        JavaFile file = JavaFile
                .builder(devPoolClass.packageName() + ".models", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);

    }


    public void contructAbstractService(DevPoolClass devPoolClass) throws IOException {
        ClassName daoName = ClassName.bestGuess(devPoolClass.packageName() + ".service." + "MongoDAO");

        FieldSpec fiedlService = FieldSpec
                .builder(ParameterizedTypeName.get(daoName, TypeVariableName.get("T")), "dao")
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec save = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeVariableName.get("T"), "entity")
                .addCode(CodeBlock.builder()
                        .addStatement("this.dao.save(entity)")
                        .build())
                .build();

        MethodSpec delete = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .addCode(CodeBlock.builder()
                        .addStatement("this.dao.deleteById(new ObjectId(id), Object.class)")
                        .build())
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder("AbstractService")
                .addModifiers(Modifier.PUBLIC)
                .addField(fiedlService)
                .addMethod(save)
                .addMethod(delete)
                .addTypeVariable(TypeVariableName.get("T"))
                .build();

        JavaFile file = JavaFile
                .builder("br.com.docvirtus.service", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);

    }

    public void constructService(DevPoolClass devPoolClass) throws IOException {
        String entityName = StringUtils.capitalize(devPoolClass.name());
        String name = entityName + "Service";
        ClassName daoName = ClassName.bestGuess(devPoolClass.packageName() + ".service." + "MongoDAO");
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".service." + devPoolClass.name());

        MethodSpec privateContructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("this.dao = new MongoDAO<>();")
                .build();


        MethodSpec instance = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addStatement("return new " + name + "()")
                .build();

        FieldSpec fiedlService = FieldSpec
                .builder(ParameterizedTypeName.get(daoName, entityClass), "dao")
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec save = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(entityClass, "entity")
                .addCode(CodeBlock.builder()
                        .addStatement("this.dao.save(entity)")
                        .build())
                .build();

        MethodSpec update = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(entityClass, "entity")
                .addParameter(String.class, "id")
                .addCode(CodeBlock.builder()
                        .addStatement("ResponseModel<String> r = new ResponseModel<>()")
                        .addStatement("this.dao.update(entity, new ObjectId(id), " + entityName + ".class)")
                        .addStatement("r.getDados().add(id)")
                        .addStatement("r.setMensagem(\"ok\")")
                        .addStatement("return r")
                        .build())
                .returns(ClassName.bestGuess("ResponseModel"))
                .build();

        MethodSpec delete = MethodSpec.methodBuilder("deleteById")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .addCode(CodeBlock.builder()
                        .addStatement("this.dao.deleteById(new ObjectId(id), " + entityName + ".class)")
                        .build())
                .build();

        MethodSpec findAll = MethodSpec.methodBuilder("findAll")
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder()
                        .addStatement("this.dao.setDataStore()")
                        .addStatement("Query<" + entityName + "> query = this.dao.getDs().createQuery(" + entityName + ".class)")
                        .addStatement("QueryResults find = dao.find(query)")
                        .addStatement("ResponseModel<" + entityName + "> r = new ResponseModel<>()")
                        .addStatement("r.getDados().addAll(find.asList())")
                        .addStatement("r.setMensagem(\"ok\")")
                        .addStatement("return r")
                        .build())
                .returns(ClassName.bestGuess("ResponseModel"))
                .build();

        MethodSpec findById = MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .addCode(CodeBlock.builder()
                        .addStatement("ResponseModel<" + entityName + "> response = new ResponseModel<>()")
                        .addStatement("Query<" + entityName + "> query = this.dao.getDs().createQuery(" + entityName + ".class);")
                        .addStatement("query.and(query.criteria(\"_id\").equal(id))")
                        .addStatement("QueryResults<" + entityName + "> find = this.dao.find(query)")
                        .beginControlFlow("if (find.count() == 0)")
                        .addStatement("response.setMensagem(\"Nenhum resultado encontrado\")")
                        .addStatement("response.setCodigoErro(404)")
                        .addStatement("return response")
                        .endControlFlow()
                        .addStatement("response.setMensagem(\"ok\")")
                        .addStatement("response.getDados().add(find.get())")
                        .addStatement("return response")
                        .build())
                .returns(ClassName.bestGuess("ResponseModel"))
                .build();


        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .addField(fiedlService)
                .addMethod(instance)
                .addMethod(privateContructor)
                .addMethod(save)
                .addMethod(update)
                .addMethod(delete)
                .addMethod(findById)
                .addMethod(findAll)
                .build();

        JavaFile file = JavaFile
                .builder(devPoolClass.packageName() + ".service", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);
    }


    public void constructResource(DevPoolClass devPoolClass) throws IOException {
        String name = StringUtils.capitalize(devPoolClass.name());
        String serviceName = devPoolClass.name().concat("Service");
        ClassName className = ClassName.bestGuess(devPoolClass.packageName() + ".service." + serviceName);

        MethodSpec contructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("this.$N = $N", "service", serviceName + ".init()")
                .build();

        FieldSpec fiedlService = FieldSpec
                .builder(className, "service")
                .addModifiers(Modifier.PRIVATE)
                .build();


        CodeBlock findAll = CodeBlock
                .builder()
                .add("path(basePath + \"/" + devPoolClass.name().toLowerCase() + "\" , () -> {\n").indent()
                .add("post(\"\", (request, response) -> service.save(gson.fromJson(request.body(), " + devPoolClass.packageName() + ".models." + name + ".class), response), gson::toJson ); \n")
                .add("get(\"\", (request, response) -> service.findAll(), gson::toJson ); \n")
                .add("get(\"/:id\", (request, response) -> service.findById(request.params(\":id\")), gson::toJson ); \n")
                .add("put(\"/:id\", (request, response) -> service.update(gson.fromJson(request.body(), " + devPoolClass.packageName() + ".models." + name + ".class),request.params(\":id\"), response), gson::toJson ); \n")
                .add("delete(\"/:id\", (request, response) -> service.deleteById(request.params(\":id\")), gson::toJson ); \n").unindent()
                .add("});")
                .build();

        MethodSpec configureRoutes = MethodSpec
                .methodBuilder("configureRoutes")
                .addParameter(Gson.class, "gson")
                .addParameter(String.class, "basePath")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode(findAll)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(name + "Resource")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(contructor)
                .addMethod(configureRoutes)
                .addField(fiedlService)
                .build();

        JavaFile file = JavaFile
                .builder(devPoolClass.packageName() + ".resource", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);
    }

}
