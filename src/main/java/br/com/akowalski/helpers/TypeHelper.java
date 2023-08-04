package br.com.akowalski.helpers;

import br.com.akowalski.pojos.KcgAttribute;
import com.squareup.javapoet.ClassName;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class TypeHelper {
    public static Class<?> get(String value) {
        switch (value.toLowerCase()) {
            case "string":
                return String.class;
            case "boolean":
            case "bool":
                return boolean.class;
            case "integer":
            case "int":
                return int.class;
            case "date":
                return Date.class;
            case "double":
                return double.class;
            case "bigdecimal":
                return BigDecimal.class;
            case "objectid":
                return ObjectId.class;
            case "array":
            case "list":
                return List.class;
            default:
                return Object.class;
        }
    }

}
