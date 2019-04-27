package sceneGraph.numerical;

public class Precision {

	public static final double SAFE_MIN_DOUBLE;
    private static final long EXPONENT_OFFSET_DOUBLE = 1023l;
    public static final double EPSILON_DOUBLE;
	
    public static final double SAFE_MIN_FLOAT;
    private static final long EXPONENT_OFFSET_FLOAT = 127;
    public static final double EPSILON_FLOAT;
    
	static {
        EPSILON_DOUBLE = Double.longBitsToDouble((EXPONENT_OFFSET_DOUBLE - 53l) << 52);
        SAFE_MIN_DOUBLE = Double.longBitsToDouble((EXPONENT_OFFSET_DOUBLE - 1022l) << 52);
        
        
        EPSILON_FLOAT = Double.longBitsToDouble((EXPONENT_OFFSET_FLOAT - 24l) << 23);
        SAFE_MIN_FLOAT = Double.longBitsToDouble((EXPONENT_OFFSET_FLOAT - 126) << 23);
    }
	
}