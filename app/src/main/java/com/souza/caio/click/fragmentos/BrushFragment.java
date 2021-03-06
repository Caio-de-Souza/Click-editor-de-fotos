package com.souza.caio.click.fragmentos;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.souza.caio.click.adaptadores.AdaptadorCores;
import com.souza.caio.click.sensores.BrushFragmentListener;
import com.souza.caio.click.R;

public class BrushFragment extends BottomSheetDialogFragment implements AdaptadorCores.AdaptadorCoresListener
{
    private View view;
    private SeekBar espessura, opacidade;
    private RecyclerView recyclerView_cores;
    private ToggleButton btn_estado_pincel;
    private AdaptadorCores adaptadorCores;
    private BrushFragmentListener listener;

    static BrushFragment instance;

    public static BrushFragment getInstance()
    {
        if (instance == null)
            instance = new BrushFragment();
        return instance;
    }

    public void setListener(BrushFragmentListener listener)
    {
        this.listener = listener;
    }

    public BrushFragment()
    {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_brush, container, false);

        iniciarComponentes();
        configurarComponentes();

        return view;
    }

    private void configurarComponentes()
    {
        configurarRecyclerView();
        configurarEspessura();
        configurarOpacidade();
        configurarBotaoEstadoPincel();
    }

    public void iniciarComponentes()
    {
        espessura = view.findViewById(R.id.seekbar_espessura_pincel);
        opacidade = view.findViewById(R.id.seekbar_opacidade_pincel);
        recyclerView_cores = view.findViewById(R.id.recycler_cores);
        btn_estado_pincel = view.findViewById(R.id.btn_estado_pincel);
        recyclerView_cores.setHasFixedSize(true);
    }

    private void configurarBotaoEstadoPincel()
    {
        btn_estado_pincel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                listener.aoMudarEstadoPincel(isChecked);
            }
        });
    }

    private void configurarOpacidade()
    {
        opacidade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                listener.aoMudarOpacidadePincel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    private void configurarEspessura()
    {
        espessura.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                listener.aoMudarEspessuraPincel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });
    }

    private void configurarRecyclerView()
    {
        recyclerView_cores.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adaptadorCores = new AdaptadorCores(getContext(), this);
        recyclerView_cores.setAdapter(adaptadorCores);
    }


    @Override
    public void aoSelecionarCor(int cor)
    {
        listener.aoMudarCor(cor);
    }
}
