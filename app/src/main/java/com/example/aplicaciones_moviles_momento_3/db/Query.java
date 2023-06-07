package com.example.aplicaciones_moviles_momento_3.db;

public class Query {
    private final String selection;
    private final String[] args;

    public Query(String selection, String[] args) {
        this.selection = selection;
        this.args = args;
    }

    public String getSelection() {
        return selection;
    }

    public String[] getArgs() {
        return args;
    }
}