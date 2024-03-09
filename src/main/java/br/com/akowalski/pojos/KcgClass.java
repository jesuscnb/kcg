package br.com.akowalski.pojos;

import java.util.Set;

public record KcgClass(String name,
                       String serializedName,
                       String type,
                       Set<KcgAttribute> attributes) {
}
