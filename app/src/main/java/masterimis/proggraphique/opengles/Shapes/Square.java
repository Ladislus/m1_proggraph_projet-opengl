package masterimis.proggraphique.opengles.Shapes;

import masterimis.proggraphique.opengles.Utils.Couple;

public class Square extends DefaultShape {

    private static final float[] SQUARE_INITIALS = {
            -1.0f,   1.0f, 0.0f,
            -1.0f,  -1.0f, 0.0f,
            1.0f,  -1.0f, 0.0f,
            1.f,  1.f, 0.0f
    };
    private static final short[] SQUARE_INDICES = { 0, 1, 2, 0, 2, 3 };

    public Square(Couple<Float> position, Color color) {
        super(position, color, Family.SQUARE, SQUARE_INITIALS, SQUARE_INDICES);
    }

    @Override
    protected void initColors(Color color) {
        // Ce code est fait par des professionnels, ne pas le reproduire
        this._colors = new float[] {
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
        };
    }
}
