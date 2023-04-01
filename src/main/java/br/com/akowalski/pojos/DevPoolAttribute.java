package br.com.akowalski.pojos;

import java.util.List;
import java.util.Set;

public record DevPoolAttribute(String name,
                               String serializedName,
                               String type,
                               List<String> rules,
                               Set<DevPoolAnnotation> annotations) {

}
