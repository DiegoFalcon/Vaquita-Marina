import java.util.Stack;

public class DatosRecursividad {
	public int lastByteRead;
	public boolean lastTokenReadOperator;
	public boolean lastTokenReadSubstractOperator;
	public Stack<Token> stackValoresExpresion = new Stack<Token>();
	public Stack<Integer> stackTokensInIndex = new Stack<Integer>();
      public DatosRecursividad(int lastByteRead, boolean lastTokenReadSubstractOperator, boolean lastTokenReadOperator,Stack<Token> stackValoresExpresion, Stack<Integer> stackTokensInIndex){
    	  this.lastByteRead = lastByteRead;
    	  this.lastTokenReadOperator = lastTokenReadOperator;
    	  this.lastTokenReadSubstractOperator = lastTokenReadSubstractOperator;
    	  while(!stackValoresExpresion.isEmpty())
    		  this.stackValoresExpresion.push(stackValoresExpresion.pop());
    	  while(!stackTokensInIndex.isEmpty())
    		  this.stackTokensInIndex.push(stackTokensInIndex.pop());
      }
}
