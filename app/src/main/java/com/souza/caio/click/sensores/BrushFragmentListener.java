package com.souza.caio.click.sensores;

public interface BrushFragmentListener
{
    void aoMudarEspessuraPincel(float tamanho);
    void aoMudarOpacidadePincel(int opacidade);
    void aoMudarCor(int cor);
    void aoMudarEstadoPincel(boolean estaApagando);
}
