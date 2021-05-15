package masterimis.proggraphique.opengles.Shapes;

public enum Color {
    RED( 1.0f, 0.0f, 0.0f, 1.0f ),
    GREEN( 0.0f, 1.0f, 0.0f, 1.0f ),
    BLUE( 0.0f, 0.0f, 1.0f, 1.0f );

    private final Float[] _values;

    Color(Float... values) {
        this._values = values;
    }

    public Float[] getColorValues() { return this._values; }
}
