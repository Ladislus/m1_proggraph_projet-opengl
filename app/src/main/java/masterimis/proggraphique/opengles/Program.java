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

    int[] _linkStatus = {0};

    public Program() {

        /* Chargement des shaders */
        int vertexShader = MyGLRenderer.loadShader(
                GLES30.GL_VERTEX_SHADER,
                Shaders._vertex);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES30.GL_FRAGMENT_SHADER,
                Shaders._fragment);

        // identifiant du programme pour lier les shaders
        int _programId = GLES30.glCreateProgram();             // create empty OpenGL Program
        GLES30.glAttachShader(_programId, vertexShader);   // add the vertex shader to program
        GLES30.glAttachShader(_programId, fragmentShader); // add the fragment shader to program
        GLES30.glLinkProgram(_programId);                  // create OpenGL program executables
        GLES30.glGetProgramiv(_programId, GLES30.GL_LINK_STATUS, _linkStatus,0);

        // Add program to OpenGL environment
        GLES30.glUseProgram(_programId);

        // get handles to transformation matrix
        this._mvpId = GLES30.glGetUniformLocation(_programId, "uMVPMatrix");
        this._positionId = GLES30.glGetAttribLocation(_programId, "vPosition");
        this._colorId = GLES30.glGetAttribLocation(_programId, "vCouleur");
    }

    public void activate() {
        GLES30.glEnableVertexAttribArray(this._positionId);
        GLES30.glEnableVertexAttribArray(this._colorId);
    }

    public void disable() {
        // Disable vertex array
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
