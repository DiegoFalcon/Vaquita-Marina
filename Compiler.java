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
	static Variable _variablesTable[]=new Variable[0];
	static Token _arrayToken[];
	static boolean _isCondition = false;
	static Stack<Boolean> _stackIsCondition = new Stack<Boolean>();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		openFile();
		//while (!isFileFinished) {
	//		_currentToken = Tokenizer();
	//		
		//}
		
	/*	while(!isFileFinished){
			 Token array_TempToken[];
			 Token tokenToAdd = Tokenizer();
		        int array_dataLength = _arrayToken.length;
		        array_TempToken = new Token[array_dataLength];

		        for (int index=0; index<array_TempToken.length; index++){
		        	array_TempToken[index]=_arrayToken[index];
		        }
		        
		        //array_tempData = array_data;
		        _arrayToken = new Token[_arrayToken.length+1];

		        for (int index=0; index<array_dataLength; index++){
		        	_arrayToken[index]=array_TempToken[index];

		        }
		        
		        _arrayToken[_arrayToken.length]=tokenToAdd;
		}
		*/
		Instrucciones();
	}

	public static void AddToTokenArray(){
		
	}
	public static boolean Instrucciones() throws IOException{
		//<Instrucción> {<Instrucciones>}
		//_currentToken = Tokenizer();
		while(CurrentTokenInFirst("instruccion")){
			if(!Instruccion())
				return false;
		}
		
		return true;
	}

	public static boolean Declaracion() throws IOException{
		//#<TipoDato> <ListaVariables> ;
		if(!TipoDato())
			return false;
		if(!ListaVariables())
				return false;
		if(!Expect(";"))
			return false;
		return true;
	}
	public static boolean ListaVariables() throws IOException{
		//<Variable> {,<ListaVariables>}
		if(!Variables())
			return false;
		
		while(CurrentTokenInFirst("ListaVariables")){
			if(!Expect(","))
				return false;
			if(!ListaVariables())
				return false;
		}
		
		return true;
	}
	public static boolean Letra() throws IOException{
        if(Expect("[a-zA-Z]+"))
            return true;
        return false;
    }
	public static boolean Digito()throws IOException{
        if(_currentToken.description.equals("0"))
            if(!Expect("0"))
                return false;
        else if(_currentToken.description.equals("1"))
            if(!Expect("1"))
                return false;
        else if(_currentToken.description.equals("2"))
            if(!Expect("2"))
                return false;
        else if(_currentToken.description.equals("3"))
            if(!Expect("3"))
                return false;
        else if(_currentToken.description.equals("4"))
            if(!Expect("4"))
                return false;
        else if(_currentToken.description.equals("5"))
            if(!Expect("5"))
                return false;
        else if(_currentToken.description.equals("6"))
            if(!Expect("6"))
                return false;
        else if(_currentToken.description.equals("7"))
            if(!Expect("7"))
                return false;
        else if(_currentToken.description.equals("8"))
            if(!Expect("8"))
                return false;
        else if(_currentToken.description.equals("9"))
            if(!Expect("9"))
                return false;
        return true;
    }
    public static boolean Comentarios() throws IOException{
        if(Expect("\\"))
            return true;
        return false;
    }
	public static boolean Variables() throws IOException{
		if(CurrentToken(45))
			return Expect(45);
		if(CurrentToken(44))
			return Expect(44);
		
		return false;
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
	public static boolean Expect(int tokenCode) throws IOException{
		_currentToken = Tokenizer();
        if(_currentToken.code==tokenCode){
        	if(_stackIsCondition.isEmpty())
        	System.out.println(_currentToken.description);
        	
        	return true;
        }
        if(_stackIsCondition.isEmpty()){
        	MessageError("Error de Sintaxis","El token: "+tokenCode+" no existe.");
        	System.exit(0);
        }
        return false;
    }
	public static boolean Expect(String instruction) throws IOException{
		_currentToken = Tokenizer();
        if(_currentToken.description.equals(instruction)){
        	if(_stackIsCondition.isEmpty())
        	System.out.println(instruction);
        	
        	return true;
        }
        if(_stackIsCondition.isEmpty()){
        	MessageError("Error de Sintaxis","El token: "+instruction+" no existe.");  	
        	System.exit(0);
        }
        return false;
    }
	public static boolean CurrentToken(String instruction) throws IOException{
		int tempLastByteRead = lastByteRead;
		_isCondition = true;
		_stackIsCondition.push(true);
		if(!Expect(instruction)){
			lastByteRead=tempLastByteRead;
			_isCondition=false;
			_stackIsCondition.pop();
			return false;
		}
		_isCondition=false;
		_stackIsCondition.pop();
		lastByteRead=tempLastByteRead;
		return true;
	}
	public static boolean CurrentToken(int instruction) throws IOException{
		int tempLastByteRead = lastByteRead;
		_isCondition = true;
		_stackIsCondition.push(true);
		if(!Expect(instruction)){
			_isCondition=false;
			_stackIsCondition.pop();
			lastByteRead=tempLastByteRead;	
			return false;
		}
		_isCondition=false;
		_stackIsCondition.pop();
		lastByteRead=tempLastByteRead;	
		return true;
	}
	public static boolean CurrentTokenInFirst(String instruction) throws IOException{
		int tempLastByteRead = lastByteRead;
		boolean result = false;
		_isCondition = true;
		_stackIsCondition.push(true);
		//System.out.println("El temporal al principio en CurrentToken "+instruction+" = "+tempLastByteRead);
		//System.out.println("La condicion al principio en CurrentToken "+instruction+" = "+_isCondition);
		switch (instruction){
			case "instruccion":
				result = Instruccion();
			break;
			case "instrucciones":
				result = Instrucciones();
			break;
			case "condicion":
				result = Condicion();
			break;
			case "if":
				result = If();
				break;
			case "declaracion":
				result = Declaracion();
				break;
			case "asignacion":
				result = Asignacion();
				break;
			case "for":
				result = For();
				break;
			case "while":
				result = While();
				break;
			case "ListaVariables":
				result = ListaVariables();
		}
		_isCondition=false;
		lastByteRead = tempLastByteRead;
		_stackIsCondition.pop();
		//System.out.println("El temporal al final en CurrentToken "+instruction+" = "+tempLastByteRead);
		//System.out.println("La condicion al final en CurrentToken "+instruction+" = "+_isCondition);
		return result;
	}
	 public static boolean Condicion() throws IOException{
         if(!Expresion())
        	 return false;
         if(!OperadoresLogicos())
        	 return false;
         if(!Expresion())
        	 return false;
         
         return true;
     }
	 public static boolean OperadoresLogicos() throws IOException{
         String currentOperador = _currentToken.description;
         switch(currentOperador){
             case ">":
            	 if(!Expect(">"))
                    return false;
                 return true;
             case "<":
                if(!Expect("<"))
                    return false;
                 return true;
             case "<=":
                if(!Expect("<="))
                    return false;
                 return true;
             case ">=":
                if(!Expect(">="))
                    return false;
                 return true;
             case "==":
                if(!Expect("=="))
                    return false;
                 return true;
             case "!=":
                if(!Expect("!="))
                    return false;
                 return true;
         }
         
         return true;
     }
	public static boolean Instruccion() throws IOException{
	//	<For> | <While> | <If> | <Asignación> | <Lectura> | <Escritura> | <Declaración>
		if (CurrentTokenInFirst("if")){
			return If();
		}
		if (CurrentTokenInFirst("for")){
			return For();
		}
		if (CurrentTokenInFirst("while")){
			return While();
		}
		if (CurrentTokenInFirst("asignacion")){
			return Asignacion();
		}
		if (CurrentTokenInFirst("declaracion")){
			return Declaracion();
		}
		
		return false;
	}
	/*public static void Escritura(){
        Expect("write");
        _currentToken = Tokenizer();
        ListaEscritura();
        _currentToken = Tokenizer();
        Expect(";");
        _currentToken = Tokenizer();
    }
	public static void Lectura() throws IOException{
        // Read <ListaVariables> ;
        Expect("read");
        _currentToken = Tokenizer();
        ListaVariables();
        _currentToken = Tokenizer();
        Expect (";");
        _currentToken = Tokenizer();
    }
	public static void ListaEscritura(){
        //Si es mensaje
        if(_currentToken == Mensaje)
            Mensaje();
        else
            Variables();
        _currentToken = Tokenizer();
        if(_currentToken.info.equals("+")){
            Expect("+");
            _currentToken = Tokenizer();
            ListaEscritura();
            _currentToken = Tokenizer();
        }
    }
    */
	public static boolean If() throws IOException{
	//	If ( <Condiciones> ) “{“ <Instrucciones> “}” [ Else “{“ <Instrucciones> “}” ]
        if(!Expect("if"))
        	return false;
        if(!Expect("("))
        	return false;
        if(!Condiciones())
        	return false;
        if(!Expect(")"))
        	return false;
        if(!Expect("{"))
        	return false;
        Instrucciones();
        if(!Expect("}"))
        	return false;
        
        if(_currentToken.description.equals("else")){
        	if(!Expect("else"))
        		return false;
            if(!Expect("{"))
            	return false;
            Instrucciones();
            if(!Expect("}"))
            	return false;
        }
        
        return true;
    }

	public static void MessageError(String error,String messageError){
		switch(error){
			case "Error de Sintaxis":
				System.out.println("Error de sintaxis, "+messageError);
				break;
				
			default: 
				System.out.println("Error no identificado, "+messageError);
		}
	}
	public static Token Tokenizer() throws IOException{
		Token tokenToReturn = new Token();
		tokenToReturn.description = ReadTokenFromFile();
		tokenToReturn.code = GetTokenCode(tokenToReturn.description);
		return tokenToReturn;
	}
		
	public static void openFile() throws IOException{
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
	public static boolean Condiciones() throws IOException{
		//<Condición> { <ANDOR> <Condiciones> }
		if(!Condicion())
			return false;
		
		while(CurrentTokenInFirst("condicion")){
			if(!AndOr())
				return false;
			if(Condicion())
				return false;
		}
		
		return true;
	}
	
	public static boolean AndOr() throws IOException{
		if(CurrentToken("AND"))
			return Expect("AND");
		if(CurrentToken("OR"))
			return Expect("OR");
		return false;
	}

public static boolean For() throws IOException{
    // For ( [ <Asignación> ] ; <Condiciones> ; [ <Asignación> ] ) “{“ <Instrucciones> “}”
        if(!Expect("for"))
            return false;
        if(!Expect("("))
            return false;
        if(!_currentToken.equals(";")){
            if(!Asignacion())
            	return false;
        }
        if(!Expect(";"))
            return false;
        if(!Condiciones())
        	return false;
        if(!Expect(";"))
            return false;
        if(!_currentToken.equals(")")){
            if(!Asignacion())
            	return false;
        }
        if(!Expect(";"))
            return false;
        if(!Expect("{"))
            return false;
        if(!Instrucciones())
        	return false;
        if(!Expect("}"))
            return false;

    return true;
}
	public static boolean Asignacion() throws IOException{
		if(!Expect(44))
			return false;
		if(!OperadorAsignacion())
			return false;
		if(!Expresion())
			return false;
		
		return true;
	}
	public static boolean OperadorAsignacion() throws IOException{
		if(CurrentToken("="))
			return Expect("=");
		if(CurrentToken("+="))
			return Expect("+=");
		if(CurrentToken("-="))
			return Expect("-=");
		return false;
	}
	
	public static boolean Expresion() throws IOException{
		
		//<Valor> | [ ( ]<Valor><Operaciones>
		

		if(CurrentTokenInFirst("Valor")){
			if(!Valor())
				return false;
			if(_currentToken.code==GetTokenCode(";"))
				return Expect(";");
		}
		
		if(GetTokenCode(_currentToken.description)==23)
			Expect(GetTokenCode("("));
		
		if(!Valor())
			return false;
		if(!Operaciones())
			return false;
		
		if(_currentToken.code==GetTokenCode(";"))
			return Expect(";");
		else
			if(_currentToken.code==GetTokenCode(")"))
				return true;
		
		return false;
	}
	public static boolean Operaciones() throws IOException{
		if(Operacion()){
			if(GetTokenCode(_currentToken.description)!=19){
				if(!Operaciones())
					return false;
			}
			else{
				Expect(GetTokenCode(";"));
				return true;
			}
		}
		else
			return false;
		return true;
	}
	
	public static boolean Operacion() throws IOException{
		if(!Operador())
			return false;
		
		if(GetTokenCode(_currentToken.description)==23){
			Expect(GetTokenCode("("));
		}
		
		if(!Valor())
			return false;
		
		if(GetTokenCode(_currentToken.description)==24){
			Expect(GetTokenCode(")"));
		}
		return true;
	}
	
	public static boolean Valor() throws IOException{
		if(CurrentToken(43))
			return Expect(43);
		if(CurrentToken(44))
			return Expect(44);
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
            return false;;
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
					if(!quotationFound){
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
					if(!quotationFound){
						commentFound = true;
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
				//case 91:
				//case 93:
				case 123:
				case 125:
					if(!quotationFound){
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
					if( _bytesInFile[lastByteRead] == 34){
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
				if(_bytesInFile[lastByteRead] == 10){
					commentFound = false;
				}
				lastByteRead++;
			}

		}

		return tokenWord;
	}

	public static int GetTokenCode(String token)
    {
        switch(token)
        {
        	
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
            	if(isNumber(token) || token.charAt(0)=='"' || (""+token.charAt(0)).equals("'"))
            		return 43;
            	if(isVariableInTable(token))
            		return 44;
            	return 45;
        }
    }

	public static boolean isNumber(String tokenWord){
		for(int i=0;i<tokenWord.length();i++){
			if((tokenWord.charAt(i)-50<0 || tokenWord.charAt(i)-50>9) && tokenWord.charAt(i)==41){
				return false;
			}
		}
		return false;
	}
	
	public static boolean isVariableInTable(String variableName){
		for(int i=0; i<_variablesTable.length;i++){
			if((_variablesTable[i].name).equals(variableName)){
				return true;
			}
		}
		return false;
	}


}
