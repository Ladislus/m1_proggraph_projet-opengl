package masterimis.proggraphique.opengles.Shapes;

import masterimis.proggraphique.opengles.Utils.Couple;

public class Triangle extends DefaultShape {

    private static final float[] TRIANGLE_INITIALS = {
            0.0f, 1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f
    };
    private static final short[] TRIANGLE_INDICES = { 0, 1, 2};

    public Triangle(Couple<Float> position, Color color) {
        super(position, color, Family.TRIANGLE, TRIANGLE_INITIALS, TRIANGLE_INDICES);
    }

    @Override
    protected void initColors(Color color) {
        //Ce code est fait par des professionnels, ne pas le reproduire
        this._colors = new float[] {
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
                color.getColorValues()[0], color.getColorValues()[1], color.getColorValues()[2], color.getColorValues()[3],
        };
    }
}
