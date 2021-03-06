import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
//import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Compiler {

    static int lastByteRead = 0;
    static boolean lastTokenReadOperator = false;
    static boolean lastTokenReadSubstractOperator = false;
    static boolean isFileFinished = false;
    static byte[] _bytesInFile;
    static Token _currentToken;
    static int _tagNumber = 0;
    static int _SC = 0;
    static String _filename;
    static Variable _variablesTable[] = new Variable[0];
    static Token _arrayToken[];
    static String _currentTypeVariable;
    static Stack<Boolean> _stackInsideInstruction = new Stack<Boolean>();
    static Stack<DatosRecursividad> _stackIsCondition = new Stack<DatosRecursividad>();
    static Stack<Tag> _tagStack = new Stack<Tag>();
    static byte[] _KWA = new byte[0];
    static Stack<Token> _stackValoresExpresion = new Stack<Token>();
    static Stack<Integer> _stackTokensInIndex = new Stack<Integer>();
    static boolean _isDeclaration = false;
    static virtualMachine _callMachine = new virtualMachine();
    static byte[] _arrayTmp = new byte[0];
    static boolean _isArrayAssignment = false;
    static boolean _isAssigned = false;
    static int lineReadNumber = 1;
    static Stack<Token> _stackOperadores = new Stack<Token>();
    static boolean _IndiceVector;
    static boolean _Parentesis = false;
    static Token _variableAsignacion = null;
    
    public static void mainCompiler() throws IOException, InterruptedException {
        // TODO Auto-generated method stub
        Initialize();
        if(!InitializeFile())
            return ;
        cleanLastBytesInFile();
        cleanSpacesInFile();
        addParentesisToDivisionHierarchy();
        cleanLastBytesInFile();
                
        if(Instrucciones()){
            AddInstruction("HALT");
            //JOptionPane.showMessageDialog(null,"Se corrió la semántica correctamente.","Finalizado", JOptionPane.INFORMATION_MESSAGE);
            WriteAssemblyFile();
            _callMachine.mainVirtualMachine(_filename);
            //_filename = null;
        }
        else{
            NewJFrame.jTextArea3.append("Ocurrió un error en la semántica.");
            NewJFrame.jTextArea3.update(NewJFrame.jTextArea3.getGraphics());
        }
    }
    public static void Initialize() throws IOException{
        lastByteRead = 0;
        lastTokenReadOperator = false;
        lastTokenReadSubstractOperator = false;
        isFileFinished = false;
        lineReadNumber = 1;
        _tagNumber = 0;
        _SC = 0;
        _variablesTable = new Variable[0];
        _stackInsideInstruction = new Stack<Boolean>();
        _stackIsCondition = new Stack<DatosRecursividad>();
        _tagStack = new Stack<Tag>();
        _KWA = new byte[0];
        _stackValoresExpresion = new Stack<Token>();
        _stackTokensInIndex = new Stack<Integer>();
        _isDeclaration = false;
        NewJFrame.jTextArea2.setText("");
        NewJFrame.jTextArea3.setText("");
        
        /*File f = new File ("Temp123456789.KWBG");
        String fileDir = f.getAbsolutePath();
        fileDir = fileDir.substring(0, fileDir.length()-5) + ".KWA";

        try {
            Files.delete(Paths.get(fileDir));
        } catch (NoSuchFileException x) {}
        **/
                
    }
    public static boolean Instrucciones() throws IOException {
        // <Instrucci�n> {<Instrucciones>}
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
        if(_stackIsCondition.isEmpty())
            _isDeclaration = true;
        if(!TipoDato())
            return false;
        if(!ListaVariables())
            return false;
        _isDeclaration = false;
        return Expect(";");
}
    public static boolean ListaVariables() throws IOException {
        // <Variables> {,<ListaVariables>}
        String currentNameVariable = GetCurrentToken().description;
        if (!VariableDeclaracion())
            return false;
        if(_stackIsCondition.isEmpty())
            if(!IsArray(currentNameVariable))
                AddToVariableTable(currentNameVariable,_currentTypeVariable);

        if (CurrentToken(",")){
            if(!Expect(","))
                return false;
            while (CurrentTokenInFirst("ListaVariables")) { 
                if (!ListaVariables())
                    return false;
            }
        }

        return true;
    }
    public static boolean VariableDeclaracion() throws IOException{
        Token variable = GetCurrentToken();
        if(!Expect(45))
            return false;
        if(CurrentToken("[")){
            if(_isAssigned)
                _isArrayAssignment = true;
            if(!Expect("[")){
                _isArrayAssignment = false;
                _arrayTmp = new byte[0];
                return false;
            }
            _stackTokensInIndex.push(0);
            if(!IndiceVector(variable)){
                _isArrayAssignment = false;
                _arrayTmp = new byte[0];
                return false;
            }
            _isArrayAssignment = false;
            if(!Expect("]"))
                return false;
        }
        return true;
    }
    public static boolean Variable() throws IOException{
        Token variable = GetCurrentToken();

        if(!Expect(44))
            return false;
        if(CurrentToken("[")){
            if(_isAssigned)
                _isArrayAssignment = true;
          if(!Expect("[")){
                _isArrayAssignment = false;
                 _arrayTmp = new byte[0];
             return false;
          }
          _stackTokensInIndex.push(0);
          if(!IndiceVector(variable)){
              _isArrayAssignment = false;
              _arrayTmp = new byte[0];
              return false;
          }
          _isArrayAssignment = false;
           if(!Expect("]"))
             return false;
        }
        return true;
    }
     public static boolean IndiceVector(Token variable) throws IOException{
        String size = GetCurrentToken().description;
        //if(!_stackOperadores.isEmpty())
        //  System.out.println("aqui es");
        _IndiceVector = true;
            if(CurrentTokenInFirst("Expresion"))
                if(!Expresion()){
                    _IndiceVector = false;
                    return false;
            }
            if(!checkIfIndexArray()){
               _IndiceVector = false;
               return false;
                
                }
            if(!_isDeclaration)
                AddInstruction("POPINDEX");
            else
                for(int i=0 ; i<Integer.parseInt(size) ; i++)
                    AddToVariableTable(variable.description,_currentTypeVariable);
            //if(CurrentTokenInFirst("IncrementoDecremento"))
            //    if(!IncrementoDecremento())
            //        return false;
            _IndiceVector = false;
            return true;
    }
    public static boolean checkIfIndexArray() throws IOException{
        String variableType;
        Token token;
        int indexCount = _stackTokensInIndex.pop();
        // EL TIPO DE DATO DE LA EXPRESION ES DIFERENTE DE LA VARIABLE
        for(int i =0; i<indexCount; i++){
            token = _stackValoresExpresion.pop();
            variableType = GetVariableType(token.description);
            if (variableType != "" && !variableType.equals("Int"))
                return false;
            if(variableType == "" && !isNumber(token.description))
                return false;
        }
        return true;
}
    public static boolean TipoDato() throws IOException{
        if(CurrentToken("#int")){
            _currentTypeVariable = "Int";
            return Expect("#int");
        }
        if(CurrentToken("#float")){
            _currentTypeVariable = "Float";
            return Expect("#float");
        }
        if(CurrentToken("#double")){
            _currentTypeVariable = "Double";
            return Expect("#double");
        }
        if(CurrentToken("#char")){
            _currentTypeVariable = "Char";
            return Expect("#char");
        }
        if(CurrentToken("#string")){
            _currentTypeVariable = "String";
            return Expect("#string");
        }
        return false;   
    }
    public static boolean Expect(int tokenCode) throws IOException {
        if(isFileFinished){
            MessageError("Expect", "no se encontr� punto y coma al final del archivo.");        
            return false;
        }
        _currentToken = Tokenizer();
        if (_currentToken.code == tokenCode) {
            //abre llave
            if(tokenCode==25)
                _stackInsideInstruction.push(true);
            //cierra llave
            if(tokenCode==26)
            {
                _stackInsideInstruction.pop();
            //  _stackInsideInstruction.push(false);
            }
            //if (_stackIsCondition.isEmpty())
                    //System.out.println(_currentToken.description);
                //NewJFrame.jTextArea2.append(_currentToken.description + "\n");

            return true;
        }
        if (_stackIsCondition.isEmpty()) {
            MessageError("Expect", "Token no identificado: " + tokenCode + ", se esperaba un: "+_currentToken.description+". En la linea "+lineReadNumber);
        }
        return false;
    }
    public static boolean Expect(String instruction) throws IOException {
        if(isFileFinished){
            MessageError("Expect", "no se encontr� punto y coma al final del archivo.");        
            return false;
        }
        _currentToken = Tokenizer();    
        if (_currentToken.description.equals(instruction)) {
            if(instruction.equals("{"))
                _stackInsideInstruction.push(true);
            if(instruction.equals("}"))
            {
                _stackInsideInstruction.pop();
                    //_stackInsideInstruction.push(false);
            }
            //if (_stackIsCondition.isEmpty()){
                    //System.out.println(instruction);
                //NewJFrame.jTextArea2.append(instruction + "\n");

            //}
            return true;
        }
        if (_stackIsCondition.isEmpty()) {
            MessageError("Expect", "Codigo de Token no identificado: " + instruction + ", se esperaba un: "+_currentToken.code +". En la linea "+lineReadNumber);
        }
        return false;
    }
    public static Token GetCurrentToken() throws IOException{
        if (isFileFinished)
            return new Token();
        GuardarDatosRecursividad();
        Token tokenToReturn = Tokenizer();
        RestaurarDatosRecursividad();
        return tokenToReturn;
    }
    public static boolean CurrentToken(String instruction) throws IOException {
        if (isFileFinished)
            return false;
        GuardarDatosRecursividad();
        if (!Expect(instruction)) {
            RestaurarDatosRecursividad();
                return false;
        }
        RestaurarDatosRecursividad();
        return true;
    }
    public static boolean CurrentToken(int instruction) throws IOException {
        if (isFileFinished)
            return false;
        GuardarDatosRecursividad();
        if (!Expect(instruction)) {
            RestaurarDatosRecursividad();
            return false;
        }
        RestaurarDatosRecursividad();
        return true;
    }
    public static boolean CurrentTokenInfo(String info) throws IOException{
        if (isFileFinished)
            return false;

        GuardarDatosRecursividad();
        _currentToken = Tokenizer();
        if (_currentToken.info.equals(info)) {
            RestaurarDatosRecursividad();
            return true;
        }       
        RestaurarDatosRecursividad();
        return false;
    }
    public static boolean CurrentTokenInFirst(String instruction) throws IOException {
        if (isFileFinished)
            return false;
        boolean result = false;

        GuardarDatosRecursividad();

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
        case "ListaEscritura":
            result = ListaEscritura();
            break;
        case "ListaLectura":
            result = ListaLectura(1);
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
        case "AndOr":
            result = AndOr();
            break;
        case "AsignacionFor":
            result = Asignacion(false);
            break;
        case "FactorCondicion":
            result = FactorCondicion();
            break;
        }
        RestaurarDatosRecursividad();
        return result;
    }
    public static void GuardarDatosRecursividad(){
        DatosRecursividad dr = new DatosRecursividad(lastByteRead, lastTokenReadSubstractOperator, lastTokenReadOperator, _stackValoresExpresion, _stackTokensInIndex,lineReadNumber);
        _stackIsCondition.push(dr);
    }
    public static void RestaurarDatosRecursividad(){
        DatosRecursividad dr = _stackIsCondition.pop();
        lastByteRead = dr.lastByteRead;
        lastTokenReadSubstractOperator = dr.lastTokenReadSubstractOperator;
        lastTokenReadOperator = dr.lastTokenReadOperator;
        lineReadNumber = dr.lineReadNumber;
        
        _stackValoresExpresion.clear();
        while(!dr.stackValoresExpresion.isEmpty())
          _stackValoresExpresion.push(dr.stackValoresExpresion.pop());
        _stackTokensInIndex.clear();
        while(!dr.stackTokensInIndex.isEmpty())
          _stackTokensInIndex.push(dr.stackTokensInIndex.pop());
        if(lastByteRead < _bytesInFile.length)
            isFileFinished = false;
    }
    public static boolean Condicion() throws IOException {
        //<expresion><operadorlogico><expresion>    
        if (!Expresion())
            return false;
        Token tokenOperator = GetCurrentToken();
        if (!OperadoresLogicos())
            return false;
        if (!Expresion())
            return false;
        String assemblyOperator = TranslateToAssembly(tokenOperator.description);
        AddInstruction(assemblyOperator);
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
            AddInstruction("WRTLN");
            return true;
           }
       return false;
    }
    public static boolean ListaEscritura() throws IOException{
        String variableName = "";
        if(CurrentTokenInFirst("Variable"))
        {
            variableName = GetCurrentToken().description;
            if(!Variable())
                return false;
            AddWrite(0,variableName);
            if(CurrentToken("+")){
                if(!Expect("+"))
                    return false;
                return ListaEscritura();
            }
            return true;    
        }
        if(CurrentTokenInfo("String"))
        {
            AddWrite(1,"");
            if(!Expect(43))
                return false;
            if(CurrentToken("+")){
                if(!Expect("+"))
                    return false;
                return ListaEscritura();
            }
            return true;             
        }
        return false;
    }
    public static void AddWrite(int variableOption, String variable) throws IOException{
        /*
        variableOption, es para diferenciar entre variables y constantes
        0 - Variables
        1 - Constante
        */
        //String variable = GetCurrentToken().description;
        String variableType = GetVariableType(variable);
        
        if(variableOption == 0)
            switch(variableType){
                case "Float":
                    if(!IsArray(variable))
                        AddInstruction("WRTF");
                    else
                        AddInstruction("WRTVF");
                    AddVariable(variable);
                break;
                case "Int":
                    if(!IsArray(variable))
                        AddInstruction("WRTI");
                    else
                        AddInstruction("WRTVI");
                    AddVariable(variable);
                break;
                case "Double":
                    if(!IsArray(variable))
                        AddInstruction("WRTD");
                    else
                        AddInstruction("WRTVD");
                    AddVariable(variable);
                break;
                case "Char":
                    if(!IsArray(variable))
                        AddInstruction("WRTC");
                    else
                        AddInstruction("WRTVC");
                    AddVariable(variable);
                break;
                case "String":
                    if(!IsArray(variable))
                        AddInstruction("WRTS");
                    else
                        AddInstruction("WRTVS");
                    AddVariable(variable);
                break;
        }
        else{ 
            AddInstruction("WRTM");
            AddString(GetCurrentToken().description);
        }
    }
    private static boolean IsArray(String variable){
        int count = 0;
        for(int i=0; i<_variablesTable.length ; i++){
            if(_variablesTable[i].name.equals(variable)){
                count++;
            }
        }
        if(count>1)
            return true;
        return false;
    }
    public static boolean Instruccion() throws IOException {
        // <For> | <While> | <If> | <Asignaci�n> | <Lectura> | <Escritura> |
        // <Declaraci�n>
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
        MessageError("InstruccionInvalida","la instrucción "+_currentToken.description+" no es válida en la línea "+lineReadNumber+"\n");
        return false;
    }
    public static boolean Lectura() throws IOException{
    // Read <ListaLectura> ;
        if(!Expect("read"))
            return false;
        if(!ListaLectura(0))
            return false;
        if(!Expect(";"))
            return false;
        return true;
    }
    public static boolean ListaLectura(int option) throws IOException{
        // <Variables> {,<ListaEscritura>}
        // option es para saber si se mandó desde currentTokenInFirst
        // 0 - no se mandó desde CTIF
        // 1 - Se mandó desde currentTokenInFirst
        
            
        String variableName = GetCurrentToken().description;
        if (!Variable())
            return false;

        if(option != 1)
            AddRead(variableName);

        if (CurrentToken(","))
            if(!Expect(","))
                return false;

        while (CurrentTokenInFirst("ListaLectura")) {   
            if (!ListaLectura(0))
                return false;
        }
        return true;
        }
    public static void AddRead(String variable) throws IOException{
        //String variable = GetCurrentToken().description;
        String variableType = GetVariableType(variable);
        
        switch(variableType){
            case "Float":
                if(!IsArray(variable))
                    AddInstruction("READF");
                else
                    AddInstruction("READVF");
                AddVariable(variable);
            break;
            case "Int":
                if(!IsArray(variable))
                    AddInstruction("READI");
                else
                    AddInstruction("READVI");
                AddVariable(variable);
            break;
            case "Double":
                if(!IsArray(variable))
                    AddInstruction("READD");
                else
                    AddInstruction("READVD");
                AddVariable(variable);
            break;
            case "Char":
                if(!IsArray(variable))
                    AddInstruction("READC");
                else
                    AddInstruction("READVC");
                AddVariable(variable);
            break;
            case "String":
                if(!IsArray(variable))
                    AddInstruction("READS");
                else
                    AddInstruction("READVS");
                AddVariable(variable);
            break;
        }
    }

    public static boolean If() throws IOException {
        if (!Expect("if"))
            return false;
        if (!Expect("("))
            return false;
        if (!Condiciones())
            return false;
        if (!Expect(")"))
            return false;
        
        Tag tag1 = newTag();
        AddInstruction("JMPF");
        AddTag(tag1);
        
        if (!Expect("{"))
            return false;
        if (!Instrucciones())
            return false;
        if (!Expect("}"))
            return false;
        
        Tag tag2 = newTag();
        AddInstruction("JMP");
        AddTag(tag2);
        
        UpdateTagInKWA(tag1,true);
        
        if (CurrentTokenInFirst("Else"))
            if (!Else())
                return false;
        
        UpdateTagInKWA(tag2,true);
        
        return true;
    }
    public static boolean Else() throws IOException{
        //else �{� <Instrucciones> �}�
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
                //System.out.println("Error en Expect, " + messageError);
                NewJFrame.jTextArea3.append("Error, " + messageError);
                NewJFrame.jTextArea3.update(NewJFrame.jTextArea3.getGraphics());
                break;
            case "InstruccionInvalida":
                //System.out.println("Instruccion no identificada, " + messageError);
                NewJFrame.jTextArea3.append("Instrucción no identificada, " + messageError);
                NewJFrame.jTextArea3.update(NewJFrame.jTextArea3.getGraphics());
                break;
            default:
                //System.out.println("Error no identificado, " + messageError);
                NewJFrame.jTextArea3.append("Error no identificado, " + messageError);
                NewJFrame.jTextArea3.update(NewJFrame.jTextArea3.getGraphics());
        }
    }
    public static Token Tokenizer() throws IOException {
        Token tokenToReturn = new Token();
        tokenToReturn.description = ReadTokenFromFile();

        //System.out.println("Token: "+tokenToReturn.description);

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
        boolean error = false;
        Frame f = new Frame();
        FileDialog fd = new FileDialog(f, "Choose a file", FileDialog.LOAD);

        fd.setDirectory("C:\\");
        fd.setFile("*.KWBG");
        fd.isFocusOwner();
        fd.setVisible(true);
        
        String fileName = "";

        try {
            fileName = fd.getFile();
            if(fileName == null){
                error = true;
            }
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            NewJFrame.jTextArea3.append(e.getMessage());
            NewJFrame.jTextArea3.update(NewJFrame.jTextArea3.getGraphics());
        }
        
        f.dispose();
        if (!error){
            _filename = fileName.substring(0, fileName.length()-5);
            FileReader reader = new FileReader(fileName);
            AppendFile(reader);
        }
    }
    public static boolean InitializeFile() throws IOException{
        if (!WriteTempFile())
            return false;

        String fileName = _filename + ".KWBG";
        File f = new File (fileName);
        String fileDir = f.getAbsolutePath();
        fileDir = fileDir.substring(0, fileDir.length()-(fileName.length()));
        
        _bytesInFile = Files.readAllBytes(Paths.get(fileDir, fileName));
        FileReader reader = new FileReader(fileName);
        return true;
    }
    public static void AppendFile(FileReader fileName) throws IOException{
        BufferedReader buff = null;
        NewJFrame.jTextArea1.setText("");
        
        try {
            buff = new BufferedReader(fileName);
            String str;
            while ((str = buff.readLine()) != null) {
                NewJFrame.jTextArea1.append(str + "\n");
            }
        } catch (IOException e) {} 
        finally {
            try {buff.close();} 
            catch (Exception ex) { }
        }
    }
    public static boolean WriteTempFile() throws IOException{
        if (_filename == null){
            String inputValue = JOptionPane.showInputDialog("Save File As: ");
            if (inputValue == null){
                return false;
                //_filename = "TempFile";
            }
            _filename = inputValue;
        }
        FileWriter writer = new FileWriter(_filename + ".KWBG");
        BufferedWriter bw = new BufferedWriter(writer);
        NewJFrame.jTextArea1.write(bw);
        bw.close();
        return true;
    }
    public static boolean Condiciones() throws IOException {
        // <Condición> { <ANDOR> <Condiciones>} | (<Condiciones>)

        //<factorCondicion>[<ANDOR><Condiciones>]
        String AndOr="";
        if(CurrentTokenInFirst("FactorCondicion")){
            if(!FactorCondicion())
                return false;
            if(CurrentTokenInFirst("AndOr")){
                AndOr=GetCurrentToken().description;
                if(!AndOr())
                    return false;
                if(!Condiciones())
                    return false;
                if(AndOr.equals("AND"))
                    AddInstruction("MUL");
                else
                    AddInstruction("ADD");
            }
            return true;
        }
        return false;
            /*if(CurrentTokenInFirst("Condicion")){
            if(!Condicion())
               return false;
            if(CurrentTokenInFirst("AndOr")){
                if(!AndOr())
                    return false;
                if (!Condiciones())
                    return false;           
            }
        }
        else{
            if(CurrentToken("(")){
                if(!Expect("("))
                    return false;
                if(!Condiciones())
                    return false;
                if(!Expect(")"))
                    return false;
            }
            else
                return false;
        }  
        return true;*/
    }
    public static boolean FactorCondicion() throws IOException{
        if(CurrentTokenInFirst("Condicion")){
            if(!Condicion()){
                return false;
            }
            return true;
        }
        if(CurrentToken("(")){
            Expect("(");
            if(!Condiciones())
                return false;
            return Expect(")");
        }
        return false;
    }
    public static boolean AndOr() throws IOException {
        if (CurrentToken("AND"))
            return Expect("AND");
        if (CurrentToken("OR"))
            return Expect("OR");
        return false;
    }
    public static boolean For() throws IOException {
        // For ( [ <Asignacion> ] ; <Condiciones> ; [ <Asignacion> ] ) "{"
        // <Instrucciones> "}"
        if (!Expect("for"))
            return false;
        if (!Expect("("))
            return false;
        if (CurrentTokenInFirst("AsignacionFor"))
            if (!Asignacion(false))
                return false;
        if (!Expect(";"))
            return false;

        // Agregar TAG1
        Tag tag1 = newTag();
        UpdateTagInKWA(tag1,false);

        if (!Condiciones())
            return false;
        if (!Expect(";"))
            return false;

        Tag tag2 = newTag();
        AddInstruction("JMPF");
        AddTag(tag2);

        Tag tag3 = newTag();
        AddInstruction("JMP");
        AddTag(tag3);

        Tag tag4 = newTag();
        UpdateTagInKWA(tag4,false);

        if (CurrentTokenInFirst("AsignacionFor"))
            if (!Asignacion(false))
                return false;

        if (!Expect(")"))
            return false;
        if (!Expect("{"))
            return false;

        AddInstruction("JMP");
        AddTag(tag1);
        
        UpdateTagInKWA(tag3,true);

        if (!Instrucciones())
            return false;

        AddInstruction("JMP");
        AddTag(tag4);

        if (!Expect("}"))
            return false;

        UpdateTagInKWA(tag2,true);

        return true;
    }
    public static boolean Asignacion(boolean usesSemiColon) throws IOException{
        boolean EsConcatenacion = false;
        if(CurrentTokenInFirst("IncrementoDecremento")){
            if(!IncrementoDecremento())
                return false;
            if(usesSemiColon)
                return Expect(";");
            return true;
        }
        if(CurrentTokenInFirst("Variable")){
            Token tokenVariable = GetCurrentToken();
            _variableAsignacion = tokenVariable;
            _isAssigned = true;
            if(!Variable()){
                _isAssigned = false;
                return false;
            }
          
            _isAssigned = false;
            Token tokenOperator = GetCurrentToken();
            if(!OperadorAsignacion())
                return false;
            if(!tokenOperator.description.equals("="))
                EsConcatenacion = true;
            

            _stackValoresExpresion.clear();
            if(!Expresion())
                return false;
            while(!_stackValoresExpresion.isEmpty()){
            Token tokenExpresion = _stackValoresExpresion.pop();

            if(tokenExpresion.code == 43){
                if(!AddAsignment(tokenVariable, tokenOperator,tokenExpresion.info,EsConcatenacion))
                    return false;
            }
            else{
                if(!AddAsignment(tokenVariable, tokenOperator,GetVariableType(tokenExpresion.description),EsConcatenacion))
                    return false;
                }//else        
            }//while(!_stackValoresExpresion.isEmpty()){
            
           
            _variableAsignacion = null;
            
            if(usesSemiColon)
                return Expect(";");
            return true;        
        }
        return false;
    }
    private static boolean AddAsignment(Token tokenVariable, Token tokenOperator, String tipoDatoExpresion, boolean EsConcatenacion) throws IOException {
        String operatorAssembly = TranslateToAssembly(tokenOperator.description);
        String variableType = GetVariableType(tokenVariable.description);
        // EL TIPO DE DATO DE LA EXPRESION ES DIFERENTE DE LA VARIABLE
        if(tipoDatoExpresion.equals("DoubleFloat")){
            if(!variableType.equals("Double") && !variableType.equals("Float")){
                return false;
            }
        }
        //else if (!variableType.equals(tipoDatoExpresion) && (!variableType.equals("Double") && !tipoDatoExpresion.equals("Int"))){
        else if(!variableType.equals(tipoDatoExpresion)){
            if((!variableType.equals("Double") && !variableType.equals("Float")) || !tipoDatoExpresion.equals("Int"))
                return false;
        }
        
        //SI LA PILA EXPRESION AUN TIENE DATOS NO HACE NADA, SIGUE VALIDANDO LOS DATOS DE LA EXPRESION
        if(!_stackValoresExpresion.isEmpty())
            return true;
        
        AddToKWA(_arrayTmp);
        _arrayTmp = new byte[0];
        //String fixPopInstruction="";
        //String fixPushInstruction = "";
        switch(variableType){
            case "Int":
                if(EsConcatenacion){
                    if(!IsArray(tokenVariable.description)){
                        if(EsConcatenacion)
                            fixAssignment(tokenOperator,tokenVariable,"PUSHI","I");
                    }
                    else{
                        if(EsConcatenacion)
                            fixAssignment(tokenOperator,tokenVariable,"PUSHVI","I");
                    }
                }
                if(!tokenOperator.description.equals("="))
                    AddInstruction(operatorAssembly);
                if(!IsArray(tokenVariable.description)){
                    AddInstruction("POPI");
                }
                else{
                    AddInstruction("POPVI");
                }
                AddVariable(tokenVariable.description);    
            break;
            case "Double":
                if(EsConcatenacion){
                    if(!IsArray(tokenVariable.description)){
                        if(EsConcatenacion)
                            fixAssignment(tokenOperator,tokenVariable,"PUSHD","D");
                    }
                    else{
                        if(EsConcatenacion)
                            fixAssignment(tokenOperator,tokenVariable,"PUSHVD","D");
                    }
                }
                if(!tokenOperator.description.equals("="))
                    AddInstruction(operatorAssembly);
                if(!IsArray(tokenVariable.description)){
                    AddInstruction("POPD");
                }
                else{
                    AddInstruction("POPVD");
                }
                AddVariable(tokenVariable.description);
                
                break;
            case "Float":
                if(!IsArray(tokenVariable.description)){
                    if(EsConcatenacion)
                        fixAssignment(tokenOperator,tokenVariable,"PUSHF","F");
                }
                else{
                    if(EsConcatenacion)
                        fixAssignment(tokenOperator,tokenVariable,"PUSHVF","F");
                }
                if(!tokenOperator.description.equals("="))
                    AddInstruction(operatorAssembly);
                if(!IsArray(tokenVariable.description)){
                    AddInstruction("POPF");
                }
                else{
                    AddInstruction("POPVF");
                }
                AddVariable(tokenVariable.description);
                
                break;
            case "String":
               if(!IsArray(tokenVariable.description)){
                    if(EsConcatenacion)
                        fixAssignment(tokenOperator,tokenVariable,"PUSHS","S");
                }
                else{
                    if(EsConcatenacion)
                        fixAssignment(tokenOperator,tokenVariable,"PUSHVS","S");
                }
                if(operatorAssembly.equals("ADD"))
                {
                    AddInstruction(operatorAssembly);
                    if(!IsArray(tokenVariable.description))
                        AddInstruction("POPS");
                    else
                        AddInstruction("POPVS");
                    AddVariable(tokenVariable.description);
                }
                else if(tokenOperator.description.equals("=")){
                    if(!IsArray(tokenVariable.description))
                        AddInstruction("POPS");
                    else
                        AddInstruction("POPVS");
                    AddVariable(tokenVariable.description);
                }       
                else
                    return false;
                break;
            case "Char":
                if(!tokenOperator.description.equals("="))
                    return false;
                if(!IsArray(tokenVariable.description))
                    AddInstruction("POPC");
                else
                    AddInstruction("POPVC");
                AddVariable(tokenVariable.description);
                break;      
        }
        
        return true;
}
    public static void fixAssignment(Token tokenOperator,Token tokenVariable, String PushInstruction, String Type) throws IOException{
        
        AddInstruction("POP"+Type);
        AddVariableDir(GetVariableDir("TMP"+Type,true));
        AddInstruction(PushInstruction);
        AddVariable(tokenVariable.description);
        AddInstruction("PUSH"+Type);
        AddVariableDir(GetVariableDir("TMP"+Type,true));
    }
    public static boolean IncrementoDecremento() throws IOException{
        Token tokenVariable = GetCurrentToken();
        AddValue(tokenVariable);
        String operator = "";
        if(!Variable())
            return false;
        if(CurrentToken("++")){
            if(!Expect("++"))
                return false;
            operator = "ADD";
        }
        else if(CurrentToken("--")){        
            if(!Expect("--"))
                return false;
            operator = "SUB";
        }
        else 
            return false;
        
        switch(GetVariableType(tokenVariable.description)){
            case "Int":
                AddInstruction("PUSHKI");
                AddInteger(1);
                AddInstruction(operator);
                if(!IsArray(tokenVariable.description))
                    AddInstruction("POPI");
                else
                    AddInstruction("POPVI");
                AddVariable(tokenVariable.description);
                break;
            case "DoubleFloat":
                AddInstruction("PUSHKD");
                AddDouble(1);
                AddInstruction(operator);
                if(!IsArray(tokenVariable.description))
                    AddInstruction("POPD");
                else
                    AddInstruction("POPVD");
                AddVariable(tokenVariable.description);
                break;
            default:
                return false;
        }
        return true;
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
    
    public static boolean Expresion() throws IOException{
        //<Termino>|<Termino><OperadorSUma><Expresion>  //
        if(Termino()){
            if(CurrentToken("+") || CurrentToken("-")){
                Token tokenOperator = GetCurrentToken();
                if(_stackIsCondition.isEmpty()){
                _stackOperadores.push(tokenOperator);
                }
                String assemblyOperator = TranslateToAssembly(tokenOperator.description);

                if(CurrentToken("+")){
                    Expect("+");           
                }
                else{
                    Expect("-");
                }

                if(!Expresion())
                    return false;

                while(!_stackOperadores.isEmpty())
                    AddInstruction(TranslateToAssembly(_stackOperadores.pop().description));
                return true;
            }
            return true;
        }
        return false;
    }
    public static boolean Termino() throws IOException{
        if(Factor()){
            if(CurrentToken("*") || CurrentToken("/") || CurrentToken("%")){
                Token tokenOperator = GetCurrentToken();
                if(_stackIsCondition.isEmpty()){
                _stackOperadores.push(tokenOperator);
                }
                String assemblyOperator = TranslateToAssembly(tokenOperator.description);
                if(CurrentToken("*"))
                    Expect("*");
                else{
                    if(CurrentToken("/"))
                        Expect("/");
                    else
                        Expect("%");
                }
                if(!Termino())
                    return false;
                    //AddInstruction(assemblyOperator);
                if(_stackIsCondition.isEmpty())
                    if(!_stackOperadores.isEmpty())
                            AddInstruction(TranslateToAssembly(_stackOperadores.pop().description));            
                return true;
            }
            if(_stackIsCondition.isEmpty())
                if(!_stackOperadores.isEmpty())
                    if(!_IndiceVector)
                        if(!_Parentesis){
                        AddInstruction(TranslateToAssembly(_stackOperadores.pop().description));
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
            _Parentesis = true;
            if(!Expresion())
                return false;
            _Parentesis = false;
            return Expect(")");     
        }
        return false;
    }
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
        
        Token tokenValor = GetCurrentToken();
         if(tokenValor.code == 43 && tokenValor.info == "Char"){
             String regex = "[\"\'a-z A-Z 0-9]+";
             if(tokenValor.description.matches(regex) && tokenValor.description.length() > 3){
                 return false;
             }
         }
        _stackValoresExpresion.push(tokenValor);
        if(!_stackTokensInIndex.isEmpty())
            _stackTokensInIndex.push(_stackTokensInIndex.pop()+1);
        
        if(CurrentTokenInFirst("Variable")){
            if(!Variable())
               return false;       
            AddValue(tokenValor);
            if(_stackIsCondition.isEmpty()){
               if(_IndiceVector)
                   if(!_stackOperadores.isEmpty())
                       AddInstruction(TranslateToAssembly(_stackOperadores.pop().description));
                
            //if(!_stackOperadores.isEmpty())
         //   AddInstruction(TranslateToAssembly(_stackOperadores.pop().description))   
            }
            return true;
         }
         
         if(!Expect(43)){
             if(!_stackTokensInIndex.isEmpty())
             _stackTokensInIndex.push(_stackTokensInIndex.pop()-1);
             _stackValoresExpresion.pop();
            return false;
         }
         
         AddValue(tokenValor);
         if(_stackIsCondition.isEmpty()){
         // if(!_stackOperadores.isEmpty())
          //      AddInstruction(TranslateToAssembly(_stackOperadores.pop().description));
         }
         return true;
    }
    public static boolean While() throws IOException {
        if (!Expect("while"))
            return false;
        if (!Expect("("))
            return false;
        
        Tag tag1 = newTag();
        UpdateTagInKWA(tag1,false);
        
        if (!Condiciones())
            return false;
        
        if (!Expect(")"))
            return false;
        if (!Expect("{"))
            return false;
        
        Tag tag2 = newTag();
        AddInstruction("JMPF");
        AddTag(tag2);
        
        if (!Instrucciones())
            return false;
        
        AddInstruction("JMP");
        AddTag(tag1);
        
        if (!Expect("}"))
            return false;
        
        UpdateTagInKWA(tag2,true);
        
        return true;
    }
    
    public static String ReadTokenFromFile() throws IOException {

        // 9 - Tab
        // 10 - Salto de linea
        // 32 - Espacio
        // 33 - !
        // 37 - %
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
        boolean vectorIndexFound = false;
        boolean justClosedVector = false;
        boolean comillaSimpleFound = false;

        while (!isComplete) {

            boolean increaseByte = false;
            if (!commentFound) {
                switch (_bytesInFile[lastByteRead]) {

                // Separadores de palabra que no se convierten a token
                case 9:
                case 10:
                	lineReadNumber++;
                case 13:
                case 32: //Vacio
                    increaseByte = true;
                    if (!quotationFound) {
                    	if(comillaSimpleFound){
                    		tokenWord += (char) _bytesInFile[lastByteRead];
                    	} else {
                    		if(vectorIndexFound){
	                            tokenWord += (char) _bytesInFile[lastByteRead];
	                        } else {
	                            if (!tokenWord.equals("")) {
	                                isComplete = true;
	                            }
	                        }
	                        
                    	}
                    	
                        
                    } else {
                    	
                        tokenWord += (char) _bytesInFile[lastByteRead];
                    }

                    lastTokenReadOperator = false;
                    lastTokenReadSubstractOperator = false;
                    break;

                // Comentarios
                case 92:
                    increaseByte = true;
                    if (!quotationFound) {
                        if(vectorIndexFound){
                            tokenWord += (char) _bytesInFile[lastByteRead];
                        } else {
                            commentFound = true;
                        }
                        
                    }
                    else{
                    	
                        tokenWord += (char) _bytesInFile[lastByteRead];
                    }
                    
                    lastTokenReadOperator = false;
                    lastTokenReadSubstractOperator = false;
                    
                    break;
                    
                //Operadores logicos aritmeticos que pueden estar juntos
                case 33: //!
                case 37: //%
                case 42: //*
                case 43: //+
                case 45: // -
                case 47: // /
                case 60: // <
                case 61: // =
                case 62: // >
                    if(!quotationFound){
                    	
                    	if(comillaSimpleFound){
                    		tokenWord += (char) _bytesInFile[lastByteRead];
	                        increaseByte = true;
                    	}else {
                    		//System.out.println("No quotation, comilla simple not found");
                    		if(vectorIndexFound){
	                            tokenWord += (char) _bytesInFile[lastByteRead];
	                            increaseByte=true;
	                        } else {
	                            if(!lastTokenReadOperator){
	                                if (tokenWord.length() != 0) {
	                                    isComplete = true;
	                                }
	                            } else {

	                
	                            	
	                                tokenWord += (char) _bytesInFile[lastByteRead];
	                                increaseByte = true;
	                                
	                                if (_bytesInFile[lastByteRead-1] >= 48 && _bytesInFile[lastByteRead-1] <=57) {
	                                	//Ultimo token en leer es numero
	                                	
	                                	if((_bytesInFile[lastByteRead] == 33 || _bytesInFile[lastByteRead] == 62 || _bytesInFile[lastByteRead]== 60 || _bytesInFile[lastByteRead]==61)){
                     	            		//System.out.println("Operador Logico");
	                                		
	                                		if((tokenWord.equals(">")||tokenWord.equals("<") || tokenWord.equals("=") || tokenWord.equals("!")) && _bytesInFile[lastByteRead] == 61){
	                     	            		//System.out.println("Operador Logico");
	                     	            		
	                                			if(_bytesInFile[lastByteRead+1]!=61){
		                     	            		isComplete = true;
	                                			} else {
	                                				tokenWord += (char) _bytesInFile[lastByteRead+1];
		                     	            		lastByteRead++;
	                                			}
	                                			
	                     	            		
	                     	            	}
	                                		
                     	            	}
                     	            } else {
                     	            	/*
                     	            	if((tokenWord.equals(">")||tokenWord.equals("<") || tokenWord.equals("=")) && _bytesInFile[lastByteRead] == 61){
                     	            		//System.out.println("Operador Logico");
                     	            		tokenWord += (char) _bytesInFile[lastByteRead];
                     	            		lastByteRead++;
                     	            		isComplete = true;
                     	            	}*/
                     	            }
	                            }
	                            
	                            lastTokenReadOperator = true;
	                        }
                    	}
                        
                    } else {
                    	
                    	if(!comillaSimpleFound){
                    		tokenWord += (char) _bytesInFile[lastByteRead];
	                        increaseByte = true;
                    	}
                        
                    }
                    
                    lastTokenReadSubstractOperator = false;
                    
                    if (_bytesInFile[lastByteRead] == 45) {
                        lastTokenReadSubstractOperator = true;
                    }
                    
                    
                    
                    break;
                    
                // Separadores de palabra que se convierten a token
                case 40:
                case 41:
                case 44:
                case 59:
                case 91:
                case 93:
                case 123:
                case 125:
                    if (!quotationFound) {
                    	
                    	if(comillaSimpleFound){
                    		if( _bytesInFile[lastByteRead] == 59){
	                    		if(!tokenWord.equals("'")){
		                    		isComplete = true;
	                    		} else {
	                    			tokenWord += (char) _bytesInFile[lastByteRead];
			                        increaseByte = true;
	                    		}
	                    	} else {
	                    		tokenWord += (char) _bytesInFile[lastByteRead];
		                        increaseByte = true;
	                    	}
                    	} else {
                    		if(vectorIndexFound){
	                            tokenWord += (char) _bytesInFile[lastByteRead];
	                        } else {
	                        	
	                            if (tokenWord.length() == 0) {
	                                tokenWord += (char) _bytesInFile[lastByteRead];
	                                increaseByte = true;
	                            }
	                            
	                            isComplete = true;
	                            
	                        }
                    	}
                        
                    } else {

                    	//System.out.println("Aqui: "+_bytesInFile[lastByteRead]);
                    	if( _bytesInFile[lastByteRead] == 59){
                    		if(!tokenWord.equals("")){
	                    		//isComplete = true;
                    			tokenWord += (char) _bytesInFile[lastByteRead];
		                        increaseByte = true;
                    		}
                    	} else {
                    		tokenWord += (char) _bytesInFile[lastByteRead];
	                        increaseByte = true;
                    	}
	                    
                    	
                    }
                    
                    lastTokenReadOperator = false;
                    lastTokenReadSubstractOperator = false;
                    
                    break;
                    
                    // No separadores de palabra
                default:

                    boolean thisNumber = false;
                    
                    if (_bytesInFile[lastByteRead] == 34) {
                    	if(!comillaSimpleFound){
                    		quotationFound = !quotationFound;
                    	}
                    }
                    
                    if (_bytesInFile[lastByteRead] == 39) {
                    	if(!quotationFound){
	                    	comillaSimpleFound = !comillaSimpleFound;
                    	}
                    }
                    
                    //if (_bytesInFile[lastByteRead] == 91) {
                    //  vectorIndexFound = true;
                    //}
                    
                    //if (_bytesInFile[lastByteRead] == 93) {
                    //  vectorIndexFound = false;
                    //  justClosedVector = true;
                    //}
                    
                    if (_bytesInFile[lastByteRead] >= 48 && _bytesInFile[lastByteRead] <=57) {
                        //Es 
                        thisNumber = true;

                    } 
                    if(!justClosedVector){

                        if(lastTokenReadOperator){

                            //Si este es numero y el pasado fue menos
                            if(thisNumber){
                                if(lastTokenReadSubstractOperator){
                                    if(tokenWord.length()==1){
                                    	if(_bytesInFile[lastByteRead-2]!=61){
                                    		if(tokenWord.equals("-")){
                                    			
                                    			if(_bytesInFile[lastByteRead-2]== 45){
                                    				increaseByte = true;
		    	                                    tokenWord += (char) _bytesInFile[lastByteRead];
                                    			} else {
                                    				isComplete=true;
                                    			}
                                    			
                                    		} else {
                                    			isComplete = true;
                                    		}
                                    	}
                                        
                                    } else {
                                    	//System.out.println(tokenWord);
                                    	if(!tokenWord.equals("")){
                                    		tokenWord = tokenWord.substring(0, tokenWord.length() - 1);
	                                        lastByteRead--;
                                    	}
                                    }
                                    
                                } else{
                                	if(!tokenWord.equals("")){
        	                    		isComplete = true;
        	                    	}
                                  
                                }
                            } else {
                            	if(!tokenWord.equals("")){
    	                    		isComplete = true;
    	                    	}
                            }
                            
                        } else {
                            increaseByte = true;
                            tokenWord += (char) _bytesInFile[lastByteRead];
                        }
                        
                    } else {
                        //Se acaba indice de vector corchetes
                        isComplete = true;
                        increaseByte = true;
                        tokenWord += (char) _bytesInFile[lastByteRead];
                    }
                    
                    justClosedVector = false;
                    
                    
                    lastTokenReadOperator = false;
                    lastTokenReadSubstractOperator = false;
                    
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
        
     
        //System.out.println(tokenWord);
        
        /*
        if(tokenWord.charAt(tokenWord.length()-1) == '-'){
        	pastTokenHasSubstract = true;
        } else {
        	pastTokenHasSubstract = false;
        } */
        
        return tokenWord;
    }

    public static boolean isOperator(char character){
        if(character == 33 || character == 37 || character == 42 || character == 43 || character == 45 || character == 47
                || character == 60 || character == 61 || character == 62){
            return true;
        }
        return false;
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
            case "*=":
                return 46;
            case "/=":
                return 47;
            case "%=":
                return 48;
            default:
                if (isNumber(token) || token.charAt(0) == '"' || ("" + token.charAt(0)).equals("'"))
                    return 43;
                if (isVariableInTable(token))
                    return 44;
                return 45;
            }
    }
    public static String TranslateToAssembly(String operator){
        switch(operator){
            case "+":
                return "ADD";
            case "-":
                return "SUB";
            case "*":
                return "MUL";
            case "/":
                return "DIV";
            case "%":
                return "MOD";
            case "==":
                return "CMPEQ";
            case "!=":
                return "CMPNE";
            case "<":
                return "CMPLT";
            case "<=":
                return "CMPLE";
            case ">":
                return "CMPGT";
            case ">=":
                return "CMPGE";
            case "+=":
                return "ADD";
            case "-=":
                return "SUB";
            case "*=":
                return "MUL";
            case "/=":
                return "DIV";
            case "%=":
                return "MOD";
        }
        return "";
    }
    public static boolean isNumber(String tokenWord) {
        for (int i = 0; i < tokenWord.length(); i++) {
            if(i==0 && tokenWord.charAt(i)==45){    
            } else {
                if ((tokenWord.charAt(i) - 48 < 0 || tokenWord.charAt(i) - 48 > 9) && tokenWord.charAt(i) != 46) {
                    return false;
                }
            }
        }
        return true;
    }
    public static boolean isVariableInTable(String variableName) {
        for (int i = 0; i < _variablesTable.length; i++) {
            if ((_variablesTable[i].name).equals(variableName)) {
                return true;
            }
        }
        return false;
    }
    public void AddLength(int length) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            byte[] instructionArray = new byte[1];
            instructionArray[0] = (byte)length;
            if(_isArrayAssignment)
                AddToArrayTmp(instructionArray);
        else
            AddToKWA(instructionArray);
            _SC += 1;
        }
    }
    public static Tag newTag(){
        Tag returnTag = new Tag("ETQ"+_tagNumber);
        _tagNumber++;
        _tagStack.push(returnTag);
        return returnTag;
    }   
    public static void AddInstruction(int instruction) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            byte[] instructionArray = new byte[1];
            instructionArray[0] = (byte)instruction;
            if(_isArrayAssignment)
                AddToArrayTmp(instructionArray);
            else
                AddToKWA(instructionArray);
                _SC += 1;
        }
    }
    public static void AddInstruction(String instruction) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            byte[] instructionArray = new byte[1];
            instructionArray[0] = (byte)GetInstructionCode(instruction);
            if(_isArrayAssignment)
                AddToArrayTmp(instructionArray);
        else
            AddToKWA(instructionArray);
            _SC += 1;
        }
    }    
    public static int GetInstructionCode(String instruction){
        switch(instruction){
            case "HALT":
                return 0;
            case "READI":
                return 1;
            case "READD":
                return 2;
            case "READF":
                return 3;
            case "READC":
                return 4;
            case "READS":
                return 5;
            case "READVI":
                return 6;
            case "READVD":
                return 7;
            case "READVF":
                return 8;
            case "READVC":
                return 9;
            case "READVS":
                return 10;
            case "WRTI":
                return 11;
            case "WRTD":
                return 12;
            case "WRTF":
                return 13;
            case "WRTC":
                return 14;
            case "WRTS":
                return 15;
            case "WRTM":
                return 16;
            case "WRTLN":
                return 17;
            case "WRTVI":
                return 18;
            case "WRTVD":
                return 19;
            case "WRTVC":
                return 20;
            case "WRTVF":
                return 21;
            case "WRTVS":
                return 22;
            case "SETINDEX":
                return 23;
            case "SETINDEXK":
                return 24;
            case "POPINDEX":
                return 25;
            case "PUSHI":
                return 26;
            case "PUSHD":
                return 27;
            case "PUSHC":
                return 28;
            case "PUSHF":
                return 29;
            case "PUSHS":
                return 30;
            case "PUSHKI":
                return 31;
            case "PUSHKF":
                return 32;
            case "PUSHKD":
                return 33;
            case "PUSHKC":
                return 34;
            case "PUSHKS":
                return 35;
            case "PUSHVI":
                return 36;
            case "PUSHVF":
                return 37;
            case "PUSHVD":
                return 38;
            case "PUSHVC":
                return 39;
            case "PUSHVS":
                return 40;
            case "POPI":
                return 41;
            case "POPD":
                return 42;
            case "POPC":
                return 43;
            case "POPF":
                return 44;
            case "POPS":
                return 45;
            case "POPVI":
                return 46;
            case "POPVD":
                return 47;
            case "POPVC":
                return 48;
            case "POPVF":
                return 49;
            case "POPVS":
                return 50;
            case "CMPEQ":
                return 51;
            case "CMPNE":
                return 52;
            case "CMPLT":
                return 53;
            case "CMPLE":
                return 54;
            case "CMPGT":
                return 55;
            case "CMPGE":
                return 56;
            case "JMP":
                return 57;
            case "JMPT":
                return 58;
            case "JMPF":
                return 59;
            case "ADD":
                return 60;
            case "SUB":
                return 61;
            case "MUL":
                return 62;
            case "DIV":
                return 63;
            case "MOD":
                return 64;
            default:
                return -1;
        }
    }
    public static void AddTag(Tag tag) throws IOException {
        if (_stackIsCondition.isEmpty() && !_isDeclaration) {
            byte[] variableBytes = new byte[2];
            tag.referencedDir = _SC;
            variableBytes[1] = (byte) (tag.dir & 0xFF);
            variableBytes[0] = (byte) ((tag.dir >> 8) & 0xFF);
            if(_isArrayAssignment)
                AddToArrayTmp(variableBytes);
        else
          AddToKWA(variableBytes);
            _SC += 2;
        }
    }
    public static void UpdateTagInKWA(Tag tagToUpdate, boolean isTagAlreadyInCode) {
        if (_stackIsCondition.isEmpty()) {
            tagToUpdate.dir = _SC;
            
            if(isTagAlreadyInCode){
                byte[] tagBytes = new byte[2];
                tagBytes[1] = (byte) (tagToUpdate.dir & 0xFF);
                tagBytes[0] = (byte) ((tagToUpdate.dir >> 8) & 0xFF);
                _KWA[tagToUpdate.referencedDir] = tagBytes[0];
                _KWA[tagToUpdate.referencedDir + 1] = tagBytes[1];
            }
        }
    }
    public static void AddVariable(String variable) throws IOException{     
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            int variableDir = GetVariableDir(variable);
            byte[] variableBytes=new byte[2];
            variableBytes[1]=(byte)(variableDir & 0xFF);
            variableBytes[0]=(byte)((variableDir>>8) & 0xFF);
            if(_isArrayAssignment)
                AddToArrayTmp(variableBytes);
        else
            AddToKWA(variableBytes);
            _SC += 2;
        }
    }
    public static void AddVariableDir(int variableDir) throws IOException{     
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            byte[] variableBytes=new byte[2];
            variableBytes[1]=(byte)(variableDir & 0xFF);
            variableBytes[0]=(byte)((variableDir>>8) & 0xFF);
            if(_isArrayAssignment)
                AddToArrayTmp(variableBytes);
        else
            AddToKWA(variableBytes);
            _SC += 2;
           //System.out.println(variableDir);
        }
    }
    private static void AddValue(Token tokenToAdd) throws IOException {
        // ES CONSTANTE
        String info = "";
        
        if(tokenToAdd.code == 43){
            if(_variableAsignacion!=null && !_IndiceVector)
                info = GetVariableType(_variableAsignacion.description);
            else
                info = tokenToAdd.info;
      
            switch(info)
            {
                case "Int":
                    AddInstruction("PUSHKI");
                    
                    if(tokenToAdd.info.equals("Int") || tokenToAdd.info.equals("DoubleFloat") || tokenToAdd.info.equals("Double")|| tokenToAdd.info.equals("Float")){
                        double doubleTemporal = Double.parseDouble(tokenToAdd.description);
                        int integerReal = (int) doubleTemporal;
                        AddInteger(integerReal);
                    }
                    
                    break;
                case "DoubleFloat":
                    if(tokenToAdd.info.equals("Int") || tokenToAdd.info.equals("DoubleFloat") || tokenToAdd.info.equals("Double") || tokenToAdd.info.equals("Float") ){

                        AddInstruction("PUSHKD");
                        AddDouble(Double.parseDouble(tokenToAdd.description));
                    }
                    break;
                case "Double":
                     if(tokenToAdd.info.equals("Int") || tokenToAdd.info.equals("DoubleFloat") || tokenToAdd.info.equals("Double") || tokenToAdd.info.equals("Float")){
                         AddInstruction("PUSHKD");
                         AddDouble(Double.parseDouble(tokenToAdd.description));
                     }
                    break;
                    
                case "Float":
                     if(tokenToAdd.info.equals("Int") || tokenToAdd.info.equals("DoubleFloat") || tokenToAdd.info.equals("Double") || tokenToAdd.info.equals("Float")){
                         AddInstruction("PUSHKF");
                         float tempFloat = Float.parseFloat(tokenToAdd.description);
                         AddFloat(tempFloat);
                     }
                    break;
                    
                case "String":
                    AddInstruction("PUSHKS");
                    AddString(tokenToAdd.description);
                    break;
                case "Char":
                    AddInstruction("PUSHKC");
                    String regexNumber = "[0-9]+";
                    if(tokenToAdd.description.matches(regexNumber)){
                        Pattern p = Pattern.compile("[0-9]+");
                        Matcher m = p.matcher(tokenToAdd.description);
                        if (m.find()) {
                           AddChar(Integer.parseInt(m.group(0)));
                        }
                    }
                    else 
                        AddChar(tokenToAdd.description.charAt(1));
                    break;
            }
        }
        // ES VARIABLE
        else{
            
            switch(GetVariableType(tokenToAdd.description)){
                case "Int":
                    if(!IsArray(tokenToAdd.description))
                        AddInstruction("PUSHI");
                    else
                        AddInstruction("PUSHVI");
                    break;
                case "Double":
                    if(!IsArray(tokenToAdd.description))
                        AddInstruction("PUSHD");
                    else
                        AddInstruction("PUSHVD");                            
                    break;
                case "Float":
                    if(!IsArray(tokenToAdd.description))
                        AddInstruction("PUSHF");
                    else
                        AddInstruction("PUSHVF");
                    break;
                case "String":
                    if(!IsArray(tokenToAdd.description))
                        AddInstruction("PUSHS");
                    else
                        AddInstruction("PUSHVS");
                    break;
                case "Char":
                    if(!IsArray(tokenToAdd.description))
                        AddInstruction("PUSHC");
                    else
                        AddInstruction("PUSHVC");
                    break;
            }
            AddVariable(tokenToAdd.description);
        }
    }
    
    public static int GetVariableDir(String variable,boolean isTmp){
        int dir=0;
        for(int i=0; i<_variablesTable.length ; i++){
            switch(_variablesTable[i].type){
                case "Int":
                    dir+=4;
                    break;
                case "Float":
                    dir+=4;
                    break;
                case "Double":
                    dir+=8;
                    break;
                case "String":
                    dir+=255;
                    break;
                case "Char":
                    dir+=1;
                    break;
            }
        }
        
        switch(variable){
        case "TMPC":
            return dir;
        case "TMPI":
            return dir+1;
        case "TMPF":
            return dir+1+4;
        case "TMPD":
            return dir+1+4+4;
        case "TMPS":
            return dir+1+4+4+8;
        }
        return -1;
    }
    
    public static int GetVariableDir(String variable){
        int dir=0;
        for(int i=0; i<_variablesTable.length ; i++){
            if(_variablesTable[i].name.equals(variable)){
                    return dir;
            }
            switch(_variablesTable[i].type){
                case "Int":
                    dir+=4;
                    break;
                case "Float":
                    dir+=4;
                    break;
                case "Double":
                    dir+=8;
                    break;
                case "String":
                    dir+=255;
                    break;
                case "Char":
                    dir+=1;
                    break;
            }
        }
        return -1;
    }
    public static String GetVariableType(String variable){
        for(int i=0; i<_variablesTable.length ; i++){
            if(_variablesTable[i].name.equals(variable)){
                return _variablesTable[i].type;
            }
        }
        return "";
    }
    public static void AddInteger(int variable) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            if(_isArrayAssignment)
                AddToArrayTmp(ByteBuffer.allocate(4).putInt(variable).array());
            else
                AddToKWA(ByteBuffer.allocate(4).putInt(variable).array());
                _SC += 4;
        }
    }
    public static void AddDouble (double variable) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            if(_isArrayAssignment)
                AddToArrayTmp(ByteBuffer.allocate(8).putDouble(variable).array());
        else
            AddToKWA(ByteBuffer.allocate(8).putDouble(variable).array());
            _SC += 8;
        }
    }
    public static void AddFloat (float variable) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
          if(_isArrayAssignment)
            AddToArrayTmp(ByteBuffer.allocate(4).putFloat(variable).array());
        else
            AddToKWA(ByteBuffer.allocate(4).putFloat(variable).array());
            _SC += 4;
        }
    }
    public static void AddChar (char variable) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            byte[] instructionArray = new byte[1];
            instructionArray[0] = (byte)variable;
            if(_isArrayAssignment)
                AddToArrayTmp(instructionArray);
            else
                AddToKWA(instructionArray);
            _SC += 1;
        }
    }   
    public static void AddChar (int variable) throws IOException{
        if(_stackIsCondition.isEmpty() && !_isDeclaration){
            byte[] instructionArray = new byte[1];
            instructionArray[0] = (byte)variable;
            if(_isArrayAssignment)
                AddToArrayTmp(instructionArray);
            else
                AddToKWA(instructionArray);
            _SC += 1;
        }
    } 
    public static void AddString(String variable) throws IOException {
        if (_stackIsCondition.isEmpty() && !_isDeclaration) {
            int size = variable.length() - 2;
            byte[] instructionArray = new byte[1];
            instructionArray[0] = (byte) size;
            if(_isArrayAssignment)
                AddToArrayTmp(instructionArray);
        else
            AddToKWA(instructionArray);
            _SC += 1;

            for (int i = 1; i < variable.length() - 1; i++) {
                instructionArray[0] = (byte) variable.charAt(i);
                if(_isArrayAssignment)
                    AddToArrayTmp(instructionArray);
            else
              AddToKWA(instructionArray);
                _SC += 1;
            }
        }
    }
    public static void AddToArrayTmp(byte[] bytesToAdd){
        byte[] newKWA = new byte[_arrayTmp.length + bytesToAdd.length];

        for(int i=0; i<_arrayTmp.length; i++)
                newKWA[i] = _arrayTmp[i];

        for(int i = _arrayTmp.length ; i<newKWA.length ; i++)
                newKWA[i] = bytesToAdd[i-_arrayTmp.length];
        _arrayTmp = newKWA;
    }
    public static void AddToKWA(byte[] bytesToAdd){
        byte[] newKWA = new byte[_KWA.length + bytesToAdd.length];

        for(int i=0; i<_KWA.length; i++)
                newKWA[i] = _KWA[i];

        for(int i = _KWA.length ; i<newKWA.length ; i++)
                newKWA[i] = bytesToAdd[i-_KWA.length];
        _KWA = newKWA;
}
    public static void AddToVariableTable(String name, String type) throws IOException{
        if(_stackIsCondition.isEmpty()){
            int newLength;
            Variable arrayTemp[] = null;
            newLength = _variablesTable.length + 1;
            arrayTemp = java.util.Arrays.copyOf(_variablesTable, newLength);
            CreateNewVariableInTable(arrayTemp, newLength-1, name, type);
            _variablesTable = arrayTemp;
        }
    }
    public static void CreateNewVariableInTable(Variable arrayTemp[], int position, String name, String type) throws IOException{
        Variable newObject = new Variable(name,"",type);
        arrayTemp[position] = newObject;
    }   
    public static int GetVariableSize(){
        int size=0;
        for(int i=0; i<_variablesTable.length ; i++){
            switch(_variablesTable[i].type){
                case "Int":
                    size+=4;
                break;
                
                case "Float":
                    size+=4;
                break;
                
                case "Double":
                    size+=8;
                break;
                
                case "String":
                    size+=255;
                break;
                
                case "Char":
                    size+=1;
                break;
            }
        }
        return size;
    }
    public static void getHeader(){
        byte[] KWAWithHeader=new byte[_KWA.length+14]; 
        KWAWithHeader[0]=(byte)'(';
        KWAWithHeader[1]=(byte)'C';
        KWAWithHeader[2]=(byte)')';
        KWAWithHeader[3]=(byte)'K';
        KWAWithHeader[4]=(byte)'W';
        KWAWithHeader[5]=(byte)'A';
        KWAWithHeader[6]=(byte)'2';
        KWAWithHeader[7]=(byte)'0';
        KWAWithHeader[8]=(byte)'1';
        KWAWithHeader[9]=(byte)'5';
        KWAWithHeader[10]=(byte)((_SC>>8) & 0xFF);
        KWAWithHeader[11]=(byte)(_SC & 0xFF);;
        int variableSize = GetVariableSize();
        KWAWithHeader[12]=(byte)((variableSize>>8) & 0xFF);
        KWAWithHeader[13]=(byte)(variableSize & 0xFF);
        
        for(int i=0;i<_KWA.length;i++)
            KWAWithHeader[i+14]=_KWA[i];
        _KWA=KWAWithHeader;
    }
    public static void WriteAssemblyFile() throws NumberFormatException, IOException{
        BufferedOutputStream bufferedOut = new BufferedOutputStream(new FileOutputStream(_filename+".KWA")); 
        getHeader();
        bufferedOut.write(_KWA);
        bufferedOut.close();
    }
    public static void cleanLastBytesInFile(){
        int nArrayLength = _bytesInFile.length;
        int newLength = nArrayLength;
        byte[] _copyBytesInFile = new byte[nArrayLength];
        
        boolean bCommentFound = false;
        
        //Eliminar de comentarios
        int j = 0;
        for(int i = 0; i < nArrayLength; i++){
            if(_bytesInFile[i] == 92){
                bCommentFound = true;
            }
            if(_bytesInFile[i] == 13){
                bCommentFound = false;
            }
            if(!bCommentFound){
                _copyBytesInFile[j] = _bytesInFile[i];
                j++;
            }
        }
        
        _bytesInFile = _copyBytesInFile;
        
        //Eliminar ultimos saltos de linea y enter
        for(int i = nArrayLength-1; i > 0; i--){
            if(_bytesInFile[i] == 10 || _bytesInFile[i] == 13 || _bytesInFile[i] == 32 || _bytesInFile[i] == 9 || _bytesInFile[i] == 0){  
            } else {
                newLength = i+1;
                break;
            }
        }
        
        if(nArrayLength != newLength){
            byte[] _newBytesInFile = new byte[newLength];
            
            for(int i = 0; i < newLength; i++){
                _newBytesInFile[i] = _bytesInFile[i];
            }
            
            _bytesInFile = _newBytesInFile;
        }
    }
    
