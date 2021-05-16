package masterimis.proggraphique.opengles.Utils;

public class Couple<T> {

    private T _x;
    private T _y;

    public Couple(T x, T y) {
        this._x = x;
        this._y = y;
    }

    public T getX() { return this._x; }
    public void setX(T x) { this._x = x; }

    public T getY() { return this._y; }
    public void setY(T y) { this._y = y; }
}
