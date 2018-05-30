package dongeronimo.com.testejpct.model;

import android.content.Context;
import android.graphics.Bitmap;


import com.threed.jpct.FrameBuffer;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.MemoryHandler;

import dongeronimo.com.testejpct.MyRenderer;


public class Mundo implements IRenderable, IUpdatable{
    private Ceu ceu;
    private Mar mar;
    private Terreno terreno;
    private float seaLevel;
    private long elapsedTime;

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
        terreno.setSun(ceu.getSol());
        world.renderScene(fb);
        world.draw(fb);
    }
    /**
     * Informa a posição da câmera no espaço para cara renderable. Alguns podem precisar dessa informação*/
    public void setCameraPosition(SimpleVector cameraPosition) {
        terreno.setCameraPosition(cameraPosition);
    }


    @Override
    public void avancarTempo() {
        elapsedTime++;//avança o contador da simulação.
        ceu.avancarTempo();
    }
}
