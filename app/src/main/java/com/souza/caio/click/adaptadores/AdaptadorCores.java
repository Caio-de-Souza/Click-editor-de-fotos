package com.souza.caio.click.adaptadores;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.souza.caio.click.R;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorCores extends RecyclerView.Adapter<AdaptadorCores.ViewHolder>
{
    private Context context;
    private List<Integer> cores;
    private AdaptadorCoresListener coresListener;


    public AdaptadorCores(Context context, AdaptadorCoresListener coresListener)
    {
        this.context = context;
        this.cores = gerarListaCores();
        this.coresListener = coresListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(context).inflate(R.layout.color_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.palheta_cor.setCardBackgroundColor(cores.get(position));
    }

    @Override
    public int getItemCount()
    {
        return cores.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CardView palheta_cor;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            palheta_cor = itemView.findViewById(R.id.lista_cores);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    coresListener.aoSelecionarCor(cores.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface AdaptadorCoresListener
    {
        void aoSelecionarCor(int cor);
    }

    private List<Integer> gerarListaCores()
    {
        List<Integer> colorList = new ArrayList<>();

        colorList.add(Color.parseColor("#131722"));
        colorList.add(Color.parseColor("#ff545e"));
        colorList.add(Color.parseColor("#57bb82"));
        colorList.add(Color.parseColor("#dbeeff"));
        colorList.add(Color.parseColor("#ba5796"));
        colorList.add(Color.parseColor("#bb349b"));
        colorList.add(Color.parseColor("#d8cfc0"));
        colorList.add(Color.parseColor("#f1e7d6"));
        colorList.add(Color.parseColor("#b7b2ad"));
        colorList.add(Color.parseColor("#b7adad"));
        colorList.add(Color.parseColor("#a59999"));
        colorList.add(Color.parseColor("#fff5d0"));
        colorList.add(Color.parseColor("#fff0b6"));
        colorList.add(Color.parseColor("#eeb932"));
        colorList.add(Color.parseColor("#ffbf00"));
        colorList.add(Color.parseColor("#5d6c53"));
        colorList.add(Color.parseColor("#5f775f"));
        colorList.add(Color.parseColor("#0f5e63"));
        colorList.add(Color.parseColor("#5eb246"));
        colorList.add(Color.parseColor("#07c4c5"));
        colorList.add(Color.parseColor("#d8cfc0"));
        colorList.add(Color.parseColor("#027fdc"));
        colorList.add(Color.parseColor("#daf8e3"));
        colorList.add(Color.parseColor("#97ebdb"));
        colorList.add(Color.parseColor("#00c2c7"));
        colorList.add(Color.parseColor("#0086ad"));
        colorList.add(Color.parseColor("#005582"));
        colorList.add(Color.parseColor("#d8cfc0"));


        return colorList;
    }

}
