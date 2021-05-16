package masterimis.proggraphique.opengles.Shapes;

public enum Family {
    TRIANGLE("Triangle"),
    SQUARE("Carré"),
    LOSANGE("Losange");
    
    private final String _value;
    
    Family(String value) {
        this._value = value;
    }
    
    public String getValue() { return this._value; }
}
