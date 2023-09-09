package com.souza.caio.click.fragmentos;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.souza.caio.click.sensores.EditarImagemFragmentListener;
import com.souza.caio.click.R;

public class EditarImagemFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    private View view;
    private EditarImagemFragmentListener listener;
    private SeekBar brilho, contraste, saturacao;

    static EditarImagemFragment instance;

    public EditarImagemFragment() {
    }

    public static EditarImagemFragment getInstance() {
        if (instance == null)
            instance = new EditarImagemFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_editar_imagem, container, false);

        iniciarComponentes();
        configurarParametros();

        return view;
    }

    public void iniciarComponentes() {
        brilho = view.findViewById(R.id.seekbar_brilho);
        contraste = view.findViewById(R.id.seekbar_constraste);
        saturacao = view.findViewById(R.id.seekbar_saturacao);
    }

    private void configurarParametros() {
        brilho.setMax(200);
        brilho.setProgress(100);

        contraste.setMax(20);
        contraste.setProgress(0);

        saturacao.setMax(30);
        saturacao.setProgress(10);

        brilho.setOnSeekBarChangeListener(this);
        contraste.setOnSeekBarChangeListener(this);
        saturacao.setOnSeekBarChangeListener(this);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i("onProgressChanged", seekBar.getId() + " - " +progress);
        if (listener != null) {
            if (seekBar.getId() == R.id.seekbar_brilho) {
                listener.aoMudarBrilho(progress - 100);
            } else if (seekBar.getId() == R.id.seekbar_constraste) {
                progress += 10;
                float value = .10f * progress;
                Log.i("contraste", "contraste de " + progress);
                listener.aoMudarContraste(value);
            } else if (seekBar.getId() == R.id.seekbar_saturacao) {
                float value = .10f * progress;
                listener.aoMudarSaturacao(value);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (listener != null) {
            listener.aoIniciarEdicao();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (listener != null) {
            listener.aoFinalizarEdicao();
        }
    }

    public void resetarAlteracoes(){
        instance = null;

        brilho.setMax(200);
        brilho.setProgress(100);

        contraste.setMax(20);
        contraste.setProgress(0);

        saturacao.setMax(30);
        saturacao.setProgress(10);
    }

    public void setListener(EditarImagemFragmentListener listener) {
        this.listener = listener;
    }
}
