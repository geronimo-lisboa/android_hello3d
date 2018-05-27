package dongeronimo.com.testejpct.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.threed.jpct.World;

import dongeronimo.com.testejpct.MyRenderer;


public class Mundo {
    private Ceu ceu;
    private Mar mar;
    private Terreno terreno;
    private float seaLevel;
    //Low level
    private World world;
    private MyRenderer renderer;
    /**Pega o contexto de android.*/
    public Context getContext(){
        return renderer.getContext();
    }

    public Mundo(MyRenderer renderer, Bitmap heightmap, float seaLevelInicial, int horaInicial){
        this.renderer = renderer;
        world = new World();
        //Criação do terreno
        terreno = new Terreno(heightmap, this, seaLevelInicial);

    }
}