public static void cleanSpacesInFile(){
        
        int nArrayLength = _bytesInFile.length;
        int newLength = nArrayLength;
        byte[] _copyBytesInFile = new byte[nArrayLength];
        
        boolean bQuotationFound = false;
        
        //32 Espacio
        //34 "
        
        //119 - w
        //114 - r
        //105 - i
        //116 - t
        //101 - e
        
        //108 - l
        //110 - n
        
        //114 - r
        //101 - e
        //97  - a
        //100 - d
        
        //35 - #
        //105 - i
        //110 - n
        //116 - t
        
        //35 - #
        //99 - c
        //104 - h
        //97 - a
        //114 - r
        
        //Eliminar de comentarios
        int j = 0;
        
        boolean bVieneDeInstruccion = false;
        for(int i = 0; i < nArrayLength; i++){
            
            boolean bAgregar = true;
            
            if(_bytesInFile[i] == 34){
                bQuotationFound = !bQuotationFound;
            }
            
            if(_bytesInFile[i] == 32 && !bVieneDeInstruccion){
                if(!bQuotationFound){
                    //No es cadena

                    bAgregar = false;
                    
                    if(_bytesInFile[i+1]==65){
                        //SI es A
                        if(_bytesInFile[i+2]==78 && _bytesInFile[i+3]==68){ // Es R
                            bAgregar = true;
                        }
                    }
                    
                    if(_bytesInFile[i+1]==79){
                        //SI es R
                        if(_bytesInFile[i+2]==82){ // Es O
                            bAgregar = true;
                        }
                    }
                    
                    if(_bytesInFile[i-1]==82){
                        //SI es R
                        if(_bytesInFile[i-2]==79){ // Es O
                            bAgregar = true;
                        }
                    }
                    
                    if(_bytesInFile[i-1]==68){
                        //SI es D
                        if(_bytesInFile[i-2]==78 && _bytesInFile[i-3]==65){ // Es NA
                            bAgregar = true;
                        }
                    }
                }
            }
            
            bVieneDeInstruccion = false;
            
            
            
            
            
            if(i>2){
                if(_bytesInFile[i]==100){
                    //Puede ser D
                    if(_bytesInFile[i-1] == 97 && _bytesInFile[i-2] == 101 && _bytesInFile[i-3]==114){
                        //Es READ
                        bVieneDeInstruccion = true;
                    }
                }
                
                if(_bytesInFile[i]==116){
                    //Puede ser T
                    if(_bytesInFile[i-1] == 110 && _bytesInFile[i-2] == 105 && _bytesInFile[i-3]==35){
                        //Es #INT
                        bVieneDeInstruccion = true;
                    }
                }
            }
            
            if(i>3){
                
                if(_bytesInFile[i]==101){
                    //Puede ser E
                    if(_bytesInFile[i-1] == 116 && _bytesInFile[i-2] == 105 && _bytesInFile[i-3]==114 && _bytesInFile[i-4]==119){
                        //Es WRITE
                        bVieneDeInstruccion = true;
                    }
                }
                
                if(_bytesInFile[i]==114){
                    //Puede ser R
                    if(_bytesInFile[i-1] == 97 && _bytesInFile[i-2] == 104 && _bytesInFile[i-3]==99 && _bytesInFile[i-4]==35){
                        //Es #CHAR
                        bVieneDeInstruccion = true;
                    }
                }
            }
            
            if(i>4){
                if(_bytesInFile[i]==116){
                    //Puede ser T
                    if(_bytesInFile[i-1] == 97 && _bytesInFile[i-2] == 111 && _bytesInFile[i-3] == 108 && _bytesInFile[i-4] == 102 && _bytesInFile[i-5]==35){
                        //Es #FLOAT
                        bVieneDeInstruccion = true;
                    }
                }
            }
            
            if(i>5){
                if(_bytesInFile[i]==110){
                    //Puede ser N
                    if(_bytesInFile[i-1] == 108 && _bytesInFile[i-2] == 101 && _bytesInFile[i-3] == 116 && _bytesInFile[i-4] == 105 && _bytesInFile[i-5]==114 && _bytesInFile[i-6]==119){
                        //Es WRITELN
                        bVieneDeInstruccion = true;
                    }
                }
                
                if(_bytesInFile[i]==101){
                    //Puede ser E
                    if(_bytesInFile[i-1] == 108 && _bytesInFile[i-2] == 98 && _bytesInFile[i-3] == 117 && _bytesInFile[i-4] == 111 && _bytesInFile[i-5]==100 && _bytesInFile[i-6]==35){
                        //Es #double
                        bVieneDeInstruccion = true;
                    }
                }
                
                if(_bytesInFile[i]==103){
                    //Puede ser G
                    if(_bytesInFile[i-1] == 110 && _bytesInFile[i-2] == 105 && _bytesInFile[i-3] == 114 && _bytesInFile[i-4] == 116 && _bytesInFile[i-5]==115 && _bytesInFile[i-6]==35){
                        //Es #string
                        bVieneDeInstruccion = true;
                    }
                }
            }
            
            if(bAgregar){
                _copyBytesInFile[j] = _bytesInFile[i];
                j++;
            }
            
        }
        
        _bytesInFile = _copyBytesInFile;
        
        //Eliminar ultimos saltos de linea y enter
                for(int i = nArrayLength-1; i > 0; i--){
                    if(_bytesInFile[i] == 10 || _bytesInFile[i] == 13 || _bytesInFile[i] == 32 || _bytesInFile[i] == 9 || _bytesInFile[i] == 0){
                        
                    } else {
                        newLength = i+1;
                        break;
                    }
                }
        
        if(nArrayLength != newLength){
            byte[] _newBytesInFile = new byte[newLength];
            
            for(int i = 0; i < newLength; i++){
                _newBytesInFile[i] = _bytesInFile[i];
            }
            
            _bytesInFile = _newBytesInFile;
        }
    }

