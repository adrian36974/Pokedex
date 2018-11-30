package com.adrian.tecmilenio.pokedex.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrian.tecmilenio.pokedex.R;
import com.adrian.tecmilenio.pokedex.models.Pokemon;

public class PokemonDetailActivity extends AppCompatActivity {

    private ImageView ivImage;
    private TextView tvName;
    private TextView tvID;
    String pokemon;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivImage = (ImageView) findViewById(R.id.iv_image);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvID = (TextView) findViewById(R.id.tv_id);

        Typeface pokemon_font = Typeface.createFromAsset(getAssets(),  "fonts/Pokemon_Solid.ttf");

        tvName.setTypeface(pokemon_font);
        tvID.setTypeface(pokemon_font);

        Intent i= getIntent();
        Bundle b = i.getExtras();

        if(b!=null)
        {
            if(getIntent().hasExtra("POKEMON_IMAGE")) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("POKEMON_IMAGE"), 0, getIntent().getByteArrayExtra("POKEMON_IMAGE").length);
                ivImage.setImageBitmap(bitmap);
            }
            pokemon = String.valueOf(b.get("POKEMON_NAME")).toUpperCase();
            pokemon = pokemon.substring(0,2) + String.valueOf(b.get("POKEMON_NAME")).substring(2);
            tvName.setText(pokemon);
            id = (int) b.get("POKEMON_ID");
            tvID.setText(String.valueOf(id));

            Log.d("DEBUG", pokemon + "POKE");
            this.setTitle(pokemon);
            //buscarDatos(id);
        }

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
}

