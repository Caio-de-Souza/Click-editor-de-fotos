package com.souza.caio.click.adaptadores;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.souza.caio.click.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterFont extends RecyclerView.Adapter<AdapterFont.FontViewHolder> {
    public static final String NAME_FONT_ONE = "Cheque-Black.otf";
    public static final String NAME_FONT_TWO = "Cheque-Regular.otf";
    public static final String NAME_FONT_THREE = "viper_nora.otf";
    private Context context;
    private AdaptadorFonteListener listener;
    private List<String> fonts;
    private int selectedLine = -1;

    public AdapterFont(Context context, AdaptadorFonteListener listener) {
        this.context = context;
        this.listener = listener;
        this.fonts = loadFonts();
    }

    private List<String> loadFonts() {
        List<String> listaFontes = new ArrayList<>();
        listaFontes.add(NAME_FONT_ONE);
        listaFontes.add(NAME_FONT_TWO);
        listaFontes.add(NAME_FONT_THREE);
        return listaFontes;
    }

    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.font_item, parent, false);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {
        if (selectedLine == position) {
            holder.img_check.setVisibility(View.VISIBLE);
        } else {
            holder.img_check.setVisibility(View.INVISIBLE);
        }

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), String.valueOf(new StringBuilder("fonts/")
                .append(fonts.get(position).toString())));

        holder.nameFont.setText(fonts.get(position));
        holder.demoFont.setTypeface(typeface);
    }

    @Override
    public int getItemCount() {
        return fonts.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder {
        TextView nameFont, demoFont;
        ImageView img_check;

        public FontViewHolder(@NonNull View itemView) {
            super(itemView);

            nameFont = itemView.findViewById(R.id.txt_name_font);
            demoFont = itemView.findViewById(R.id.txt_demo_font);
            img_check = itemView.findViewById(R.id.img_check);

            itemView.setOnClickListener(v -> {
                listener.onSelectFont(fonts.get(getAdapterPosition()));
                selectedLine = getAdapterPosition();
                notifyDataSetChanged();
            });
        }
    }

    public interface AdaptadorFonteListener {
        void onSelectFont(String nome);
    }
}

