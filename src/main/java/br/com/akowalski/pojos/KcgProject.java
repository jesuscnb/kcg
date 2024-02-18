package br.com.akowalski.pojos;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public record KcgProject(String name, String packageName, List<Pair<String, String>> resources) {
}
