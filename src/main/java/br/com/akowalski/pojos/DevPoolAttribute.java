package br.com.akowalski.pojos;

import java.util.Set;

public record DevPoolAttribute(String name,
                               String serializedName,
                               String type,
                               boolean indexed,
                               boolean required,
                               boolean unique,
                               Set<DevPoolAnnotation> annotations) {

}
