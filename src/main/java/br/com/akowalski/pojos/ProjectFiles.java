package br.com.akowalski.pojos;

import java.util.List;

public record ProjectFiles (String name, String origin, String target, List<Tag> tags){

    public record Tag(String name, String value){}

}


