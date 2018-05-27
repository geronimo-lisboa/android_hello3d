package dongeronimo.com.testejpct.model;

import android.content.Context;
import android.graphics.Bitmap;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.util.logging.MemoryHandler;

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
    /**Pega o world da jpct.*/
    public World getWorldObject(){
        return world;
    }
    /**
     * Quando cria o mundo cria o terrno e o céu, que por sua vez cria o sol*/
    public Mundo(MyRenderer renderer, Bitmap heightmap, float seaLevelInicial, int horaInicial){
        this.renderer = renderer;
        world = new World();
        //Criação do terreno
        terreno = new Terreno(heightmap, this, seaLevelInicial);
        //Criação do céu
        ceu = new Ceu(horaInicial, this);

        MemoryHelper.compact();
    }

    public void render(FrameBuffer fb){
        world.renderScene(fb);
        world.draw(fb);
    }
}
