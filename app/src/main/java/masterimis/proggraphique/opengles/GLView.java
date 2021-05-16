package masterimis.proggraphique.opengles;

import android.content.Context;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.Map;

import masterimis.proggraphique.opengles.Shapes.DefaultShape;

public class GLView extends GLSurfaceView {

    private static final int RANDOMIZE_ROUNDS = 20;

    private final Plateau _plateau;
    private boolean _isRandomized = false;

    public GLView(Context context, Map<Integer, MediaPlayer> sounds) {
        super(context);
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setEGLContextClientVersion(3);

        // Création du renderer qui va être lié au conteneur View créé
        GLRenderer _renderer = new GLRenderer();
        setRenderer(_renderer);

        // Option pour indiquer qu'on redessine uniquement si les données changent
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        // Création du plateau de jeu
        this._plateau = new Plateau(_renderer, this, sounds);
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {

        if (e.getAction() == MotionEvent.ACTION_UP) {
            // Premier clique, mélange le plateau
            if (!this._isRandomized) {
                this._plateau.randomize(RANDOMIZE_ROUNDS);

                if (this._plateau.check()) Toast.makeText(this.getContext(), "La fonction de mélange a donné le tableau fini !", Toast.LENGTH_SHORT).show();
                else this._isRandomized = true;

                requestRender();
            } else {

                Log.d("OpenGL", "Coup !");
                // Conversion des coordonnées pixel en coordonnées OpenGL
                // Attention l'axe x est inversé par rapport à OpenGLSL
                // On suppose que l'écran correspond à un carré d'arête 2 centré en 0
                float glX = (20.0f * e.getX() / getWidth() - 10.0f) / DefaultShape.OFFSET;
                float glY = (-20.0f * e.getY() / getHeight() + 10.0f) / DefaultShape.OFFSET;

                //Définition des plages de cellules
                boolean XGauche = (glX <= -0.75f && glX >= -1.25f);
                boolean XMilieu = (glX <= 0.25f && glX >= -0.25f);
                boolean XDroite = (glX <= 1.25f && glX >= 0.75f);

                boolean YHaut = (glY <= 0.75f && glY >= 0.25f);
                boolean YMilieu = (glY <= 0.20f && glY >= -0.20f);
                boolean YBas = (glY <= -0.25f && glY >= -0.75f);

                //Calcul des valeurs booleans
                boolean inHautGauche = XGauche && YHaut;
                boolean inHautMilieu = XMilieu && YHaut;
                boolean inHautDroite = XDroite && YHaut;

                boolean inMilieuGauche = XGauche && YMilieu;
                boolean inMilieuMilieu = XMilieu && YMilieu;
                boolean inMilieuDroite = XDroite && YMilieu;

                boolean inBasGauche = XGauche && YBas;
                boolean inBasMilieu = XMilieu && YBas;
                boolean inBasDroite = XDroite && YBas;

                Log.d("testAction", "Test click  : \n"
                        +"hautGauche : "+inHautGauche+"\n"
                        +"hautMilieu : "+inHautMilieu+"\n"
                        +"hautDroite : "+inHautDroite+"\n"
                        +"inMilieuGauche : "+inMilieuGauche+"\n"
                        +"inMilieuMilieu : "+inMilieuMilieu+"\n"
                        +"inMilieuDroite : "+inMilieuDroite+"\n"
                        +"inBasGauche : "+inBasGauche+"\n"
                        +"inBasMilieu : "+inBasMilieu+"\n"
                        +"inBasDroite : "+inBasDroite+"\n"
                );

                int posX = (XGauche) ? -1 : (XDroite) ? 1 : 0;
                int posY = (YHaut) ? 1 : (YBas) ? - 1 : 0;
                boolean played = this._plateau.play(posX, posY);
                if (!played) Toast.makeText(this.getContext(), "Impossible de jouer ce coup !", Toast.LENGTH_SHORT).show();
                else if (this._plateau.check()) {
                    Toast.makeText(this.getContext(), "Féliciation ! Victoire ! Cliquez pour rejouer", Toast.LENGTH_SHORT).show();
                    this._isRandomized = false;
                }
            }
        }
        return true;
    }
}
