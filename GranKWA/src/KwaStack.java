import java.util.LinkedList;

public class KwaStack {
LinkedList<KwaVariable> stack;
    
    public KwaStack(){
        stack=new LinkedList<>();
    }
    
    public int getType(){
        /*
         * int=0
         * float=1
         * double=2
         * char=3
         * string=4
         * boolean=5
         * vi=6
         * vf=7
         * vd=8
         * vc=9
         * vs=10
         */
        int var = stack.getLast().getType();
        return var;        
    }
    
    public void PUSHI(int var){
        stack.addLast(new KwaVariable(var));
    }
    
    public void PUSHD(double var){
        stack.addLast(new KwaVariable(var));
    }
    
    public void PUSHF(float var){
        stack.addLast(new KwaVariable(var));
    }
    
    public void PUSHC(char var){
        stack.addLast(new KwaVariable(var));
    }
    
    public void PUSHS(String var){
        stack.addLast(new KwaVariable(var));
    }
    
    public int POPI(){
        int var;
        var = stack.getLast().GetVarInt();
        stack.removeLast();
        return var;
    }
    
    public double POPD(){
        double var;
        var = stack.getLast().GetVarDouble();
        stack.removeLast();
        return var;
    }
    
    public float POPF(){
        float var;
        var = stack.getLast().GetVarFloat();
        stack.removeLast();
        return var;
    }
    
    public char POPC(){
        char var;
        var = stack.getLast().GetVarChar();
        stack.removeLast();
        return var;
    }
    
    public String POPS(){
        String var;
        var = stack.getLast().GetVarString();
        stack.removeLast();
        return var;
    }
    
    public boolean POPB(){
        boolean var;
        var = stack.getLast().GetVarBoolean();
        stack.removeLast();
        return var;
    }
    
    public int[] POPVI(){
        int []var;
        var = stack.getLast().GetVectorInt();
        stack.removeLast();
        return var;
    }
    
    public double[] POPVD(){
        double[] var;
        var = stack.getLast().GetVectorDouble();
        stack.removeLast();
        return var;
    }
    
    public float[] POPVF(){
        float[] var;
        var = stack.getLast().GetVectorFloat();
        stack.removeLast();
        return var;
    }
    
    public char[] POPVC(){
        char[] var;
        var = stack.getLast().GetVectorChar();
        stack.removeLast();
        return var;
    }
    
    public String [] POPVS(){
        String[] var;
        var = stack.getLast().GetVectorString();
        stack.removeLast();
        return var;
    }
}
