package com.adrian.tecmilenio.pokedex.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrian.tecmilenio.pokedex.R;
import com.adrian.tecmilenio.pokedex.activities.PokeGame;
import com.adrian.tecmilenio.pokedex.activities.PokemonDetailActivity;
import com.adrian.tecmilenio.pokedex.models.Pokemon;
import com.bumptech.glide.*;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ListaPokemonAdapter extends RecyclerView.Adapter<ListaPokemonAdapter.ViewHolder> {

    private ArrayList<Pokemon> dataset;
    private Context context;
    public Pokemon p;
    private String pokemonName;
    public static int PokeHits = 0;
    public static int countPokemon = 0;
    public static int [] lastPokemon = new int [5];

    public ListaPokemonAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (context.getClass().getSimpleName().toString().equals("PokeGame")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        p = dataset.get(position);
        if (context.getClass().getSimpleName().toString().equals("PokemonListActivity")) {
            holder.nombreTextView.setText(p.getName().substring(0, 1).toUpperCase() + p.getName().substring(1));
        }

        Glide
                .with(context)
                .load("http://pokeapi.co/media/sprites/pokemon/" + p.getNumber() + ".png")
                .apply(RequestOptions.centerCropTransform()
                        .error(R.drawable.pokebola).centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH))
                .transition(withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (context.getClass().getSimpleName().toString().equals("PokeGame")) {
                            countPokemon++;
                            if (position < 5)
                            {
                                Log.d("ID POKEMON", "" + Arrays.toString(lastPokemon));
                                Bitmap bitmap = drawableToBitmap(resource);
                                //if (!Arrays.asList(lastPokemon).contains(position+1)) {
                                if (lastPokemon[position] != (position+1)) {
                                    holder.nombreTextView.setText("?");
                                    holder.nombreTextView.setTextColor(Color.BLACK);
                                    holder.nombreTextView.setShadowLayer(1.5f, -2, 2, Color.DKGRAY);
                                    bitmap = SetBrightness(bitmap, -255);
                                }
                                lastPokemon[position] = position + 1;
                                Log.d("ID POKEMON", "" + lastPokemon[position]);
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);
                                holder.fotoImageView2.setImageBitmap(bitmap);
                                PokeGame.Pokemon = p.getName();
                                PokeGame.ready = true;
                                PokeGame.send.setEnabled(false);
                                if (countPokemon == 1) {
                                    PokeGame.button.setEnabled(true);
                                } else {
                                    PokeGame.send.setEnabled(true);
                                    PokeGame.BeginCounter();
                                    PokeGame.drawer.start(PokeGame.TIMER_LENGTH);
                                }
                                PokeGame.ivPokemon.setImageBitmap(bitmap);
                                PokeGame.send.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        pokemonName = PokeGame.name.getText().toString();
                                        pokemonName = pokemonName.toLowerCase();
                                        if (p.getName().equals(pokemonName)) {
                                            PokeGame.Pokemons++;
                                            PokeHits++;
                                            Snackbar.make(v, "Es correcto!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            holder.nombreTextView.setText(p.getName().substring(0,1).toUpperCase() + p.getName().substring(1));
                                            holder.nombreTextView.setTextColor(Color.parseColor("#ffcb05"));
                                            holder.nombreTextView.setShadowLayer(1.5f, -2, 2, Color.parseColor("#3c5aa6"));
                                            holder.fotoImageView2.setImageDrawable(holder.fotoImageView.getDrawable());
                                            PokeGame.ivPokemon.setImageDrawable(holder.fotoImageView2.getDrawable());
                                            PokeGame.drawer.stop();
                                            PokeGame.BeginCounter();
                                            PokeGame.timer.cancel();
                                            PokeGame.button.setEnabled(false);
                                            PokeGame.c.setText("Tiempo agotado.");
                                            Random r = new Random();
                                            do {
                                                PokeGame.pokerandom[PokeGame.Pokemons -1] = r.nextInt(151 - 1) + 1;
                                            }while(Arrays.asList(PokeGame.pokerandom).contains(p.getNumber()));
                                            if (PokeGame.Pokemons < 5) {
                                                PokeGame.obtenerPokemon(Integer.toString(PokeGame.pokerandom[PokeGame.Pokemons -1]));
                                            }
                                            if (PokeGame.Pokemons == 5) {
                                                PokeGame.ShowAlert(context, PokeHits);
                                            }
                                            PokeGame.name.setText("");
                                            PokeGame.send.setEnabled(false);
                                        } else {
                                            Snackbar.make(v, "Erraste, intenta de nuevo!", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();
                                            PokeGame.name.setText("");
                                        }
                                    }
                                });
                            }
                        }
                        return false;
                    }
                })
                .into(holder.fotoImageView);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void adicionarListaPokemon(ArrayList<Pokemon> listaPokemon) {
        dataset.addAll(listaPokemon);
        notifyDataSetChanged();
    }

    public void limpiarListaPokemon() {
        dataset.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView fotoImageView;
        public ImageView fotoImageView2;
        public TextView nombreTextView;
        public CardView items;

        public ViewHolder(View itemView) {
            super(itemView);

            fotoImageView = (ImageView) itemView.findViewById(R.id.fotoImageView);
            fotoImageView2 = (ImageView) itemView.findViewById(R.id.fotoImageView2);
            nombreTextView = (TextView) itemView.findViewById(R.id.nombreTextView);

            Typeface pokemon_font = Typeface.createFromAsset(context.getAssets(),"fonts/Pokemon_Solid.ttf");

            nombreTextView.setTypeface(pokemon_font);

            items = (CardView) itemView.findViewById(R.id.items);
            items.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            fotoImageView = (ImageView) itemView.findViewById(R.id.fotoImageView);
            p = dataset.get(getAdapterPosition());
            switch (v.getId()) {
                case R.id.items:
                    Intent i = new Intent(v.getContext(), PokemonDetailActivity.class);
                    Bitmap bitmap = drawableToBitmap(fotoImageView.getDrawable());
                    if (nombreTextView.getText().equals("?")){
                        bitmap = drawableToBitmap(fotoImageView2.getDrawable());
                    }
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bs);
                    i.putExtra("POKEMON_ID", p.getNumber());
                    i.putExtra("POKEMON_NAME", " " + nombreTextView.getText() + " ");
                    //i.putExtra("POKEMON_NAME", " " + p.getName() + " ");
                    i.putExtra("POKEMON_IMAGE", bs.toByteArray());
                    v.getContext().startActivity(i);
                    break;
            }
        }
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final int width = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().height() : drawable.getIntrinsicHeight();

        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width,
                height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);

        Log.v("Bitmap width - Height :", width + " : " + height);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap SetBrightness(Bitmap src, int value) {
        int width = src.getWidth();
        int height = src.getHeight();
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        int A, R, G, B;
        int pixel;

        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                R += value;
                if(R > 255) { R = 255; }
                else if(R < 0) { R = 0; }

                G += value;
                if(G > 255) { G = 255; }
                else if(G < 0) { G = 0; }

                B += value;
                if(B > 255) { B = 255; }
                else if(B < 0) { B = 0; }

                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }
}