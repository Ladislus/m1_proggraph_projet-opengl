package masterimis.proggraphique.opengles;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.util.ArrayList;

import masterimis.proggraphique.opengles.Shapes.Color;
import masterimis.proggraphique.opengles.Shapes.Losange;
import masterimis.proggraphique.opengles.Shapes.Shape;
import masterimis.proggraphique.opengles.Shapes.Square;
import masterimis.proggraphique.opengles.Shapes.Triangle;
import masterimis.proggraphique.opengles.Utils.Couple;

public class GLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private final ArrayList<Shape> shapes = new ArrayList<>();


    // Les matrices MVP
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    private final Couple<Float> _position = new Couple<>(3.0f, 0.0f);
    private final ArrayList<Color> _colors = new ArrayList<>();

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this._colors.add(Color.RED);
        this._colors.add(Color.GREEN);
        this._colors.add(Color.BLUE);

        // Couleur de fond
        GLES30.glClearColor( 1.0f, 1.0f, 1.0f, 1.0f );

        // Ajout des éléments
        shapes.add(new Square(new Couple<>(-1.0f, 1.0f), _colors.get(0)));
        shapes.add(new Square(new Couple<>(-1.0f + 1, 1.0f), _colors.get(1)));
        shapes.add(new Square(new Couple<>(-1.0f + 2, 1.0f), _colors.get(2)));

        shapes.add(new Triangle(new Couple<>(-1.0f, 0.0f), _colors.get(0)));
        shapes.add(new Triangle(new Couple<>(-1.0f + 1, 0.0f), _colors.get(1)));
        shapes.add(new Triangle(new Couple<>(-1.0f + 2, 0.0f), _colors.get(2)));

        shapes.add(new Losange(new Couple<>(-1.0f, -1.0f), _colors.get(0)));
        shapes.add(new Losange(new Couple<>(-1.0f + 1, -1.0f), _colors.get(1)));
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        Matrix.setIdentityM(mViewMatrix,0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.setIdentityM(mModelMatrix,0);
        for (int i = 0; i < shapes.size(); i++){
            Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, 0);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mModelMatrix, 0);
            shapes.get(i).draw(scratch);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        int widthProj = width / 100;
        int heightProj = height / 100;
        GLES30.glViewport(0, 0, width, height);
        Matrix.orthoM(mProjectionMatrix, 0, -widthProj, widthProj, -heightProj, heightProj, -1.0f, 1.0f);
    }

    public static int loadShader(int type, String shaderCode){
        int shaderId = GLES30.glCreateShader(type);

        GLES30.glShaderSource(shaderId, shaderCode);
        GLES30.glCompileShader(shaderId);

        return shaderId;
    }

    public Couple<Float> getPosition() { return this._position; }
}
