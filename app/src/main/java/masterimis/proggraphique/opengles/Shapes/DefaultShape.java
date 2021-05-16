package masterimis.proggraphique.opengles.Shapes;

import android.opengl.GLES30;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import masterimis.proggraphique.opengles.Program;
import masterimis.proggraphique.opengles.Utils.Couple;

public abstract class DefaultShape implements Shape {

    public static final int OFFSET = 7;
    protected static final int COORDINATES_SIZE = 3; // nombre de coordonnées par vertex
    protected static final int COLORS_SIZE = 4; // nombre de composantes couleur par vertex
    protected final int VERTEX_STRIDE = COORDINATES_SIZE * 4;
    protected final int COLOR_STRIDE = COLORS_SIZE * 4;

    protected static final Program _program = new Program();

    // Buffer pout les points
    protected FloatBuffer _vertexBuffer;
    // Buffer des indices
    protected ShortBuffer _indicesBuffer;
    // Buffer des couleurs (4 par sommets)
    protected FloatBuffer _colorBuffer;

    protected final Couple<Float> _position = new Couple<>( 0.0f, 0.0f );

    protected final Color _colorType;
    protected final Family _shapeType;

    protected final short[] _indices;
    protected final float[] _initials;
    protected float[] _coordinates;
    protected float[] _colors;

    public DefaultShape(Couple<Float> position, Color color, Family family, float[] initials, short[] indices) {

        this._initials = initials;
        this._coordinates = new float[initials.length];
        System.arraycopy(initials, 0, this._coordinates, 0, initials.length);
        this._indices = indices;
        this.setPosition(position);

        this._colorType = color;
        this._shapeType = family;

        // initialisation du buffer pour les vertex (4 bytes par float)
        ByteBuffer bb = ByteBuffer.allocateDirect(this._coordinates.length * 4);
        bb.order(ByteOrder.nativeOrder());
        this._vertexBuffer = bb.asFloatBuffer();
        this._vertexBuffer.put(this._coordinates);
        this._vertexBuffer.position(0);

        // initialisation du buffer pour les couleurs (4 bytes par float)
        this.initColors(color);
        ByteBuffer bc = ByteBuffer.allocateDirect(this._colors.length * 4);
        bc.order(ByteOrder.nativeOrder());
        this._colorBuffer = bc.asFloatBuffer();
        this._colorBuffer.put(this._colors);
        this._colorBuffer.position(0);

        // initialisation du buffer des indices
        ByteBuffer dlb = ByteBuffer.allocateDirect(this._indices.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        this._indicesBuffer = dlb.asShortBuffer();
        this._indicesBuffer.put(this._indices);
        this._indicesBuffer.position(0);
    }

    public void draw(float[] mvpMatrix) {

        ///////////////////////////////////
        // Actualisation des coordinates //
        ///////////////////////////////////
        this._vertexBuffer.clear();
        this._vertexBuffer.put(this._coordinates);
        this._vertexBuffer.position(0);

        GLES30.glUniformMatrix4fv(_program.getMvpId(), 1, false, mvpMatrix, 0);
        _program.activate();

        GLES30.glVertexAttribPointer(
                _program.getPositionId(), COORDINATES_SIZE,
                GLES30.GL_FLOAT, false,
                VERTEX_STRIDE, this._vertexBuffer);

        GLES30.glVertexAttribPointer(
                _program.getColorId(), COLORS_SIZE,
                GLES30.GL_FLOAT, false,
                COLOR_STRIDE, this._colorBuffer);

        GLES30.glDrawElements(
                GLES30.GL_TRIANGLES, this._indices.length,
                GLES30.GL_UNSIGNED_SHORT, this._indicesBuffer);

        _program.disable();
    }

    @Override
    public Couple<Float> getPosition() {
        return this._position;
    }

    @Override
    public void setPosition(Couple<Float> position) {
        this._position.setX(position.getX());
        this._position.setY(position.getY());
        for(int i = 0; i < this._coordinates.length; i += 3) {
            this._coordinates[i] = _initials[i] + (OFFSET * position.getX());
            this._coordinates[i + 1] = _initials[i + 1] + (OFFSET * position.getY());
        }
    }

    @Override
    public Family getFamily() {
        return this._shapeType;
    }

    @Override
    public Color getColor() {
        return this._colorType;
    }

    abstract protected void initColors(Color color);
}
