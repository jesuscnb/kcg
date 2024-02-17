package br.com.akowalski.requests;

public enum Modules {
    C("class"), S("service"), R("rules"), E("entity");

    private String name;

    Modules(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
