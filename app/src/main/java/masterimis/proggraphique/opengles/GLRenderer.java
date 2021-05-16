/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package masterimis.proggraphique.opengles;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;

import masterimis.proggraphique.opengles.Shapes.Color;
import masterimis.proggraphique.opengles.Shapes.Losange;
import masterimis.proggraphique.opengles.Shapes.Shape;
import masterimis.proggraphique.opengles.Shapes.Square;
import masterimis.proggraphique.opengles.Shapes.Triangle;
import masterimis.proggraphique.opengles.Utils.Couple;

/* MyGLRenderer implémente l'interface générique GLSurfaceView.Renderer */

public class GLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private ArrayList<Shape> shapes = new ArrayList<>();


    // Les matrices habituelles Model/View/Projection

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

//    private ArrayList<float[]> mSquarePosition = new ArrayList<>();
    private float[] mSquarePosition = new float[]{3.0f, 0.0f};

    private ArrayList<Color> mSquareColors = new ArrayList<>();

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    /* Première méthode équivalente à la fonction init en OpenGLSL */
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {


        this.mSquareColors.add(Color.RED);
        this.mSquareColors.add(Color.GREEN);
        this.mSquareColors.add(Color.BLUE);

        // la couleur du fond d'écran
        GLES30.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        /* on va définir une classe Square pour dessiner des carrés */
        shapes.add(new Square(new Couple<Float>(-1.0f, 1.0f ), mSquareColors.get(0)));
        shapes.add(new Square(new Couple<Float>(-1.0f + 1, 1.0f ), mSquareColors.get(1)));
        shapes.add(new Square(new Couple<Float>(-1.0f + 2, 1.0f ), mSquareColors.get(2)));

        shapes.add(new Triangle(new Couple<Float>(-1.0f, 0.0f ), mSquareColors.get(0)));
        shapes.add(new Triangle(new Couple<Float>(-1.0f + 1, 0.0f ), mSquareColors.get(1)));
        shapes.add(new Triangle(new Couple<Float>(-1.0f + 2, 0.0f ), mSquareColors.get(2)));

        shapes.add(new Losange(new Couple<Float>(-1.0f, -1.0f ), mSquareColors.get(0)));
        shapes.add(new Losange(new Couple<Float>(-1.0f + 1, -1.0f ), mSquareColors.get(1)));


    }

    /* Deuxième méthode équivalente à la fonction Display */
    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16]; // pour stocker une matrice
        /* on utilise une classe Matrix (similaire à glm) pour définir nos matrices P, V et M*/
            /* Pour le moment on va utiliser une projection orthographique
               donc View = Identity
             */
        // glClear rien de nouveau on vide le buffer de couleur et de profondeur */
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        /*pour positionner la caméra mais ici on n'en a pas besoin*/
        // Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.setIdentityM(mViewMatrix,0);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.setIdentityM(mModelMatrix,0);
            /* Pour définir une translation on donne les paramètres de la translation
            et la matrice (ici mModelMatrix) est multipliée par la translation correspondante
             */

        /* scratch est la matrice PxVxM finale */

        for (int i = 0; i < shapes.size(); i++){
            /* on appelle la méthode dessin du carré élémentaire */
//            float x = (i == 0) ? -3.0f : this.mSquarePosition[0];
//            x = (i == 3 || i == 6) ? -6.0f : x;
//            float y = (i == 3 || i == 6) ? -3.0f : 0;
            Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, 0);

            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mModelMatrix, 0);
            shapes.get(i).draw(scratch);

        }




    }

    /* équivalent au Reshape en OpenGLSL */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        /* ici on aurait pu se passer de cette méthode et déclarer
        la projection qu'à la création de la surface !!
         */
//        GLES30.glViewport(0, 0, width, height);
//        Matrix.orthoM(mProjectionMatrix, 0, -10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 1.0f);

        int width_proj = width/100;
        int heigth_proj = height/100;
        GLES30.glViewport(0, 0, width, height);
        Matrix.orthoM(mProjectionMatrix, 0, -width_proj, width_proj, -heigth_proj, heigth_proj, -1.0f, 1.0f);
    }

    /* La gestion des shaders ... */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES30.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES30.GL_FRAGMENT_SHADER)
        int shader = GLES30.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);

        return shader;
    }


    /* Les méthodes nécessaires à la manipulation de la position finale du carré */
    public void setPosition(float x, float y) {
        /*mSquarePosition[0] += x;
        mSquarePosition[1] += y;*/
        mSquarePosition[0] = x;
        mSquarePosition[1] = y;

    }

    public float[] getPosition() {
        return mSquarePosition;
    }

}
