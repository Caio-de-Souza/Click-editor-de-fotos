package com.souza.caio.click.adaptadores;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DecoracaoEspaco extends RecyclerView.ItemDecoration
{
    private int espaco;

    public DecoracaoEspaco(int espaco)
    {
        this.espaco = espaco;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1)
        {
            outRect.left = espaco;
            outRect.right = 0;
        }
        else
        {
            outRect.left = 0;
            outRect.right = espaco;
        }
    }
}
