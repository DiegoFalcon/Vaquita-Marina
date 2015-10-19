package compiler;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Compiler {

	static int lastByteRead = 0;
	static boolean isFileFinished = false;
	static byte[] _bytesInFile;
	static Token _currentToken;
	static Variable _variablesTable[] = new Variable[0];
	static Token _arrayToken[];
	static boolean _isCondition = false;
	static Stack<Boolean> _stackInsideInstruction = new Stack<Boolean>();
	static Stack<Integer> _stackIsCondition = new Stack<Integer>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		openFile();
		Instrucciones();
	}


	public static boolean Instrucciones() throws IOException {
		// <Instrucciï¿½n> {<Instrucciones>}
		// _currentToken = Tokenizer();
		
			
		if(!_stackInsideInstruction.isEmpty()){
			while(_stackInsideInstruction.peek())
			{
				if(CurrentToken("}")){
					_stackInsideInstruction.push(true);
					return true;				
				}
				if(!Instruccion())
					return false;
			}
			return true;
		}
		
		if(!Instruccion())
			return false;
		
		if(!isFileFinished)
			if(!Instrucciones())
				return false;
		
		return true;
		
		
		/*while (CurrentTokenInFirst("Instrucciones")) {
			if (!Instrucciones())
				return false;
		}*/
	}

	public static boolean Declaracion() throws IOException{
		//#<TipoDato> <ListaVariables> ;
		if(!TipoDato())
			return false;
		if(!ListaVariables())
			return false;
		return Expect(";");
	}


	public static boolean ListaVariables() throws IOException {
		// <Variables> {,<ListaVariables>}
		if (!Variable())
			return false;

		if (CurrentToken(","))
			if(!Expect(","))
				return false;
		while (CurrentTokenInFirst("ListaVariables")) {	
			if (!ListaVariables())
				return false;
		}

		return true;
	}

