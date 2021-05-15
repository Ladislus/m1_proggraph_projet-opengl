package masterimis.proggraphique.opengles.Shapes;

public interface Shape {
    float[] getPosition();
    void setPosition(float[] position);

    void draw(float[] scratch);

}
