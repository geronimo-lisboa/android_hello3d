package dongeronimo.com.testejpct.model;

import com.threed.jpct.Light;
import com.threed.jpct.SimpleVector;

public class Sol {
    private Mundo mundo;
    private int hora;
    //Baixo nivel
    Light lightSource;

    public void avancarHora(){
        hora++;
        calculaPosicao(hora);
    }

    private void calculaPosicao(int hora){
        lightSource.setPosition(new SimpleVector(0,100, 0));
    }


    public Sol(int horaAtual, Mundo m){
        this.mundo = m;
        this.hora = horaAtual;
        lightSource = new Light(m.getWorldObject());
        calculaPosicao(hora);
    }
}
