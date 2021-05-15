package masterimis.proggraphique.opengles.Shapes;

public interface Shape {
    void draw(float[] scratch);
    float[] getPosition();
    void set_position(float[] pos);
}
