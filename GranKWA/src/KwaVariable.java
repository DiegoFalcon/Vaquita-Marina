public class KwaVariable {
	private int _varInt;
    private double _varDouble;
    private float _varFloat;
    private String _varString;
    private char _varChar;
    private int [] _vectorInt;
    private double [] _vectorDouble;
    private float[] _vectorFloat;
    private String[] _vectorString;
    private char[] _vectorChar;
    private boolean _varBoolean;
    private int _type;
        
    KwaVariable(int varInt){
        _varInt = varInt;
        _varFloat = Float.parseFloat(String.valueOf(varInt));
        _varDouble = Double.parseDouble(String.valueOf(varInt));
        _type = 0;
    }
    
    KwaVariable(double varDouble){
        _varDouble = varDouble;
        _varInt = (int)varDouble;
        _varFloat = Float.parseFloat(String.valueOf(varDouble));
        _type = 2;
    }
    
    KwaVariable(float varFloat){
        _varFloat = varFloat;
        _varDouble = Double.parseDouble(String.valueOf(varFloat));
        _varInt = (int)varFloat;//Integer.parseInt(String.valueOf(varFloat));
        _type = 1;
    }
    
    KwaVariable(String varString){
        _varString = varString;
        _type = 4;
    }
    
    KwaVariable(char varChar){
        _varChar = varChar;
        _type = 3;
    }
    
    KwaVariable(int[] varInt){
        _vectorInt = varInt;
        _type=6;
    }
    
    KwaVariable(double[] varDouble){
        _vectorDouble = varDouble;
        _type=8;
    }
    
    KwaVariable(float[] varFloat){
        _vectorFloat = varFloat;
        _type=7;
    }
    
    KwaVariable(String[] varString){
        _vectorString = varString;
        _type=10;
    }
     KwaVariable(char[] varChar){
        _vectorChar = varChar;
        _type=9;
    }
      KwaVariable(boolean varBoolean){
        _varBoolean = varBoolean;
        _type=5;
    }
    
    public int GetVarInt() {
        return _varInt;
    }
    
    public double GetVarDouble() {
        return _varDouble;
    }
    
    public float GetVarFloat() {
        return _varFloat;
    }
    
    public char GetVarChar() {
        return _varChar;
    }
    
    public String GetVarString() {
        return _varString;
    }
    
    public int[] GetVectorInt() {
        return _vectorInt;
    }
    
    public double[] GetVectorDouble() {
        return _vectorDouble;
    }
    
    public float[] GetVectorFloat() {
        return _vectorFloat;
    }
    
    public char[] GetVectorChar() {
        return _vectorChar;
    }
    
    public String[] GetVectorString() {
        return _vectorString;
    }
    
    public boolean GetVarBoolean(){
        return _varBoolean;
    }
    
    public int getType(){
        return _type;
    }
}
