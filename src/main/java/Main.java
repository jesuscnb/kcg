import com.google.gson.Gson;
import com.squareup.javapoet.TypeSpec;
import generators.ClassGeneration;
import pojos.DevPoolClass;
import utils.FileUtils;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        DevPoolClass classe = gson.fromJson(getUserSchema(), DevPoolClass.class);
        ClassGeneration classGenerator = ClassGeneration.init();

        /**
         * Gerando entity
         */
        classGenerator.contrucEntity(classe);

        /**
         * Gerando Service Abstrata
         */
        classGenerator.contructAbstractService(classe);

        /**
         * Gerador da service
         */
        classGenerator.constructService(classe);

        /**
         * Gerador de resource
         */

        classGenerator.constructResource(classe);


    }

    public static String getUserSchema() {
        return """
                {
                	"name": "Usuario",
                	"serializedName": "usuarios",
                	"packageName": "br.com.docvirtus",
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
