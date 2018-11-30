package com.adrian.tecmilenio.pokedex.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.adrian.tecmilenio.pokedex.R;
import com.adrian.tecmilenio.pokedex.adapter.ListaPokemonAdapter;
import com.adrian.tecmilenio.pokedex.api.PokeapiService;
import com.adrian.tecmilenio.pokedex.models.Pokemon;
import com.adrian.tecmilenio.pokedex.models.PokemonRespuesta;
import com.adrian.tecmilenio.pokedex.services.BackgroundService;

public class PokemonListActivity extends AppCompatActivity {

    private static final String TAG = "POKEDEX";

    private Retrofit retrofit;

    private RecyclerView recyclerView;
    private ListaPokemonAdapter listaPokemonAdapter;

    private int offset;

    private boolean aptoParaCargar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_list);
        startService(new Intent(PokemonListActivity.this,BackgroundService.class));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent i = new Intent(view.getContext(), PokeGame.class);
                view.getContext().startActivity(i);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        listaPokemonAdapter = new ListaPokemonAdapter(this);
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if (aptoParaCargar) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            Log.i(TAG, " Llegamos al final.");

                            aptoParaCargar = false;
                            offset += 20;
                            obtenerDatos(offset);
                        }
                    }
                }
            }
        });


        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.salestock.net/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        aptoParaCargar = true;
        offset = 0;
        obtenerDatos(offset);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_poke_list, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarPokemon(query.toLowerCase());
                searchView.clearFocus();
                searchView.setQuery("", false);
                searchView.setIconified(true);
                searchItem.collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    private void obtenerDatos(int offset) {
        PokeapiService service = retrofit.create(PokeapiService.class);
        Call<PokemonRespuesta> pokemonRespuestaCall = service.obtenerListaPokemon(20, offset);

        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                aptoParaCargar = true;
                if (response.isSuccessful()) {

                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon> listaPokemon = pokemonRespuesta.getResults();

                    listaPokemonAdapter.adicionarListaPokemon(listaPokemon);

                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                aptoParaCargar = true;
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }

    private void obtenerPokemon(String pokemon) {
        PokeapiService service = retrofit.create(PokeapiService.class);
        Log.d("listPokes", "yes");
        Call<PokemonRespuesta> pokemonRespuestaCall = service.buscarPokemon(pokemon);
        final String[] name = {""};
        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                aptoParaCargar = true;
                View parentLayout = findViewById(android.R.id.content);
                if (response.isSuccessful()) {
                    Snackbar.make(parentLayout, "Se ha encontrado un Pokémon!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon> listaPokemon = pokemonRespuesta.getForms();

                    listaPokemonAdapter.adicionarListaPokemon(listaPokemon);

                    Log.d("listPokes", listaPokemon.toString().toUpperCase());


                    name[0] = listaPokemon.get(0).getName().toUpperCase();
                    PokemonListActivity.this.setTitle(name[0].substring(0,1) + listaPokemon.get(0).getName().substring(1));

                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                    Snackbar.make(parentLayout, "No se encontró ningún Pokémon", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    final GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            if (dy > 0) {
                                int visibleItemCount = layoutManager.getChildCount();
                                int totalItemCount = layoutManager.getItemCount();
                                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                                if (aptoParaCargar) {
                                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                        Log.i(TAG, " Llegamos al final.");

                                        aptoParaCargar = false;
                                        offset += 20;
                                        obtenerDatos(offset);
                                    }
                                }
                            }
                        }
                    });
                    obtenerDatos(0);
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                aptoParaCargar = true;
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }

    private void buscarPokemon(String pokemon) {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        listaPokemonAdapter = new ListaPokemonAdapter(this);
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        obtenerPokemon(pokemon);
    }
}
