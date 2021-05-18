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

public class AdaptadorFonte extends RecyclerView.Adapter<AdaptadorFonte.FontViewHolder>
{
    public static final String NOME_FONTE_UM = "Cheque-Black.otf";
    public static final String NOME_FONTE_DOIS = "Cheque-Regular.otf";
    public static final String NOME_FONTE_TRES = "viper_nora.otf";
    private Context context;
    private AdaptadorFonteListener listener;
    private List<String> fontes;
    private int linha_selecionada = -1;

    public AdaptadorFonte(Context context, AdaptadorFonteListener listener)
    {
        this.context = context;
        this.listener = listener;
        this.fontes = carregarFontes();
    }

    private List<String> carregarFontes()
    {
        List<String> listaFontes = new ArrayList<>();

        listaFontes.add(NOME_FONTE_UM);
        listaFontes.add(NOME_FONTE_DOIS);
        listaFontes.add(NOME_FONTE_TRES);

        return listaFontes;
    }


    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.font_item, parent, false);
        return new FontViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position)
    {
        if (linha_selecionada == position)
        {
            holder.img_check.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.img_check.setVisibility(View.INVISIBLE);
        }

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), String.valueOf(new StringBuilder("fonts/")
                .append(fontes.get(position).toString())));

        holder.nome_fonte.setText(fontes.get(position));
        holder.demo_fonte.setTypeface(typeface);


    }

    @Override
    public int getItemCount()
    {
        return fontes.size();
    }

    public class FontViewHolder extends RecyclerView.ViewHolder
    {
        TextView nome_fonte, demo_fonte;
        ImageView img_check;

        public FontViewHolder(@NonNull View itemView)
        {
            super(itemView);

            nome_fonte = itemView.findViewById(R.id.txt_nome_fonte);
            demo_fonte = itemView.findViewById(R.id.txt_fonte_demo);
            img_check = itemView.findViewById(R.id.img_check);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.aoSelecionarFonte(fontes.get(getAdapterPosition()));
                    linha_selecionada = getAdapterPosition();

                    notifyDataSetChanged();
                }
            });
        }
    }

    public interface AdaptadorFonteListener
    {
        void aoSelecionarFonte(String nome);
    }
}

