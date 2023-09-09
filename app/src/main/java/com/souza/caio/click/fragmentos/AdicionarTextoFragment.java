package com.souza.caio.click.fragmentos;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.souza.caio.click.adaptadores.AdapterColors;
import com.souza.caio.click.adaptadores.AdapterFont;
import com.souza.caio.click.sensores.AdicionarTextoFragmentListener;
import com.souza.caio.click.R;

public class AdicionarTextoFragment extends BottomSheetDialogFragment implements AdapterColors.AdaptadorCoresListener, AdapterFont.AdaptadorFonteListener {
    private View view;
    private int corSelecionada = Color.parseColor("#000000");

    private AdicionarTextoFragmentListener listener;

    private EditText texto;
    private RecyclerView lista_cores;
    private Button btnFinalzar;
    private Typeface typefaceSelecionada = Typeface.DEFAULT;

    private static AdicionarTextoFragment instance;

    public static AdicionarTextoFragment getInstance() {
        if (instance == null){
            instance = new AdicionarTextoFragment();
        }
        return instance;
    }

    public void setListener(AdicionarTextoFragmentListener listener) {
        this.listener = listener;
    }

    public AdicionarTextoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("AdicionarTextoFragment", "onCreateView");
        view = inflater.inflate(R.layout.fragment_adicionar_texto, container, false);
        iniciarComponentes();
        configurarBotoes();

        return view;
    }

    private void configurarBotoes() {
        configurarListaCores();
        configurarRecyclerView();
        configurarCliqueBotaoFinalizar();
    }

    public void iniciarComponentes() {
        texto = view.findViewById(R.id.edTxt_Add_texto);
        btnFinalzar = view.findViewById(R.id.btn_finalizar_Add_texto);
        lista_cores = view.findViewById(R.id.recycler_cores_texto);
    }

    private void configurarCliqueBotaoFinalizar() {
        this.btnFinalzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.aoClicarBtnAddTexto(typefaceSelecionada, texto.getText().toString().trim(), corSelecionada);
            }
        });
    }

    private void configurarListaCores() {
        lista_cores.setHasFixedSize(true);
        lista_cores.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        AdapterColors adaptadorCores = new AdapterColors(getContext(), this);
        lista_cores.setAdapter(adaptadorCores);
    }

    private void configurarRecyclerView() {
        RecyclerView recycler_fontes = view.findViewById(R.id.recycler_fonte_texto);
        recycler_fontes.setHasFixedSize(true);
        recycler_fontes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        AdapterFont adaptadorFonte = new AdapterFont(getContext(), this);
        recycler_fontes.setAdapter(adaptadorFonte);
    }


    @Override
    public void onSelectColor(int cor) {
        corSelecionada = cor;
    }

    @Override
    public void onSelectFont(String nome) {
        typefaceSelecionada = Typeface.createFromAsset(getContext().getAssets(), String.valueOf(new StringBuilder("fonts/")
                .append(nome)));
    }

    public void resetarAlteracoes(){
        texto.setText("", EditText.BufferType.EDITABLE);
        corSelecionada = Color.parseColor("#000000");
        typefaceSelecionada = Typeface.DEFAULT;
    }
}
