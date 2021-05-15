package masterimis.proggraphique.opengles.Shapes;
import android.opengl.GLES30;

public class Losange extends DefaultShape {

    private static final float[] LOSANGE_INITIALS = {
            -1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f,
            0.0f, -1.0f, 1.0f,
    };
    private static final short[] LOSANGE_INDICES = { 0, 1, 2, 0, 2, 3};


    public Losange(float[] position, Color color) {
        super(position, color, LOSANGE_INITIALS, LOSANGE_INDICES);
    }

    @Override
    protected void initColors(Color color) {
        //Ce code est fait par des professionnels, ne pas le reproduire
        this._colors = new float[] {
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
        };
    }
}
