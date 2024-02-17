package br.com.akowalski.pojos;

import br.com.akowalski.requests.Modules;

import java.io.File;
import java.util.List;

public record KcgContent(String json, File archive, List<Modules> exclude, String output) {
}
