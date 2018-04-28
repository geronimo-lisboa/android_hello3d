package dongeronimo.com.testejpct;

import android.graphics.drawable.Drawable;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class Terrain {

    public static Object3D Generate(Drawable texDrawable){
        Texture tex = new Texture(texDrawable);
        TextureManager.getInstance().addTexture("base", tex);

        Object3D box=new Object3D(12);

        SimpleVector upperLeftFront=new SimpleVector(-1,-1,-1);
        SimpleVector upperRightFront=new SimpleVector(1,-1,-1);
        SimpleVector lowerLeftFront=new SimpleVector(-1,1,-1);
        SimpleVector lowerRightFront=new SimpleVector(1,1,-1);

        SimpleVector upperLeftBack = new SimpleVector( -1, -1, 1);
        SimpleVector upperRightBack = new SimpleVector(1, -1, 1);
        SimpleVector lowerLeftBack = new SimpleVector( -1, 1, 1);
        SimpleVector lowerRightBack = new SimpleVector(1, 1, 1);

        // Front
        box.addTriangle(upperLeftFront,0,0, lowerLeftFront,0,1, upperRightFront,1,0);
        box.addTriangle(upperRightFront,1,0, lowerLeftFront,0,1, lowerRightFront,1,1);

        // Back
        box.addTriangle(upperLeftBack,0,0, upperRightBack,1,0, lowerLeftBack,0,1);
        box.addTriangle(upperRightBack,1,0, lowerRightBack,1,1, lowerLeftBack,0,1);

        // Upper
        box.addTriangle(upperLeftBack,0,0, upperLeftFront,0,1, upperRightBack,1,0);
        box.addTriangle(upperRightBack,1,0, upperLeftFront,0,1, upperRightFront,1,1);

        // Lower
        box.addTriangle(lowerLeftBack,0,0, lowerRightBack,1,0, lowerLeftFront,0,1);
        box.addTriangle(lowerRightBack,1,0, lowerRightFront,1,1, lowerLeftFront,0,1);

        // Left
        box.addTriangle(upperLeftFront,0,0, upperLeftBack,1,0, lowerLeftFront,0,1);
        box.addTriangle(upperLeftBack,1,0, lowerLeftBack,1,1, lowerLeftFront,0,1);

        // Right
        box.addTriangle(upperRightFront,0,0, lowerRightFront,0,1, upperRightBack,1,0);
        box.addTriangle(upperRightBack,1,0, lowerRightFront, 0,1, lowerRightBack,1,1);

        box.setTexture("base");
        box.build();
        return box;
    }
}