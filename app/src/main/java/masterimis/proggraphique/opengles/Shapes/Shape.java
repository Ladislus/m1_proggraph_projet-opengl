package masterimis.proggraphique.opengles.Shapes;

import masterimis.proggraphique.opengles.Utils.Couple;

public interface Shape {
    void draw(float[] scratch);

    Couple<Float> getPosition();
    void setPosition(Couple<Float> position);
}