/*	public static boolean Letra() throws IOException {
		if (Expect("[a-zA-Z]+"))
			return true;
		return false;
	}

	public static boolean Digito() throws IOException {
		if (_currentToken.description.equals("0"))
			if (!Expect("0"))
				return false;
			else if (_currentToken.description.equals("1"))
				if (!Expect("1"))
					return false;
				else if (_currentToken.description.equals("2"))
					if (!Expect("2"))
						return false;
					else if (_currentToken.description.equals("3"))
						if (!Expect("3"))
							return false;
						else if (_currentToken.description.equals("4"))
							if (!Expect("4"))
								return false;
							else if (_currentToken.description.equals("5"))
								if (!Expect("5"))
									return false;
								else if (_currentToken.description.equals("6"))
									if (!Expect("6"))
										return false;
									else if (_currentToken.description.equals("7"))
										if (!Expect("7"))
											return false;
										else if (_currentToken.description.equals("8"))
											if (!Expect("8"))
												return false;
											else if (_currentToken.description.equals("9"))
												if (!Expect("9"))
													return false;
		return true;
	}

	public static boolean Comentarios() throws IOException {
		if (Expect("\\"))
			return true;
		return false;
	}
*/
	  public static boolean Variable() throws IOException{
		  	if(!CurrentToken(44) && !CurrentToken(45))
		  		return false;
		  	
		  	if(CurrentToken(44))
		  		if(!Expect(44))
		  			return false;
		  	if(CurrentToken(45))
		  		if(!Expect(45))
		  			return false;
	        if(CurrentToken("[")){
	          if(!Expect("["))
	             return false;
	          if(!IndiceVector())
	             return false;
	           if(!Expect("]"))
	             return false;
	        }
	        return true;
	 }
	  public static boolean IndiceVector() throws IOException{
	        if(CurrentTokenInFirst("Expresion"))
	            if(!Expresion())
	                return false;
	        if(CurrentTokenInFirst("IncrementoDecremento"))
	            if(!IncrementoDecremento())
	                return false;
	        return true;
	    }
		public static boolean TipoDato() throws IOException{
			if(CurrentToken("#int"))
				return Expect("#int");
			if(CurrentToken("#float"))
				return Expect("#float");
			if(CurrentToken("#double"))
				return Expect("#double");
			if(CurrentToken("#char"))
				return Expect("#char");
			if(CurrentToken("#string"))
				return Expect("#string");
			return false;	
		}

	public static boolean Expect(int tokenCode) throws IOException {
		_currentToken = Tokenizer();
		if (_currentToken.code == tokenCode) {
			//abre llave
			if(tokenCode==25)
				_stackInsideInstruction.push(true);
			//cierra llave
			if(tokenCode==26)
			{
				_stackInsideInstruction.pop();
			//	_stackInsideInstruction.push(false);
			}
			if (_stackIsCondition.isEmpty())
				System.out.println(_currentToken.description);

			return true;
		}
		if (_stackIsCondition.isEmpty()) {
			MessageError("Expect", "Token no identificado: " + tokenCode + ", se esperaba un: "+_currentToken.description);
		}
		return false;
	}

	public static boolean Expect(String instruction) throws IOException {
		_currentToken = Tokenizer();
		
		
		if (_currentToken.description.equals(instruction)) {
			if(instruction.equals("{"))
				_stackInsideInstruction.push(true);
			if(instruction.equals("}"))
			{
				_stackInsideInstruction.pop();
				//_stackInsideInstruction.push(false);
			}
			if (_stackIsCondition.isEmpty()){
				System.out.println(instruction);
				
			}
			return true;
		}
		if (_stackIsCondition.isEmpty()) {
			MessageError("Expect", "Codigo de Token no identificado: " + instruction + ", se esperaba un: "+_currentToken.code);
		}
		return false;
	}

	public static boolean CurrentToken(String instruction) throws IOException {
		_stackIsCondition.push(lastByteRead);
		if (!Expect(instruction)) {
			lastByteRead = _stackIsCondition.pop();
			return false;
		}
		lastByteRead = _stackIsCondition.pop();
		return true;
	}

	public static boolean CurrentToken(int instruction) throws IOException {
		_stackIsCondition.push(lastByteRead);
		if (!Expect(instruction)) {
			lastByteRead = _stackIsCondition.pop();
			return false;
		}
		lastByteRead = _stackIsCondition.pop();
		return true;
	}
	public static boolean CurrentTokenInfo(String info) throws IOException{
		_stackIsCondition.push(lastByteRead);
		_currentToken = Tokenizer();
		if (_currentToken.info.equals(info)) {
			lastByteRead = _stackIsCondition.pop();
			return true;
		}		
		lastByteRead = _stackIsCondition.pop();
		return false;
	}
	public static boolean CurrentTokenInFirst(String instruction) throws IOException {
		boolean result = false;
		_stackIsCondition.push(lastByteRead);

		switch (instruction) {
		case "Instruccion":
			result = Instruccion();
			break;
		case "Instrucciones":
			result = Instrucciones();
			break;
		case "Condicion":
			result = Condicion();
			break;
		case "Condiciones":
			result = Condiciones();
			break;
		case "If":
			result = If();
			break;
		case "Else":
			result = Else();
			break;
		case "Declaracion":
			result = Declaracion();
			break;
		case "Asignacion":
			result = Asignacion(true);
			break;
		case "For":
			result = For();
			break;
		case "While":
			result = While();
			break;
		case "ListaVariables":
			result = ListaVariables();
			break;
		case "Expresion":
			result = Expresion();
			break;
		case "IncrementoDecremento":
			result = IncrementoDecremento();
			break;
		case "Variable":
			result = Variable();
			break;
		case "OperadorUnitario":
			result = OperadorUnitario();
			break;
		case "Valor":
			result = Valor();
			break;
		case "Escritura":
			result = Escritura();
			break;
		case "Lectura":
			result = Lectura();
			break;
		}
		lastByteRead = _stackIsCondition.pop();
		return result;
	}

	public static boolean Condicion() throws IOException {
		if (!Expresion())
			return false;
		if (!OperadoresLogicos())
			return false;
		if (!Expresion())
			return false;

		return true;
	}

	public static boolean OperadoresLogicos() throws IOException{
		if(CurrentToken("<"))
			return Expect("<");
		if(CurrentToken(">"))
			return Expect(">");
		if(CurrentToken("<="))
			return Expect("<=");
		if(CurrentToken(">="))
			return Expect(">=");
		if(CurrentToken("!="))
			return Expect("!=");
		if(CurrentToken("=="))
			return Expect("==");
		return false;
    }
    public static boolean ListaEscritura() throws IOException{
        if(CurrentTokenInfo("String"))
        {
            if(!Expect(43))
                return false;
            if(!Expect("+"))
                return false;
            if(!ListaEscritura())
                return false;
            return true;             
        }
        else{
            if(CurrentTokenInFirst("Variable"))
            {
                if(!Variable())
                    return false;
                if(!Expect("+"))
                    return false;
                if(!ListaEscritura())
                     return false;
                return true;    
            }
        }
        return false;
    }
    public static boolean Escritura() throws IOException{
        if(CurrentToken("write")){  
            if(!Expect("write"))
                return false;
            if(!ListaEscritura())
                return false; 
            if(!Expect(";"))
                return false;
            return true;      
        }  
        
        if(CurrentToken("writeln"))
           {
                if(!Expect("writeln"))
                    return false;
                if(!ListaEscritura())
                    return false;                   
                if(!Expect(";"))
                    return false;
                return true;
           }
       return false;
    }
	public static boolean Instruccion() throws IOException {
		// <For> | <While> | <If> | <Asignaciï¿½n> | <Lectura> | <Escritura> |
		// <Declaraciï¿½n>
		if (CurrentTokenInFirst("If")) {
			return If();
		}
		if (CurrentTokenInFirst("For")) {
			return For();
		}
		if (CurrentTokenInFirst("While")) {
			return While();
		}
		if (CurrentTokenInFirst("Asignacion")) {
			return Asignacion(true);
		}
		if (CurrentTokenInFirst("Declaracion")) {
			return Declaracion();
		}
		if(CurrentTokenInFirst("Escritura")){
			return Escritura();
		}
		if(CurrentTokenInFirst("Lectura")){
			return Lectura();
		}
		_currentToken = Tokenizer();
		MessageError("InstruccionInvalida","La instruccion "+_currentToken.description+" no es valida.");
		return false;
	}

	public static boolean Lectura() throws IOException{
	       // Read <ListaVariables> ;
	           if(!Expect("read"))
	               return false;
	           if(!ListaVariables())
	               return false;
	           if(!Expect(";"))
	               return false;
	           return true;
	 }
	public static boolean If() throws IOException{
	    //	If ( <Condiciones> ) “{“ <Instrucciones> “}” [ Else “{“ <Instrucciones> “}” ]
	        if(!Expect("if"))
	            return false;
	        if (!Expect("("))
	            return false;
	        if(!Condiciones())
	            return false;
	        if(!Expect(")"))
	            return false;
	        if(!Expect("{"))
	            return false;
	        if(!Instrucciones())
	            return false;
	        if(!Expect("}"))
	            return false;
	        if(CurrentTokenInFirst("Else"))
	            if(!Else())
	                return false;
	        return true;
	    }

    public static boolean Else() throws IOException{
        //else “{“ <Instrucciones> “}”
        if(!Expect("else"))
            return false;

        if(!Expect("{"))
            return false;
        if(!Instrucciones())
            return false;
        if(!Expect("}"))
            return false;
        return true;
    }

	public static void MessageError(String error, String messageError) {
		switch (error) {
		case "Expect":
			System.out.println("Error en Expect, " + messageError);
			break;
		case "InstruccionInvalida":
			System.out.println("Instruccion no identificada, " + messageError);
			break;
		default:
			System.out.println("Error no identificado, " + messageError);
		}
		
		System.exit(0);
	}

	public static Token Tokenizer() throws IOException {
		Token tokenToReturn = new Token();
		tokenToReturn.description = ReadTokenFromFile();
		tokenToReturn.code = GetTokenCode(tokenToReturn.description);
		if(tokenToReturn.code == 43){
			tokenToReturn.info = GetTokenConstantType(tokenToReturn.description);
		}
		
		return tokenToReturn;
	}
	public static String GetTokenConstantType(String tokenDescription){
		if(tokenDescription.charAt(0) == '"'){
			return "String";
		}
		
		if(tokenDescription.charAt(0) == 39){
			return "Char";
		}
		
		for(int i = 0; i < tokenDescription.length(); i++){
			if(tokenDescription.charAt(i)== '.'){
				return "DoubleFloat";
			}
		}
		return "Int";
	}
	public static void openFile() throws IOException {
		Frame f = new Frame();
		boolean error = false;
		FileDialog fd = new FileDialog(f, "Choose a file", FileDialog.LOAD);
		fd.setDirectory("C:\\");
		fd.setFile("*.KWBG");
		fd.setVisible(true);
		String fileName = "";
		String fileDir = "";

		try {
			fileName = fd.getFile();
			fileDir = fd.getDirectory();
			if (fileName == null) {
				System.out.println("You cancelled the choice");
				error = true;
			} else {
				System.out.println("You chose " + fileName);

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			error = true;
		}
		if (error)
			System.exit(0);
		f.dispose();

		_bytesInFile = Files.readAllBytes(Paths.get(fileDir, fileName));
	}

	public static boolean Condiciones() throws IOException {
		// <CondiciÃ³n> { <ANDOR> <Condiciones>} | (<Condiciones>)
		
		if(CurrentTokenInFirst("Condicion")){
			if(!Condicion()){
				return false;
			}
			if(CurrentTokenInFirst("AndOr")){
				if(!AndOr())
					return false;
				while (CurrentTokenInFirst("Condiciones")) {
					if (!Condiciones())
						return false;
				}
			}
		}
		
		if(CurrentToken("(")){
			if(!Expect("("))
				return false;
			if(!Condicion())
				return false;
			if(!Expect(")"))
				return false;
		}	
		return true;
		
	}

	public static boolean AndOr() throws IOException {
		if (CurrentToken("AND"))
			return Expect("AND");
		if (CurrentToken("OR"))
			return Expect("OR");
		return false;
	}

	public static boolean For() throws IOException{
	       // For ( [ <Asignacion> ] ; <Condiciones> ; [ <Asignacion> ] ) "{" <Instrucciones> "}" 
	           if(!Expect("for"))
	               return false;
	           if(!Expect("("))
	               return false;
	           if(CurrentTokenInFirst("Asignacion"))
	        	   if(!Asignacion(false))
	        		   return false;
	           if(!Expect(";"))
	               return false;
	           if(!Condiciones())
	               return false;
	           if(!Expect(";"))
	               return false;
	           if(CurrentTokenInFirst("Asignacion"))
	        	   if(!Asignacion(false))
	        		   return false;
	           if(!Expect(")"))
	               return false;
	           if(!Expect("{"))
	               return false;
	           if(!Instrucciones())
	        	   return false;
	           if(!Expect("}"))
	               return false;
	           
	           return true;
	 }

	public static boolean Asignacion(boolean usesSemiColon) throws IOException{
		if(CurrentTokenInFirst("IncrementoDecremento")){
			if(!IncrementoDecremento())
				return false;
			if(usesSemiColon)
				return Expect(";");
			return true;
		}
		if(CurrentTokenInFirst("Variable")){
			if(!Variable())
				return false;
			if(!OperadorAsignacion())
				return false;
			if(!Expresion())
				return false;
			if(usesSemiColon)
				return Expect(";");
			return true;		
		}
		return false;
	}
	public static boolean IncrementoDecremento() throws IOException{
		if(!Variable())
			return false;
		if(CurrentToken("++"))
			return Expect("++");
		if(CurrentToken("--"))
			return Expect("--");	
		
		return false;
	}
	public static boolean OperadorAsignacion() throws IOException{
		if(CurrentToken("="))
			return Expect("=");
		if(CurrentToken("+="))
			return Expect("+=");
		if(CurrentToken("-="))
			return Expect("-=");
		if(CurrentToken("*="))
			return Expect("*=");
		if(CurrentToken("/="))
			return Expect("/=");
		if(CurrentToken("%="))
			return Expect("%=");
		return false;
	}
	public static boolean Operador() throws IOException{
		if(CurrentToken("+"))
			return Expect("+");
		if(CurrentToken("-"))
			return Expect("-");
		if(CurrentToken("*"))
			return Expect("*");
		if(CurrentToken("/"))
			return Expect("/");
		if(CurrentToken("%"))
			return Expect("%");
		return false;
	}
	public static boolean OperadorUnitario() throws IOException{
		if(CurrentToken("-"))
			return Expect("-");
		return false;
	}
	/*public static boolean Expresion() throws IOException{
		//<Expresión> <OperadorAritmetico> <Expresión> | <OperadorUnitario> <Expresión> | (<Expresión>) | <Valor>
		if(CurrentTokenInFirst("Expresion")){
			if(!Expresion())
				return false;
			if(!Operador())
				return false;
			if(!Expresion())
				return false;
			return true;
		}
		if(CurrentTokenInFirst("OperadorUnitario")){
			if(!OperadorUnitario())
				return false;
			if(!Expresion())
				return false;
			return true;
		}
		if(CurrentToken("(")){
			Expect("(");
			if(!Expresion())
				return false;
			return Expect(")");
		}
		if(CurrentTokenInFirst("Valor")){
			if(!Valor())
				return false;
			return true;
		}
		return false;
	}
*/
	public static boolean Expresion() throws IOException{
		//<Termino>|<Termino><OperadorSUma><Expresion>  //
			if(Termino()){
				if(CurrentToken("+") || CurrentToken("-")){
					if(CurrentToken("+"))
						Expect("+");
					else
						Expect("-");
					return Expresion();
				}
				return true;
			}
			return false;
		}
		public static boolean Termino() throws IOException{
			if(Factor()){
				if(CurrentToken("*") || CurrentToken("/") || CurrentToken("%")){
					if(CurrentToken("*"))
						Expect("*");
					else{
						if(CurrentToken("/"))
							Expect("/");
						else
							Expect("%");
					}
					return Termino();
				}
				return true;
			}
			return false;
		}
		public static boolean Factor() throws IOException{
			if(CurrentTokenInFirst("Valor")){
				return Valor();
			}
			if(CurrentToken("(")){
				Expect("(");
				if(!Expresion())
					return false;
				return Expect(")");
			}
			return false;
		}
		
/*	public static boolean Operaciones() throws IOException {
		if (Operacion()) {
			if (GetTokenCode(_currentToken.description) != 19) {
				if (!Operaciones())
					return false;
			} else {
				Expect(GetTokenCode(";"));
				return true;
			}
		} else
			return false;
		return true;
	}
*/
	public static boolean Operacion() throws IOException {
		if (!Operador())
			return false;

		if (GetTokenCode(_currentToken.description) == 23) {
			Expect(GetTokenCode("("));
		}

		if (!Valor())
			return false;

		if (GetTokenCode(_currentToken.description) == 24) {
			Expect(GetTokenCode(")"));
		}
		return true;
	}

    public static boolean Valor() throws IOException{
        // 43 - Constante, 44 - Variable Declarada
        if(CurrentTokenInFirst("Variable"))
           return Variable();
        
        if(!Expect(43))
           return false;
        
        return true;
    }

	

    public static boolean While()  throws IOException{
        if(!Expect("while"))
            return false;
        if(!Expect("("))
            return false;  
        if(!Condiciones())
            return false;     
        if(!Expect(")"))
            return false;
        if(!Expect("{"))
            return false;
        if(!Instrucciones())
            return false;
        if(!Expect("}"))
            return false;
        return true;
    }

	public static String ReadTokenFromFile() throws IOException {

		// 9 - Tab
		// 10 - Salto de linea
		// 32 - Espacio
		// 33 - !
		// 40 - Abrir parentesis
		// 41 - Cerrar parentesis
		// 42 - *
		// 43 - +
		// 44 - ,
		// 45 - -
		// 47 - /
		// 59 - ;
		// 60 - <
		// 61 - =
		// 62 - >
		// 91 - Abrir corchete
		// 92 - \
		// 93 - Cerrar corchete
		// 123 - Abrir llave
		// 125 - Cerrar Llave

		boolean isComplete = false;
		String tokenWord = "";

		boolean commentFound = false;
		boolean quotationFound = false;

		while (!isComplete) {

			boolean increaseByte = false;
			if (!commentFound) {
				switch (_bytesInFile[lastByteRead]) {

				// Separadores de palabra que no se convierten a token
				case 9:
				case 10:
				case 13:
				case 32:
					increaseByte = true;
					if (!quotationFound) {
						if (!tokenWord.equals("")) {
							isComplete = true;
						}
					} else {
						tokenWord += (char) _bytesInFile[lastByteRead];
					}

					break;

				// Comentarios
				case 92:
					increaseByte = true;
					if (!quotationFound) {
						commentFound = true;
					}
					else{
						tokenWord += (char) _bytesInFile[lastByteRead];
					}
					break;

				// Separadores de palabra que se convierten a token
				case 33:
				case 40:
				case 41:
				case 42:
				case 43:
				case 44:
				case 45:
				case 47:
				case 59:
				case 60:
				case 61:
				case 62:
					// case 91:
					// case 93:
				case 123:
				case 125:
					if (!quotationFound) {
						if (tokenWord.length() == 0) {
							tokenWord += (char) _bytesInFile[lastByteRead];
							increaseByte = true;
						}
						isComplete = true;
						break;
					} else {
						increaseByte = true;
					}

					// No separadores de palabra
				default:
					if (_bytesInFile[lastByteRead] == 34) {
						quotationFound = !quotationFound;
					}
					increaseByte = true;
					tokenWord += (char) _bytesInFile[lastByteRead];
					break;
				}

				if (increaseByte) {
					lastByteRead++;
				}

				if (lastByteRead == _bytesInFile.length) {
					isFileFinished = true;
					isComplete = true;
				}

			} else {
				if (_bytesInFile[lastByteRead] == 10) {
					commentFound = false;
				}
				lastByteRead++;
			}

		}

		return tokenWord;
	}

	public static int GetTokenCode(String token) {
		switch (token) {

		case "+":
			return 1;
		case "-":
			return 2;
		case "/":
			return 3;
		case "*":
			return 4;
		case "%":
			return 5;
		case "=":
			return 6;
		case "++":
			return 7;
		case "--":
			return 8;
		case "+=":
			return 9;
		case "-=":
			return 10;
		case ">=":
			return 11;
		case "<=":
			return 12;
		case "==":
			return 13;
		case "!=":
			return 14;
		case ">":
			return 15;
		case "<":
			return 16;
		case "AND":
			return 17;
		case "OR":
			return 18;
		case ";":
			return 19;
		case "break":
			return 20;
		case "continue":
			return 21;
		case "\"":
			return 22;
		case "(":
			return 23;
		case ")":
			return 24;
		case "{":
			return 25;
		case "}":
			return 26;
		case "[":
			return 27;
		case "]":
			return 28;
		case ",":
			return 29;
		case "for":
			return 30;
		case "while":
			return 31;
		case "if":
			return 32;
		case "else":
			return 33;
		case "#int":
			return 34;
		case "#float":
			return 35;
		case "#double":
			return 36;
		case "#char":
			return 37;
		case "#string":
			return 38;
		case "write":
			return 39;
		case "writeln":
			return 40;
		case "read":
			return 41;
		case "charAt":
			return 42;
		default:
			if (isNumber(token) || token.charAt(0) == '"' || ("" + token.charAt(0)).equals("'"))
				return 43;
			if (isVariableInTable(token))
				return 44;
			return 45;
		}
	}

	public static boolean isNumber(String tokenWord) {
		for (int i = 0; i < tokenWord.length(); i++) {
			if ((tokenWord.charAt(i) - 50 < 0 || tokenWord.charAt(i) - 50 > 9) && tokenWord.charAt(i) == 41) {
				return false;
			}
		}
		return false;
	}

	public static boolean isVariableInTable(String variableName) {
		for (int i = 0; i < _variablesTable.length; i++) {
			if ((_variablesTable[i].name).equals(variableName)) {
				return true;
			}
		}
		return false;
	}

}
