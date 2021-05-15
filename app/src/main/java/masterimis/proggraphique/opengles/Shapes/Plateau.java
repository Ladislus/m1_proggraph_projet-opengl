package masterimis.proggraphique.opengles.Shapes;

import android.util.Log;

import java.util.ArrayList;

import masterimis.proggraphique.opengles.MyGLRenderer;

public class Plateau {
    ArrayList<ArrayList<Shape>> plateau;
    private final MyGLRenderer mRenderer;



    public Plateau(MyGLRenderer renderer) {
        this.plateau = new ArrayList<>();
        mRenderer = renderer;
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
        for (int ligne = 0; ligne < this.plateau.size(); ligne++) {
            for (int elem = 0; elem < this.plateau.get(ligne).size(); elem++) {
                if (ligne > 0){
                    //Ligne au dessus
                    if(this.plateau.get(ligne - 1).get(elem) == null){
                        this.echange(ligne, elem, ligne - 1, elem);
                        return;
                    }
                }
                if (ligne < 2){
                    //Ligne en dessous
                    if(this.plateau.get(ligne + 1).get(elem) == null){
                        //Change avec voisin du dessous
                        this.echange(ligne, elem, ligne + 1, elem);
                        return;
                    }
                }
                if (elem > 0){
                    //Colonne a gauche
                    if(this.plateau.get(ligne).get(elem - 1) == null){
                        //Change avec voisin de gauche
                        this.echange(ligne, elem, ligne, elem - 1);

                        return;
                    }
                }
                if (elem < 2){
                    //Colonne a droite
                    if(this.plateau.get(ligne).get(elem + 1) == null){
                        //Change avec voisin de gauche
                        this.echange(ligne, elem, ligne, elem + 1);
                        return;
                    }
                }
            }
        }
    }

    private void echange(int xShape, int yShape, int x, int y) {
        Shape shapeADeplacer = this.plateau.get(xShape).get(yShape);
        shapeADeplacer.setPosition(new float[]{x, y}); //Changement de la pos de la shape
        this.plateau.get(x).set(y, shapeADeplacer);
        this.plateau.get(xShape).set(yShape, null);
    }
}
