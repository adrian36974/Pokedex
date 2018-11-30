package com.adrian.tecmilenio.pokedex.api;

import com.adrian.tecmilenio.pokedex.models.PokemonRespuesta;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokeapiService {
    @GET("pokemon")
    Call<PokemonRespuesta> obtenerListaPokemon(@Query("limit") int limit, @Query("offset") int offset);

    @GET("pokemon/{pokemon}")
    Call<PokemonRespuesta> buscarPokemon(@Path("pokemon") String pokemon);
}
