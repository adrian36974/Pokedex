package com.adrian.tecmilenio.pokedex.models;

import android.util.Log;

import java.util.ArrayList;

public class PokemonRespuesta {

    private ArrayList<Pokemon> results;
    private ArrayList<Pokemon> forms;

    public ArrayList<Pokemon> getResults() {
        return results;
    }

    public void setResults(ArrayList<Pokemon> results) {
        this.results = results;
    }

    public ArrayList<Pokemon> getForms() {
        return forms;
    }

    public void setForms(ArrayList<Pokemon> forms) {
        this.forms = forms;
    }

}
