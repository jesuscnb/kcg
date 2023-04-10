package br.com.akowalski.pojos;

import java.util.List;
import java.util.Set;

public record KcgAttribute(String name,
                           String serializedName,
                           String type,
                           String element,
                           List<String> rules,
                           KcgSubClass subClass,
                           Set<KcgAnnotation> annotations) {

}
