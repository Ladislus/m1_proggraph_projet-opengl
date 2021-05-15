package masterimis.proggraphique.opengles.Shapes;

import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import masterimis.proggraphique.opengles.MyGLRenderer;
import masterimis.proggraphique.opengles.MyGLSurfaceView;

public class Plateau {
    ArrayList<ArrayList<Shape>> plateau;
    private final MyGLRenderer mRenderer;
    private MyGLSurfaceView view;



    public Plateau(MyGLRenderer renderer, MyGLSurfaceView myGLSurfaceView) {
        this.plateau = new ArrayList<>();
        mRenderer = renderer;
        this.view = myGLSurfaceView;
    }

    public void updateContent(ArrayList<Shape> shape) {
        this.plateau.clear();
        for (int i = 0; i < 3; i++) {
            ArrayList<Shape> ligne = new ArrayList<>();
            ligne.add(shape.get(i*3));
            ligne.add(shape.get(i*3 + 1));
            if(i*3 + 2 >= 8){
                ligne.add(null);
            }else{
                ligne.add(shape.get(i*3+2));
            }

            this.plateau.add(ligne);
        }
    }

    /**
     * Renvoie la position de la case vide dans l'état actuelle du plateau
     * @return float[| { posX du vide, posY du vide}
     */
    public float[] getPosEmpty(){
        for(int ligne = 0; ligne < this.plateau.size(); ligne ++){
            for (int elem = 0; elem < this.plateau.get(ligne).size(); elem ++){
                if(this.plateau.get(ligne).get(elem) == null){
                    float posY = 1 - ligne;
                    float posX = -1 + elem;
                    return new float[]{posX, posY};
                }
            }
        }
        return null;
    }

    /**
     * Randomize le plateau
     */
    public void randomized() {
        this.updateContent(this.mRenderer.getShape());
        ArrayList<Shape> lesVoisins = new ArrayList<>();
        int ligneNull;
        int colonneNull;
        for (int nbR = 0; nbR < 100; nbR ++){
            for (int ligne = 0; ligne < this.plateau.size(); ligne++) {
                for (int colonne = 0; colonne < this.plateau.get(ligne).size() ; colonne++) {
                    if( this.plateau.get(ligne).get(colonne) == null){
                        lesVoisins.clear();
                        ligneNull = ligne;
                        colonneNull = colonne;
                        if (ligne > 0 ){
                            //Ligne au dessus
                            lesVoisins.add(this.plateau.get(ligne - 1).get(colonne));
                        }
                        if (ligne < 2 ){
                            //Ligne au dessus
                            lesVoisins.add(this.plateau.get(ligne + 1).get(colonne));
                        }
                        if (colonne < 2 ){
                            //Ligne au dessus
                            lesVoisins.add(this.plateau.get(ligne).get(colonne + 1 ));
                        }
                        if (colonne > 0 ){
                            //Ligne au dessus
                            lesVoisins.add(this.plateau.get(ligne).get(colonne - 1 ));
                        }
                        int randomNum = ThreadLocalRandom.current().nextInt(0, lesVoisins.size());
                        int lig = (int) (1 - lesVoisins.get(randomNum).getPosition()[1]);
                        int col = (int) (1 + lesVoisins.get(randomNum).getPosition()[0]);
                        this.echange(lig, col, ligneNull, colonneNull);
                        this.view.requestRender();
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

    }

    private void echange(int xShape, int yShape, int x, int y) {
        Shape shapeADeplacer = this.plateau.get(xShape).get(yShape);
        float posY = 1 - x;
        float posX = -1 + y;
        shapeADeplacer.setPosition(new float[]{posX, posY}); //Changement de la pos de la shape
        this.plateau.get(x).set(y, shapeADeplacer);
        this.plateau.get(xShape).set(yShape, null);
    }
}
