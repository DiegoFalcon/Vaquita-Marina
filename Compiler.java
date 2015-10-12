package compiler;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {

	static int lastByteRead = 0;
	static boolean isFileFinished = false;
	static byte[] _bytesInFile;
	static Token _currentToken;
	static Variable _variablesTable[]=new Variable[0];
	static Token _arrayToken[];
	static boolean _isCondition = false;
	static int tempLastByteRead = 0;
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
		_currentToken = Tokenizer();
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
		_currentToken=Tokenizer();
		if(!ListaVariables())
			return false;
		_currentToken=Tokenizer();
		if(!Expect(";"))
			return false;
		_currentToken=Tokenizer();
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
	public static boolean Variables() throws IOException{
		if(!Expect(45))
			return false;
		_currentToken=Tokenizer();
		
		return true;
	}
	public static boolean TipoDato(){
		switch(GetTokenCode(_currentToken.description)){
			case 34:
				if(!Expect(GetTokenCode("#int")))
					return false;
				return true;
			case 35:
				if(!Expect(GetTokenCode("#float")))
					return false;
				return true;
			case 36:
				if(!Expect(GetTokenCode("#double")))
					return false;
				return true;
			case 37:
				if(!Expect(GetTokenCode("#char")))
					return false;
				return true;
			case 38:
				if(!Expect(GetTokenCode("#string")))
					return false;
				return true;
			default: 
				return false;
		}
	}
	public static boolean Expect(int tokenCode){
        if(_currentToken.code==tokenCode){
        	if(!_isCondition)
        	System.out.println(_currentToken.description);
        	return true;
        }
        if(!_isCondition){
       MessageError("Error de Sintaxis","El token: "+tokenCode+" no existe.");
        System.exit(0);
        }
        return false;
    }
	public static boolean Expect(String instruction){
        if(_currentToken.description.equals(instruction)){
        	if(!_isCondition)
        	System.out.println(instruction);
        	return true;
        }
        if(!_isCondition){
        	MessageError("Error de Sintaxis","El token: "+instruction+" no existe.");  	
        	System.exit(0);
        }
        
        return false;
    }
	public static boolean CurrentTokenInFirst(String instruction) throws IOException{
		tempLastByteRead = lastByteRead;
		boolean result = false;
		_isCondition = true;
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
		return result;
	}
	 public static boolean Condicion() throws IOException{
         Expresion();
         _currentToken = Tokenizer();
         OperadoresLogicos();
         _currentToken = Tokenizer();
         Expresion();
         _currentToken = Tokenizer();
         
         return true;
     }
	 public static boolean OperadoresLogicos(){
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
	public static void Lectura(){
        // Read <ListaVariables> ;
        Expect("read");
        _currentToken = Tokenizer();
        ListaVariables();
        _currentToken = Tokenizer();
        Expect (";");
        _currentToken = Tokenizer();
    }*/
	public static boolean If() throws IOException{
	//	If ( <Condiciones> ) “{“ <Instrucciones> “}” [ Else “{“ <Instrucciones> “}” ]
        if(!Expect("if"))
        	return false;
        _currentToken = Tokenizer();
        if(!Expect("("))
        	return false;
        _currentToken = Tokenizer();
        if(!Condiciones())
        	return false;
        _currentToken = Tokenizer();
        if(!Expect(")"))
        	return false;
        _currentToken = Tokenizer();
        if(!Expect("{"))
        	return false;
        _currentToken = Tokenizer();
        Instrucciones();
        _currentToken = Tokenizer();
        if(!Expect("}"))
        	return false;
        _currentToken = Tokenizer();
        
        if(_currentToken.description.equals("else")){
        	if(!Expect("else"))
        		return false;
            _currentToken = Tokenizer();
            if(! Expect("{"))
            	return false;
            _currentToken = Tokenizer();
            Instrucciones();
            _currentToken = Tokenizer();
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
		while(CurrentTokenInFirst("condicion")){
			Condicion();
		}
		
		return true;
	}
	


public static boolean For() throws IOException{
    // For ( [ <Asignación> ] ; <Condiciones> ; [ <Asignación> ] ) “{“ <Instrucciones> “}”
        if(!Expect("for"))
            return false;
        _currentToken = Tokenizer();
        if(!Expect("("))
            return false;
        _currentToken = Tokenizer();
        if(!_currentToken.equals(";")){
            if(!Asignacion())
            	return false;
            _currentToken = Tokenizer();
        }
        if(!Expect(";"))
            return false;
        _currentToken = Tokenizer();
        if(!Condiciones())
        	return false;
        _currentToken = Tokenizer();
        if(!Expect(";"))
            return false;
        _currentToken = Tokenizer();
        if(!_currentToken.equals(")")){
            if(!Asignacion())
            	return false;
            _currentToken = Tokenizer();
        }
        if(!Expect(";"))
            return false;
        _currentToken = Tokenizer();
        if(!Expect("{"))
            return false;
        _currentToken = Tokenizer();
        if(!Instrucciones())
        	return false;
        _currentToken = Tokenizer();
        if(!Expect("}"))
            return false;
        _currentToken = Tokenizer();

    return true;
}
	public static boolean Asignacion() throws IOException{
		if(!Expect(44))
			return false;
		_currentToken=Tokenizer();
		if(!OperadorAsignacion())
			return false;
		if(!Expresion())
			return false;
		
		return true;
	}
	public static boolean OperadorAsignacion(){
		switch(_currentToken.description){
			case "=":
				if(!Expect(GetTokenCode("=")))
					return false;
				return true;
			case "+=":
				if(!Expect(GetTokenCode("+=")))
					return false;
				return true;
			case "-=":
				if(!Expect(GetTokenCode("-=")))
					return false;
				return true;
			default:
				return false;
		}
	}
	
	public static boolean Expresion() throws IOException{
		_currentToken=Tokenizer();
		if(GetTokenCode(_currentToken.description)==23){
			if(!Expect(GetTokenCode("(")))
				return false;
			_currentToken=Tokenizer();
		}
		
		if(!Valor())
			return false;
		if(!Operaciones())
			return false;
		
		return true;
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
		
		_currentToken=Tokenizer();
		if(GetTokenCode(_currentToken.description)==23){
			Expect(GetTokenCode("("));
			_currentToken=Tokenizer();
		}
		
		if(!Valor())
			return false;
		
		_currentToken=Tokenizer();
		if(GetTokenCode(_currentToken.description)==24){
			Expect(GetTokenCode(")"));
			_currentToken=Tokenizer();
		}
		return true;
	}
	
	public static boolean Valor(){
		switch(GetTokenCode(_currentToken.description)){
			case 43:
				if(!Expect(43))
                                    return false;
				return true;
			case 44:
				if(!Expect(44))
                                    return false;
				return true;
			default:
				return false;
		}
	}
	
	
	
	public static boolean Operador(){
		switch(_currentToken.description){
			case "+":
				Expect(GetTokenCode("+"));
				return true;
			case "-":
				Expect(GetTokenCode("-"));
				return true;
			case "*":
				Expect(GetTokenCode("*"));
				return true;
			case "/":
				Expect(GetTokenCode("/"));
				return true;
			case "%":
				Expect(GetTokenCode("%"));
				return true;
			default:
				return false;
		}
	}
	public static boolean While()  throws IOException{
        if(!Expect("while"))
            return false;
        _currentToken = Tokenizer();
        if(!Expect("("))
            return false;
        _currentToken = Tokenizer();
        if(!Condiciones())
        	return false;
        _currentToken = Tokenizer();
        if(!Expect(")"))
            return false;
        _currentToken = Tokenizer();
        if(!Expect("{"))
            return false;
        _currentToken = Tokenizer();
        if(!Instrucciones())
        	return false;
        _currentToken = Tokenizer();
        if(!Expect("}"))
            return false;
        _currentToken = Tokenizer();
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
				case 91:
				case 93:
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
            	if(isNumber(token) || token.charAt(0)=='"')
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
