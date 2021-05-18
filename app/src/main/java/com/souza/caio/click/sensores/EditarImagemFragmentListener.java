package com.souza.caio.click.sensores;

public interface EditarImagemFragmentListener
{
    void aoMudarBrilho(int brilho);
    void aoMudarSaturacao(float saturacao);
    void aoMudarContraste(float contraste);
    void aoIniciarEdicao();
    void aoFinalizarEdicao();

}
