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

public class AdapterThumbnail extends RecyclerView.Adapter<AdapterThumbnail.ThumbnailViewHolder> {
    private List<ThumbnailItem> thumbnailItems;
    private ListaFiltrosFragmentListener listener;
    private Context context;
    private int selectedIndex = 0;

    public AdapterThumbnail(List<ThumbnailItem> thumbnailItems, ListaFiltrosFragmentListener listener, Context context) {
        this.thumbnailItems = thumbnailItems;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.thumbnail_item, parent, false);
        return new ThumbnailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
        ThumbnailItem thumbnailItem = thumbnailItems.get(position);
        int positionTemp = position;

        holder.thumb.setImageBitmap(thumbnailItem.image);
        holder.thumb.setOnClickListener(v -> {
            listener.aoSelecionarFiltro(thumbnailItem.filter);
            selectedIndex = positionTemp;
            notifyDataSetChanged();
        });

        holder.thumbName.setText(thumbnailItem.filterName);
        configurarSelecaoThumbnail(holder, position);
    }

    private void configurarSelecaoThumbnail(@NonNull ThumbnailViewHolder holder, int position) {
        if (selectedIndex == position) {
            holder.thumbName.setTextColor(ContextCompat.getColor(context, R.color.filtro_selecionado));
        } else {
            holder.thumbName.setTextColor(ContextCompat.getColor(context, R.color.filtro_normal));
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItems.size();
    }

    public class ThumbnailViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView thumbName;

        public ThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);

            thumb = itemView.findViewById(R.id.thumb);
            thumbName = itemView.findViewById(R.id.thumb_name);
        }
    }
}
