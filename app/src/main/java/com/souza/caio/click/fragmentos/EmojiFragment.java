package com.souza.caio.click.fragmentos;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.souza.caio.click.adaptadores.AdaptadorEmoji;
import com.souza.caio.click.sensores.EmojiFragmentListener;
import com.souza.caio.click.R;

import ja.burhanrashid52.photoeditor.PhotoEditor;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmojiFragment extends BottomSheetDialogFragment implements AdaptadorEmoji.EmojiAdapterListener
{

    private RecyclerView recyclerView;
    static EmojiFragment instance;

    private EmojiFragmentListener listener;

    private View view;

    public EmojiFragment()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_emoji, container, false);

        iniciarComponentes();
        configurarAdapter();

        return view;
    }

    private void iniciarComponentes()
    {
        recyclerView = view.findViewById(R.id.recycler_emoji);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));

    }

    private void configurarAdapter()
    {
        AdaptadorEmoji adapter = new AdaptadorEmoji(getContext(), PhotoEditor.getEmojis(getContext()), this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void aoSelecionarEmoji(String emoji)
    {
        listener.aoSelecionarEmoji(emoji);
    }

    public void setListener(EmojiFragmentListener listener)
    {
        this.listener = listener;
    }

    public static EmojiFragment getInstance()
    {
        if (instance == null)
            instance = new EmojiFragment();
        return instance;
    }

}
