package masterimis.proggraphique.opengles;

import android.opengl.GLES30;

import masterimis.proggraphique.opengles.Shapes.Shaders;

public class Program {

    // idendifiant (location) pour transmettre les coordonnées au vertex shader
    private final int _positionId;
    // identifiant (location) pour transmettre les couleurs
    private final int _colorId;
    // identifiant (location) pour transmettre la matrice PxVxM
    private final int _mvpId;

    public Program() {

        /* Chargement des shaders */
        int vertexShader = GLRenderer.loadShader(
                GLES30.GL_VERTEX_SHADER,
                Shaders._vertex
        );
        int fragmentShader = GLRenderer.loadShader(
                GLES30.GL_FRAGMENT_SHADER,
                Shaders._fragment
        );

        int programId = GLES30.glCreateProgram();
        GLES30.glAttachShader(programId, vertexShader);
        GLES30.glAttachShader(programId, fragmentShader);
        GLES30.glLinkProgram(programId);
        int[] _linkStatus = { 0 };
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, _linkStatus,0);

        // Add program to OpenGL environment
        GLES30.glUseProgram(programId);

        // get handles to transformation matrix
        this._mvpId = GLES30.glGetUniformLocation(programId, "uMVPMatrix");
        this._positionId = GLES30.glGetAttribLocation(programId, "vPosition");
        this._colorId = GLES30.glGetAttribLocation(programId, "vCouleur");
    }

    public void activate() {
        GLES30.glEnableVertexAttribArray(this._positionId);
        GLES30.glEnableVertexAttribArray(this._colorId);
    }

    public void disable() {
        GLES30.glDisableVertexAttribArray(this._positionId);
        GLES30.glDisableVertexAttribArray(this._colorId);
    }

    public int getMvpId() {
        return this._mvpId;
    }

    public int getPositionId() {
        return this._positionId;
    }

    public int getColorId() {
        return this._colorId;
    }
}