public static void addParentesisToDivisionHierarchy(){
    
    int nArrayLength = _bytesInFile.length;
    int newLength = nArrayLength * 2;
    byte[] _copyBytesInFile = new byte[newLength];
    byte[] _copyBytesInFile2 = new byte[newLength];

    List<Integer> listAddedParentesis = new ArrayList<Integer>();
    
    boolean bQuotationFound = false;
    
    // 40 Abrir parentesis
    // 41 Cerrar parentesis
    // 47 Division
    // 48 - 57 Numeros
    
    //Eliminar de comentarios
    int j = 0;
    
    boolean bVieneDeNumero = false;
    boolean bAgregoParentesis = false;
    int nAdded = 0;
    int nInitialDir = -1;
    int nFinalDir = -1;
    
    
    for(int i = 0; i < nArrayLength; i++){
        
        boolean bPuedeAgregarParentesis = true;
        boolean bUltimo = true;
        
        if(_bytesInFile[i] == 34){
            bQuotationFound = !bQuotationFound;
        }
        
        if((_bytesInFile[i] >= 48 && _bytesInFile[i] <= 57) || _bytesInFile[i] == 46){
            bVieneDeNumero = true;
        } else {
            //No es numero
            if(_bytesInFile[i] == 47){
                //Si topa una division
                if(bVieneDeNumero&&((_bytesInFile[i+1] >= 48 && _bytesInFile[i+1] <= 57)||_bytesInFile[i+1] == 46)){
                    bAgregoParentesis = true;
                    //Agregar hasta que no sea numero para atras
                    int k = 0;
                    
                    int direccion = 0;
                    int direccionActual = j;
                    

                    for(k=j-1 ; k>0 ; k--){
                        if((_bytesInFile[k] < 48 || _bytesInFile[k] > 57) && _bytesInFile[k] != 46){
                            //Aqui acaba 
                            direccion=k;
                            break;
                        }
                    }
                    
                    nInitialDir = direccion+1;
                    
                    direccion++;
                    
                }
            } else {
                //No es numero ni division
                if(bAgregoParentesis && bVieneDeNumero){
                	nFinalDir = j;
                	nInitialDir = nInitialDir+(2*nAdded);
                	nFinalDir = nFinalDir+(2*nAdded)+1;
                	
                	listAddedParentesis.add(nInitialDir);
                	listAddedParentesis.add(nFinalDir);
                	
                	
                    bAgregoParentesis = false;
                    nAdded++;
                }
            }
            
            bVieneDeNumero = false;
        }
        
        byte miByte = _bytesInFile[i];
        _copyBytesInFile[j] = miByte;
        j++;
        
    }
    
    int nSize = listAddedParentesis.size();
    
    for(int i = 0; i < nSize; i++){
    	
    	int n1 = listAddedParentesis.get(i);
    	i++;
    	int n2 = listAddedParentesis.get(i);
    	
    	int n = 0;
    	int limite = _copyBytesInFile.length-1;
   
    			
    	for(n = limite; n > n1; n--){
    		_copyBytesInFile[n] = _copyBytesInFile[n-1];
    	}

    	_copyBytesInFile[n1]=40;
    	
    	for(n = limite; n > n2; n--){
    		_copyBytesInFile[n] = _copyBytesInFile[n-1];
    	}
    	

    	_copyBytesInFile[n2]=41;
    	
    }
    
    _bytesInFile = _copyBytesInFile;
    
    
}

}