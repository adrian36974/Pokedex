package com.adrian.tecmilenio.pokedex.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrian.tecmilenio.pokedex.R;
import com.adrian.tecmilenio.pokedex.adapter.ListaPokemonAdapter;
import com.adrian.tecmilenio.pokedex.api.PokeapiService;
import com.adrian.tecmilenio.pokedex.models.Pokemon;
import com.adrian.tecmilenio.pokedex.models.PokemonRespuesta;
import com.adrian.tecmilenio.pokedex.views.Drawer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokeGame extends AppCompatActivity {
    public static final String TAG = "POKEDEX";
    public static String Pokemon;
    public static boolean ready = false;
    public static Button button;
    public static int Pokemons = 0;
    public static int PokeError = 0;
    public static ImageView ivPokemon;
    static boolean active = false;
    public static Retrofit retrofit;
    public static final int TIMER_LENGTH = 20;
    public static Drawer drawer;
    private String pokemonName;
    public static EditText name;
    View vi, circle, zoneplay;
    public static TextView a,b,c;
    public static Button send;
    public static CountDownTimer timer;
    int intentos, letter;
    public static int [] pokerandom = new int [5];
    private RecyclerView recyclerView;
    public static ListaPokemonAdapter listaPokemonAdapter;
    public static boolean aptoParaCargar;
    public static Activity pg;
    private static Context context;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poke_game);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pg = this;
        context = PokeGame.this;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        listaPokemonAdapter = new ListaPokemonAdapter(this);
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.salestock.net/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        aptoParaCargar = false;
        Random r = new Random();
        pokerandom[0] = r.nextInt(151 - 1) + 1;
        PokeGame.obtenerPokemon(Integer.toString(pokerandom[0]));

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        letter = 0;
        intentos =0;
        button = (Button) findViewById(R.id.btnClick);
        ivPokemon = (ImageView) findViewById(R.id.ivPokemon);
        name = (EditText) findViewById(R.id.txtPokemon);
        send = (Button) findViewById(R.id.btnSendName);
        c = (TextView) findViewById(R.id.txtTime);
        zoneplay = (View) findViewById(R.id.zonePlay);
        circle = (View) findViewById(R.id.Circle);

        send.setEnabled(false);
        button.setEnabled(false);

        drawer = (Drawer) findViewById(R.id.timer);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                button.setEnabled(false);
                send.setEnabled(true);
                BeginCounter();
                vi = getWindow().getDecorView().getRootView();
                drawer.start(TIMER_LENGTH);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View parentLayout = findViewById(android.R.id.content);
                pokemonName = name.getText().toString();
                pokemonName = pokemonName.toLowerCase();
                if (Pokemon.equals(pokemonName)) {
                    Snackbar.make(parentLayout, "Es correcto!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void activateButtons(Boolean activate) {
        Button button = (Button) findViewById(R.id.btnClick);
        button.setEnabled(false);
    }

    public static void BeginCounter(){
        if (timer != null) {
            timer.cancel();
        }
         timer = new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                long time = (millisUntilFinished / 1000) - 1;
                c.setText(time+2 + " segundos");
            }

            @SuppressLint("ResourceAsColor")
            public void onFinish() {
                send.setEnabled(false);
                Pokemons++;
                PokeError++;
                c.setText("Tiempo agotado.");
                if (Pokemons == 5) {
                    ShowAlert(context, (5-PokeError));
                }
                if (Pokemons < 5) {
                    Random r = new Random();
                    obtenerPokemon(Integer.toString(r.nextInt(151 - 1) + 1));
                    //ChangeColorBackground();
                }
            }

        }.start();
    }

    public static void ShowAlert(final Context context, int Hits){
        String message = "";
        switch (Hits) {
            case 0:  message = "¡Que mal, 0 aciertos, más suerte para la próxima! ¿Quieres jugar de nuevo?";
                break;
            case 1:  message = "¡Solo 1 acierto, más suerte para la próxima! ¿Quieres jugar de nuevo?";
                break;
            case 2:  message = "¡2 de 5, no está mal, quizá la próxima mejores! ¿Quieres jugar de nuevo?";
                break;
            case 3:  message = "¡3 de 5, vaya que has visto mucho Pokémon! ¿Quieres jugar de nuevo?";
                break;
            case 4:  message = "¡Uf, cerca, solo un error, solo un poco más! ¿Quieres jugar de nuevo?";
                break;
            case 5:  message = "¡Eres todo un maestro Pokémon, acertaste todo! ¿Quieres jugar de nuevo?";
                break;
            default: message = "¿Quieres jugar de nuevo?";
                break;
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Sí",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PokeGame.pg.finish();
                        dialog.cancel();
                        Intent i = new Intent(context, PokeGame.class);
                        context.startActivity(i);
                        Pokemons = 0;
                        PokeError = 0;
                        listaPokemonAdapter.limpiarListaPokemon();
                        ListaPokemonAdapter.countPokemon = 0;
                        Arrays.fill( ListaPokemonAdapter.lastPokemon, 0 );
                        ListaPokemonAdapter.PokeHits = 0;
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        PokeGame.pg.finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public void ChangeColorBackground(){

        new CountDownTimer(1500, 100) {
            int a = 0;
            public void onTick(long millisUntilFinished) {
                if (a == 0){
                    vi.setBackgroundColor(Color.argb(255, 255, 0, 0));
                    a = 1;
                }else{
                    vi.setBackgroundColor(Color.argb(255, 0, 0, 255));
                    a = 0;
                }
            }

            public void onFinish() {
                vi.setBackgroundColor(Color.argb(255, 255, 255, 255));
            }
        }.start();
    }

    public static void obtenerPokemon(String pokemon) {
        PokeapiService service = retrofit.create(PokeapiService.class);
        Log.d("listPokes", "yes");
        Call<PokemonRespuesta> pokemonRespuestaCall = service.buscarPokemon(pokemon);
        final String[] name = {""};
        pokemonRespuestaCall.enqueue(new Callback<PokemonRespuesta>() {
            @Override
            public void onResponse(Call<PokemonRespuesta> call, Response<PokemonRespuesta> response) {
                aptoParaCargar = true;
                //View parentLayout = findViewById(android.R.id.content);
                if (response.isSuccessful()) {
                    //Snackbar.make(parentLayout, "Se ha encontrado un Pokémon!", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
                    PokemonRespuesta pokemonRespuesta = response.body();
                    ArrayList<Pokemon> listaPokemon = pokemonRespuesta.getForms();

                    listaPokemonAdapter.adicionarListaPokemon(listaPokemon);

                    Log.d("listPokes", listaPokemon.toString().toUpperCase());


                    name[0] = listaPokemon.get(0).getName().toUpperCase();
                    //PokemonListActivity.this.setTitle(name[0].substring(0,1) + listaPokemon.get(0).getName().substring(1));

                } else {
                    Log.e(TAG, " onResponse: " + response.errorBody());
                    //Snackbar.make(parentLayout, "No se encontró ningún Pokémon", Snackbar.LENGTH_LONG)
                      //      .setAction("Action", null).show();
                }
            }

            @Override
            public void onFailure(Call<PokemonRespuesta> call, Throwable t) {
                aptoParaCargar = true;
                Log.e(TAG, " onFailure: " + t.getMessage());
            }
        });
    }


}
