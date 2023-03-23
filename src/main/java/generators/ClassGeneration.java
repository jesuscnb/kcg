package generators;

import br.com.docvirtus.commons.repository.MongoDAO;
import br.com.docvirtus.commons.service.AbstractService;
import com.google.gson.Gson;
import com.squareup.javapoet.*;
import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.annotations.Entity;
import pojos.DevPoolClass;
import utils.FileUtils;

import javax.lang.model.element.Modifier;
import java.io.IOException;
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

        FieldSpec fiedlService = FieldSpec
                .builder(ParameterizedTypeName.get(ClassName.get(MongoDAO.class), TypeVariableName.get("T")), "dao")
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec abstractClazz = MethodSpec
                .methodBuilder("getClazz")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), TypeVariableName.get("?")))
                .build();

        MethodSpec getDao = MethodSpec
                .methodBuilder("getDao")
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder()
                        .addStatement("return this.dao")
                        .build())
                .returns(ParameterizedTypeName.get(ClassName.get(MongoDAO.class), TypeVariableName.get("T")))
                .build();

        MethodSpec save = MethodSpec.methodBuilder("save")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addParameter(TypeVariableName.get("T"), "entity")
                .addCode(CodeBlock.builder()
                        .addStatement("Key<T> key = this.getDao().save(entity)")
                        .addStatement("return key.getId().toString()")
                        .build())
                .returns(String.class)
                .build();

        MethodSpec update = MethodSpec.methodBuilder("update")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addParameter(TypeVariableName.get("T"), "entity")
                .addParameter(String.class, "id")
                .addCode(CodeBlock.builder()
                        .addStatement("this.getDao().update(entity, new ObjectId(id), getClazz())")
                        .addStatement("return entity")
                        .build())
                .returns(TypeVariableName.get("T"))
                .build();

        MethodSpec delete = MethodSpec.methodBuilder("delete")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addParameter(String.class, "id")
                .addCode(CodeBlock.builder()
                        .addStatement("this.getDao().deleteById(new ObjectId(id), getClazz())")
                        .addStatement("return id")
                        .build())
                .returns(String.class)
                .build();

        MethodSpec findById = MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addParameter(String.class, "id")
                .addCode(CodeBlock.builder()
                        .addStatement("Query<?> query = this.getDao().getDs().createQuery(getClazz())")
                        .addStatement("query.and(query.criteria(\"_id\").equal(id))")
                        .addStatement("QueryResults<T> find = this.getDao().find(query)")
                        .beginControlFlow("if (find.count() == 0)")
                        .addStatement("return null")
                        .endControlFlow()
                        .addStatement("return find.get()")
                        .build())
                .returns(TypeVariableName.get("T"))
                .build();

        MethodSpec findAll = MethodSpec.methodBuilder("findAll")
                .addModifiers(Modifier.PUBLIC)
                .addException(Exception.class)
                .addCode(CodeBlock.builder()
                        .addStatement("Query<?> query = this.getDao().getDs().createQuery(getClazz())")
                        .addStatement("QueryResults find = this.getDao().find(query)")
                        .addStatement("return find.asList()")
                        .build())
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), TypeVariableName.get("T")))
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder("AbstractService")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.ABSTRACT)
                .addMethod(abstractClazz)
                .addMethod(getDao)
                .addField(fiedlService)
                .addMethod(save)
                .addMethod(update)
                .addMethod(delete)
                .addMethod(findById)
                .addMethod(findAll)
                .addTypeVariable(TypeVariableName.get("T"))
                .build();

        JavaFile file = JavaFile
                .builder("br.com.docvirtus.service", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);

    }

    public void constructServiceHithoutAbstract(DevPoolClass devPoolClass) throws IOException {
        String entityName = StringUtils.capitalize(devPoolClass.name());
        String name = entityName + "ServiceHithoutAbstract";
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

        MethodSpec getClazz = MethodSpec.methodBuilder("getClazz")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addCode(CodeBlock.builder()
                        .addStatement("return " + entityName + ".class")
                        .build())
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), entityClass))
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
                .addMethod(getClazz)
                .build();

        JavaFile file = JavaFile
                .builder(devPoolClass.packageName() + ".service", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);
    }


    public void constructServiceWithAbstract(DevPoolClass devPoolClass) throws IOException {
        String entityName = StringUtils.capitalize(devPoolClass.name());
        String name = entityName + "Service";
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".models." + devPoolClass.name());


        MethodSpec instance = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addStatement("return new " + name + "()")
                .returns(TypeVariableName.get(name))
                .build();

        MethodSpec getClazz = MethodSpec.methodBuilder("getClazz")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addCode(CodeBlock.builder()
                        .addStatement("return " + entityName + ".class")
                        .build())
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), entityClass))
                .build();


        TypeSpec typeSpec = TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractService.class), entityClass))
                .addMethod(instance)
                .addMethod(getClazz)
                .build();

        JavaFile file = JavaFile
                .builder(devPoolClass.packageName() + ".service", typeSpec)
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);
    }


    public void constructResource(DevPoolClass devPoolClass) throws IOException {
        String name = StringUtils.capitalize(devPoolClass.name());
        String resourceName = devPoolClass.name().concat("Resource");
        String serviceName = devPoolClass.name().concat("Service");
        ClassName className = ClassName.bestGuess(devPoolClass.packageName() + ".service." + serviceName);
        ClassName entityClass = ClassName.bestGuess(devPoolClass.packageName() + ".models." + devPoolClass.name());

        MethodSpec init = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(Gson.class, "gson")
                .addParameter(String.class, "basePath")
                .addCode(CodeBlock.builder()
                        .addStatement("new " + resourceName + "(gson, basePath)")
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
                .add("path(basePath + \"/" + devPoolClass.name().toLowerCase() + "\" , () -> {\n").indent()
                .add("post(\"\", (request, response) -> service.save(gson.fromJson(request.body(), " + devPoolClass.name() + ".class)), gson::toJson ); \n")
                .add("get(\"\", (request, response) -> service.findAll(), gson::toJson ); \n")
                .add("get(\"/:id\", (request, response) -> service.findById(request.params(\":id\")), gson::toJson ); \n")
                .add("put(\"/:id\", (request, response) -> service.update(gson.fromJson(request.body(), " + devPoolClass.name() + ".class),request.params(\":id\")), gson::toJson ); \n")
                .add("delete(\"/:id\", (request, response) -> service.delete(request.params(\":id\")), gson::toJson ); \n").unindent()
                .add("});")
                .build();

        MethodSpec contructor = MethodSpec
                .constructorBuilder()
                .addParameter(Gson.class, "gson")
                .addParameter(String.class, "basePath")
                .addModifiers(Modifier.PRIVATE)
                .addCode(resources)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder(resourceName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(init)
                .addMethod(contructor)
                .addField(fiedlService)
                .build();

        JavaFile file = JavaFile
                .builder(devPoolClass.packageName() + ".resource", typeSpec)
                .addStaticImport(spark.Spark.class,"post", "put", "delete", "get", "path")
                .indent(FOUR_WHITESPACES)
                .build();

        FileUtils.writeToOutputFile(file);
    }

}
