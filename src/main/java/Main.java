import com.google.gson.Gson;
import generators.ClassGeneration;
import generators.RulesGenerator;
import pojos.DevPoolClass;

import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {
        Gson gson = new Gson();
        DevPoolClass classe = gson.fromJson(getTipoTramitacao(), DevPoolClass.class);
        ClassGeneration classGenerator = ClassGeneration.init();

        /**
         * Gerando entity
         */
        classGenerator.contrucEntity(classe);


        /**
         * Gerador service com Abstract
         */
        classGenerator.constructService(classe);

        /**
         * Gerador de resource
         */
        classGenerator.constructResource(classe);

        /**
         * Gerador de rules
         */
        RulesGenerator.init().run(classe);

    }


    public static String getTipoTramitacao() {
        return """
                {
                	"name": "TipoTramitacao",
                	"serializedName": "tipo-tramitacoes",
                	"packageName": "br.com.docvirtus",
                	"attributes": [
                	   {
                			"name": "id",
                			"type": "objectid"
                		},
                		{
                			"name": "descricao",
                			"type": "string",
                			"indexed": true,           			
                			"unique": false,
                			"required": true
                		}
                	]
                }                                                             
                                """;
    }



}
