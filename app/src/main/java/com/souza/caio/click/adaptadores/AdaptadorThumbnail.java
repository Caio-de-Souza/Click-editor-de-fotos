package com.souza.caio.click.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.souza.caio.click.sensores.ListaFiltrosFragmentListener;
import com.souza.caio.click.R;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class AdaptadorThumbnail extends RecyclerView.Adapter<AdaptadorThumbnail.MyViewHolder>
{
    private List<ThumbnailItem> thumbnailItems;
    private ListaFiltrosFragmentListener listener;
    private Context context;
    private int indexSelecionado = 0;

    public AdaptadorThumbnail(List<ThumbnailItem> thumbnailItems, ListaFiltrosFragmentListener listener, Context context)
    {
        this.thumbnailItems = thumbnailItems;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.thumbnail_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position)
    {
        final ThumbnailItem thumbnailItem = thumbnailItems.get(position);

        holder.thumb.setImageBitmap(thumbnailItem.image);
        holder.thumb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                listener.aoSelecionarFiltro(thumbnailItem.filter);
                indexSelecionado = position;
                notifyDataSetChanged();
            }
        });

        holder.nome_filtro.setText(thumbnailItem.filterName);

        configurarSelecaoThumbnail(holder, position);
    }

    private void configurarSelecaoThumbnail(@NonNull MyViewHolder holder, int position)
    {
        if (indexSelecionado == position)
        {
            holder.nome_filtro.setTextColor(ContextCompat.getColor(context, R.color.filtro_selecionado));
        }
        else
        {
            holder.nome_filtro.setTextColor(ContextCompat.getColor(context, R.color.filtro_normal));
        }
    }

    @Override
    public int getItemCount()
    {
        return thumbnailItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView thumb;
        TextView nome_filtro;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            thumb = itemView.findViewById(R.id.thumb);
            nome_filtro = itemView.findViewById(R.id.nome_filtro);
        }
    }
}
