package com.souza.caio.click.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.souza.caio.click.R;

import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;

public class AdaptadorEmoji extends RecyclerView.Adapter<AdaptadorEmoji.EmojiViewHolder>
{
    private Context context;
    private List<String> emojis;
    private EmojiAdapterListener listener;

    public AdaptadorEmoji(Context context, List<String> emojis, EmojiAdapterListener listener)
    {
        this.context = context;
        this.emojis = emojis;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EmojiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.emoji_item, parent, false);

        return new EmojiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmojiViewHolder holder, int position)
    {
        holder.emojiIconTextView.setText(emojis.get(position));
    }

    @Override
    public int getItemCount()
    {
        return emojis.size();
    }


    public class EmojiViewHolder extends RecyclerView.ViewHolder
    {
        EmojiconTextView emojiIconTextView;

        public EmojiViewHolder(@NonNull View itemView)
        {
            super(itemView);

            emojiIconTextView = itemView.findViewById(R.id.emoji_text_view);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.aoSelecionarEmoji(emojis.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface EmojiAdapterListener
    {
        void aoSelecionarEmoji(String emoji);
    }
}
