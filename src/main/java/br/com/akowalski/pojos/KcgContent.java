package br.com.akowalski.pojos;

import br.com.akowalski.requests.Modules;

import java.util.List;

public record KcgContent(List<KcgClass> classes, String packageName, List<Modules> exclude, String output) {
}
